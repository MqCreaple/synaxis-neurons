package com.creaple.synaxis_neurons.mixin.cimulink;

import com.creaple.synaxis_neurons.cimulink.VectorFP32SignalType;
import com.verr1.synaxis.foundation.cimulink.core.signal.SignalKind;
import com.verr1.synaxis.foundation.cimulink.core.signal.SignalType;
import com.verr1.synaxis.foundation.cimulink.core.signal.SignalValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/**
 * Patches {@link SignalType} to handle the standalone VECTOR_FP32 {@link SignalKind}
 * instance created by {@link VectorFP32SignalType}.
 * <p>
 * VECTOR_FP32 signals carry {@code float[]} data. Since the sealed {@link SignalValue}
 * interface cannot be extended, this mixin:
 * <ul>
 *   <li>Returns an empty {@link SignalValue.Bundle} as placeholder from {@code defaultValue()}</li>
 *   <li>Accepts placeholder Bundles in {@code accepts()}</li>
 * </ul>
 */
@Mixin(SignalType.class)
public abstract class SignalTypeMixin {

    /** Our standalone VECTOR_FP32 SignalKind, cached once. */
    private static SignalKind fp32Kind;

    private static SignalKind fp32() {
        if (fp32Kind == null) {
            fp32Kind = VectorFP32SignalType.kind();
        }
        return fp32Kind;
    }

    @Inject(method = "defaultValue", at = @At("HEAD"), cancellable = true)
    private void synaxisNeurons$onDefaultValue(CallbackInfoReturnable<SignalValue> cir) {
        if (((SignalType) (Object) this).kind() == fp32()) {
            // VECTOR_FP32 has no meaningful default SignalValue. Return empty Bundle as
            // placeholder; real float[] data is managed by the component at evaluate() time.
            cir.setReturnValue(new SignalValue.Bundle(Map.of()));
        }
    }

    @Inject(method = "accepts", at = @At("HEAD"), cancellable = true)
    private void synaxisNeurons$onAccepts(SignalValue value, CallbackInfoReturnable<Boolean> cir) {
        if (((SignalType) (Object) this).kind() == fp32()) {
            cir.setReturnValue(value != null
                && (value.kind() == fp32()
                    || value instanceof SignalValue.Bundle));
        }
    }
}
