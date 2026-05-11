package com.creaple.synaxis_neurons.cimulink.signal;

import com.creaple.synaxis_neurons.cimulink.signal.VectorFP32Value;
import com.verr1.synaxis.foundation.cimulink.core.signal.SignalValue;

import java.util.LinkedHashMap;

/**
 * Bridges {@link VectorFP32Value} (plain record holding {@code float[]})
 * and the sealed {@link SignalValue} type system.
 * <p>
 * Internally converts to/from {@link SignalValue.Bundle} of {@link SignalValue.Real}
 * entries for transport through the signal engine. This is a mechanical conversion
 * that avoids polluting component evaluate() methods with Bundle plumbing.
 * <p>
 * TODO: If the JVM permits adding non-sealed subclasses at load time (via a
 * {@code ClassFileTransformer}), this conversion could be eliminated entirely
 * by making VectorFP32Value extend SignalValue directly.
 */
public final class VectorFP32SignalHelper {

    private VectorFP32SignalHelper() {
    }

    /** Unwrap a SignalValue into a VectorFP32Value. Accepts Bundle or plain Real. */
    public static VectorFP32Value read(SignalValue raw) {
        if (raw instanceof SignalValue.Bundle bundle) {
            double[] values = bundle.fields().values().stream()
                    .filter(v -> v instanceof SignalValue.Real)
                    .mapToDouble(v -> ((SignalValue.Real) v).value())
                    .toArray();
            float[] data = new float[values.length];
            for (int i = 0; i < values.length; i++) {
                data[i] = (float) values[i];
            }
            return new VectorFP32Value(data);
        }
        // Fallback: try single Real
        if (raw instanceof SignalValue.Real real) {
            return new VectorFP32Value(new float[]{(float) real.value()});
        }
        return new VectorFP32Value(new float[0]);
    }

    /** Wrap a VectorFP32Value into a Bundle for signal transport. */
    public static SignalValue.Bundle write(VectorFP32Value value) {
        LinkedHashMap<String, SignalValue> fields = new LinkedHashMap<>();
        float[] data = value.data();
        for (int i = 0; i < data.length; i++) {
            fields.put("_" + i, new SignalValue.Real(data[i]));
        }
        return new SignalValue.Bundle(fields);
    }
}
