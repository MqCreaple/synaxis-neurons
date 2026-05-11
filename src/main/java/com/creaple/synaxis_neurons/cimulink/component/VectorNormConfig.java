package com.creaple.synaxis_neurons.cimulink.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.verr1.synaxis.foundation.cimulink.core.component.ComponentConfig;

public record VectorNormConfig(
        NormMode mode,
        boolean squared
) implements ComponentConfig {

    public static final Codec<VectorNormConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.xmap(NormMode::valueOf, NormMode::name)
                            .optionalFieldOf("mode", NormMode.L2)
                            .forGetter(VectorNormConfig::mode),
                    Codec.BOOL.optionalFieldOf("squared", false)
                            .forGetter(VectorNormConfig::squared)
            ).apply(instance, (m, s) -> new VectorNormConfig(m, s && m == NormMode.L2))
    );

    public VectorNormConfig {
        mode = mode == null ? NormMode.L2 : mode;
        // squared only meaningful for L2
        if (mode != NormMode.L2) squared = false;
    }

    public double compute(double[] values) {
        switch (mode) {
            case L1 -> {
                double sum = 0;
                for (double v : values) sum += Math.abs(v);
                return sum;
            }
            case L2 -> {
                double sum = 0;
                for (double v : values) sum += v * v;
                return squared ? sum : Math.sqrt(sum);
            }
        }
        return 0;
    }

    public enum NormMode {
        L1,
        L2
    }
}
