package com.creaple.synaxis_neurons.cimulink.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.verr1.synaxis.foundation.cimulink.core.component.ComponentConfig;

import java.util.Arrays;
import java.util.List;

public record VectorConstantConfig(double[] elements) implements ComponentConfig {

    public static final Codec<VectorConstantConfig> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.listOf()
                            .optionalFieldOf("elements", List.of(1.0, 2.0, 3.0))
                            .forGetter(cfg -> Arrays.stream(cfg.elements).boxed().toList())
            ).apply(instance, list -> new VectorConstantConfig(list.stream().mapToDouble(d -> d).toArray()))
    );

    public VectorConstantConfig(List<Double> list) {
        this(list.stream().mapToDouble(d -> d).toArray());
    }

    public VectorConstantConfig {
        elements = elements == null ? new double[0] : elements;
    }

    public int dimensions() {
        return elements.length;
    }
}
