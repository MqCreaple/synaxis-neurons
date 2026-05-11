package com.creaple.synaxis_neurons.cimulink.component;

import com.creaple.synaxis_neurons.cimulink.signal.VectorFP32SignalHelper;
import com.creaple.synaxis_neurons.cimulink.signal.VectorFP32SignalType;
import com.creaple.synaxis_neurons.cimulink.signal.VectorFP32Value;
import com.verr1.synaxis.foundation.cimulink.core.component.*;
import com.verr1.synaxis.foundation.cimulink.core.component.builtin.BoundedFamilySupport;
import com.verr1.synaxis.foundation.cimulink.core.signal.*;

import java.util.ArrayList;
import java.util.List;

public final class VectorAddComponent implements ComponentType {

    public static final ComponentTypeId ID = ComponentTypeId.of("synaxis_neurons:vector_add");
    public static final OutputPort OUT = new OutputPort("out");
    public static final OutputPort HAS_ERROR = new OutputPort("has_error");

    private static final int MIN_INPUTS = 1;
    private static final int MAX_INPUTS = 6;

    @Override
    public ComponentTypeId id() {
        return ID;
    }

    @Override
    public ComponentSchema schema(ComponentConfig config) {
        VectorAddConfig cfg = config(config);
        int count = BoundedFamilySupport.clampInputCount(cfg.inputCount(), MIN_INPUTS, MAX_INPUTS);
        List<PortDef> inputs = BoundedFamilySupport.indexedInputs(count, VectorFP32SignalType.type());
        return ComponentSchema.of(inputs, List.of(
                PortDef.output(OUT.name(), VectorFP32SignalType.type()),
                PortDef.output(HAS_ERROR.name(), SignalType.BOOLEAN)
        ));
    }

    @Override
    public ComponentConfig defaultConfig() {
        return new VectorAddConfig(2, List.of(1.0, 1.0));
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
        VectorAddConfig cfg = config(config);
        int count = BoundedFamilySupport.clampInputCount(cfg.inputCount(), MIN_INPUTS, MAX_INPUTS);

        // Read all inputs as VectorFP32Values
        List<VectorFP32Value> inputs = new ArrayList<>(count);
        int maxDim = 0;
        for (int i = 0; i < count; i++) {
            InputPort port = new InputPort(BoundedFamilySupport.inputName(i));
            VectorFP32Value vec = VectorFP32SignalHelper.read(in.read(port));
            inputs.add(vec);
            if (vec.dimensions() > maxDim) maxDim = vec.dimensions();
        }

        // Check dimension consistency: any input with non-zero dim must match maxDim
        boolean hasError = false;
        for (int i = 0; i < count; i++) {
            int dim = inputs.get(i).dimensions();
            if (dim > 0 && dim != maxDim) {
                hasError = true;
                break;
            }
        }

        if (hasError) {
            // Output zero-dimension vector + error flag
            out.write(OUT, VectorFP32SignalHelper.write(new VectorFP32Value(new float[0])));
            out.write(HAS_ERROR, new SignalValue.Bool(true));
        } else {
            // Compute weighted sum element-wise, padding shorter inputs with 0
            float[] result = new float[maxDim];
            for (int i = 0; i < count; i++) {
                float[] data = inputs.get(i).data();
                double weight = cfg.weight(i);
                for (int j = 0; j < maxDim; j++) {
                    result[j] += (j < data.length ? data[j] : 0.0f) * (float) weight;
                }
            }
            out.write(OUT, VectorFP32SignalHelper.write(new VectorFP32Value(result)));
            out.write(HAS_ERROR, new SignalValue.Bool(false));
        }
    }

    private static VectorAddConfig config(ComponentConfig config) {
        return config instanceof VectorAddConfig c
                ? c
                : new VectorAddConfig(2, List.of(1.0, 1.0));
    }
}
