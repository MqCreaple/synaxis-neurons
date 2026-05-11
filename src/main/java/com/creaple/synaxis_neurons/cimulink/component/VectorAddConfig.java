package com.creaple.synaxis_neurons.cimulink.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.verr1.synaxis.foundation.cimulink.core.component.ComponentConfig;
import com.verr1.synaxis.foundation.cimulink.core.component.builtin.BoundedFamilySupport;

import java.util.List;

public record VectorAddConfig(
        int inputCount,
        List<Double> weights
) implements ComponentConfig {

    private static final int MAX_INPUTS = 6;

    public static final Codec<VectorAddConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("input_count", 2).forGetter(VectorAddConfig::inputCount),
                    Codec.DOUBLE.listOf().optionalFieldOf("weights", List.of(1.0, 1.0))
                            .forGetter(VectorAddConfig::weights)
            ).apply(instance, VectorAddConfig::new)
    );

    public VectorAddConfig {
        inputCount = BoundedFamilySupport.clampInputCount(inputCount, 1, MAX_INPUTS);
        weights = BoundedFamilySupport.sanitizeWeights(weights, inputCount, 1.0);
    }

    public double weight(int index) {
        if (index < 0 || index >= weights.size()) return 1.0;
        Double w = weights.get(index);
        return w != null && Double.isFinite(w) ? w : 1.0;
    }
}
