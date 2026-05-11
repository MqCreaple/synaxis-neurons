package com.creaple.synaxis_neurons.cimulink.component;

import com.creaple.synaxis_neurons.cimulink.VectorFP32SignalType;
import com.verr1.synaxis.foundation.cimulink.core.component.ComponentConfig;
import com.verr1.synaxis.foundation.cimulink.core.component.ComponentMemory;
import com.verr1.synaxis.foundation.cimulink.core.component.ComponentSchema;
import com.verr1.synaxis.foundation.cimulink.core.component.ComponentSemantics;
import com.verr1.synaxis.foundation.cimulink.core.component.ComponentType;
import com.verr1.synaxis.foundation.cimulink.core.component.ComponentTypeId;
import com.verr1.synaxis.foundation.cimulink.core.component.EmptyComponentMemory;
import com.verr1.synaxis.foundation.cimulink.core.component.EvalContext;
import com.verr1.synaxis.foundation.cimulink.core.signal.OutputPort;
import com.verr1.synaxis.foundation.cimulink.core.signal.PortDef;
import com.verr1.synaxis.foundation.cimulink.core.signal.SignalReader;
import com.verr1.synaxis.foundation.cimulink.core.signal.SignalValue;
import com.verr1.synaxis.foundation.cimulink.core.signal.SignalWriter;

import java.util.LinkedHashMap;
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
        // Encode double[] as a Bundle of SignalValue.Real entries
        LinkedHashMap<String, SignalValue> fields = new LinkedHashMap<>();
        double[] elements = cfg.elements();
        for (int i = 0; i < elements.length; i++) {
            fields.put("_" + i, new SignalValue.Real(elements[i]));
        }
        out.write(OUT, new SignalValue.Bundle(fields));
    }

    private static VectorConstantConfig config(ComponentConfig config) {
        return config instanceof VectorConstantConfig v ? v : new VectorConstantConfig(List.of(1.0, 2.0, 3.0));
    }
}
