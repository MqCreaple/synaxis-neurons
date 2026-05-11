package com.creaple.synaxis_neurons.mixin.cimulink;

import com.creaple.synaxis_neurons.cimulink.component.VectorConstantComponent;
import com.verr1.synaxis.foundation.cimulink.core.component.ComponentRegistry;
import com.verr1.synaxis.foundation.cimulink.core.component.builtin.CoreComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Registers {@link VectorConstantComponent} after Synaxis's own component types.
 */
@Mixin(CoreComponentTypes.class)
public abstract class CoreComponentTypesMixin {

    @Inject(method = "registerAll", at = @At("RETURN"), remap = false)
    private static void synaxisNeurons$registerAll(
            ComponentRegistry registry,
            CallbackInfoReturnable<ComponentRegistry> cir
    ) {
        registry.register(new VectorConstantComponent());
    }
}
