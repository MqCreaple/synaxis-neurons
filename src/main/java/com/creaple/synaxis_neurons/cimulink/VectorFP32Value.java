package com.creaple.synaxis_neurons.cimulink;

import com.verr1.synaxis.foundation.cimulink.core.signal.SignalKind;

import java.util.Arrays;

/**
 * A signal value carrying a {@code float[]} (VECTOR_FP32).
 * <p>
 * This does NOT implement the sealed {@code com.verr1.synaxis.foundation.cimulink.core.signal.SignalValue}
 * interface (it is sealed in Synaxis and cannot be extended from outside).
 * VECTOR_FP32-aware components handle the float[] data directly and use
 * {@link SignalKind} only as a type-discriminator.
 */
public record VectorFP32Value(float[] data) {

    public VectorFP32Value {
        data = data == null ? new float[0] : data;
    }

    public SignalKind kind() {
        return VectorFP32SignalType.kind();
    }

    public int dimensions() {
        return data.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VectorFP32Value that)) return false;
        return Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return "VectorFP32Value{data=" + Arrays.toString(data) + "}";
    }
}
