package com.creaple.synaxis_neurons.cimulink.component;

import com.creaple.synaxis_neurons.cimulink.signal.VectorFP32SignalHelper;
import com.creaple.synaxis_neurons.cimulink.signal.VectorFP32SignalType;
import com.creaple.synaxis_neurons.cimulink.signal.VectorFP32Value;
import com.verr1.synaxis.foundation.cimulink.core.component.*;
import com.verr1.synaxis.foundation.cimulink.core.signal.*;

import java.util.List;

public final class VectorConstantComponent implements ComponentType {

    public static final ComponentTypeId ID = ComponentTypeId.of("synaxis_neurons:vector_constant");
    public static final OutputPort OUT = new OutputPort("out");
    private static final ComponentSchema SCHEMA = ComponentSchema.of(
            List.of(),
            List.of(PortDef.output(OUT.name(), VectorFP32SignalType.type()))
    );

    @Override
    public ComponentTypeId id() {
        return ID;
    }

    @Override
    public ComponentSchema schema(ComponentConfig config) {
        return SCHEMA;
    }

    @Override
    public ComponentConfig defaultConfig() {
        return new VectorConstantConfig(List.of(1.0, 2.0, 3.0));
    }

    @Override
    public ComponentMemory createMemory(ComponentConfig config) {
        return EmptyComponentMemory.INSTANCE;
    }

    @Override
    public ComponentSemantics semantics(ComponentConfig config) {
        return ComponentSemantics.source(SCHEMA);
    }

    @Override
    public void evaluate(EvalContext ctx, ComponentConfig config,
                         ComponentMemory memory, SignalReader in, SignalWriter out) {
        VectorConstantConfig cfg = config(config);
        double[] elements = cfg.elements();
        float[] data = new float[elements.length];
        for (int i = 0; i < elements.length; i++) {
            data[i] = (float) elements[i];
        }
        out.write(OUT, VectorFP32SignalHelper.write(new VectorFP32Value(data)));
    }

    private static VectorConstantConfig config(ComponentConfig config) {
        return config instanceof VectorConstantConfig v ? v : new VectorConstantConfig(List.of(1.0, 2.0, 3.0));
    }
}
