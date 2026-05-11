package com.creaple.synaxis_neurons.cimulink.component;

import com.creaple.synaxis_neurons.cimulink.signal.VectorFP32SignalHelper;
import com.creaple.synaxis_neurons.cimulink.signal.VectorFP32SignalType;
import com.creaple.synaxis_neurons.cimulink.signal.VectorFP32Value;
import com.verr1.synaxis.foundation.cimulink.core.component.*;
import com.verr1.synaxis.foundation.cimulink.core.signal.*;

import java.util.List;

public final class VectorNormComponent implements ComponentType {

    public static final ComponentTypeId ID = ComponentTypeId.of("synaxis_neurons:vector_norm");
    public static final InputPort IN = new InputPort("in");
    public static final OutputPort OUT = new OutputPort("out");
    private static final ComponentSchema SCHEMA = ComponentSchema.of(
            List.of(PortDef.input(IN.name(), VectorFP32SignalType.type())),
            List.of(PortDef.output(OUT.name(), SignalType.REAL))
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
        return new VectorNormConfig(VectorNormConfig.NormMode.L2, false);
    }

    @Override
    public ComponentMemory createMemory(ComponentConfig config) {
        return EmptyComponentMemory.INSTANCE;
    }

    @Override
    public ComponentSemantics semantics(ComponentConfig config) {
        return ComponentSemantics.pureCombinational(schema(config));
    }

    @Override
    public void evaluate(EvalContext ctx, ComponentConfig config, ComponentMemory memory, SignalReader in, SignalWriter out) {
        VectorNormConfig normConfig = config(config);
        VectorFP32Value vec = VectorFP32SignalHelper.read(in.read(IN));
        float[] data = vec.data();
        double[] doubleData = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            doubleData[i] = data[i];
        }
        out.real(OUT, normConfig.compute(doubleData));
    }

    private static VectorNormConfig config(ComponentConfig config) {
        return config instanceof VectorNormConfig v ? v : new VectorNormConfig(VectorNormConfig.NormMode.L2, false);
    }
}
