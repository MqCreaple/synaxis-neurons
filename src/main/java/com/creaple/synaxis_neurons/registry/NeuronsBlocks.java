package com.creaple.synaxis_neurons.registry;

import com.creaple.synaxis_neurons.SynaxisNeurons;
import com.creaple.synaxis_neurons.block.NeuronsCimulinkBlock;
import com.creaple.synaxis_neurons.cimulink.VectorAddKinds;
import com.creaple.synaxis_neurons.cimulink.VectorConstantKinds;
import com.creaple.synaxis_neurons.cimulink.VectorNormKinds;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.ModelGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.verr1.synaxis.content.blocks.cimulink.CimulinkEndpointBlockKind;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class NeuronsBlocks {

    public static final BlockEntry<NeuronsCimulinkBlock> VECTOR_CONSTANT =
            registerCimulink(VectorConstantKinds.VECTOR_CONSTANT);
    public static final BlockEntry<NeuronsCimulinkBlock> VECTOR_NORM =
            registerCimulink(VectorNormKinds.VECTOR_NORM);
    public static final BlockEntry<NeuronsCimulinkBlock> VECTOR_ADD =
            registerCimulink(VectorAddKinds.VECTOR_ADD);

    private NeuronsBlocks() {
    }

    public static void register() {
        // triggers static initialiser
    }

    private static BlockEntry<NeuronsCimulinkBlock> registerCimulink(CimulinkEndpointBlockKind kind) {
        return ((BlockBuilder<NeuronsCimulinkBlock, ?>) SynaxisNeurons.getRegistrate()
                .block(kind.id(),
                        properties -> new NeuronsCimulinkBlock(
                                (BlockBehaviour.Properties) properties,
                                kind))
                .initialProperties(SharedProperties::stone)
                .properties(p -> p.explosionResistance(64.0f))
                .properties(p -> p.noOcclusion())
                .transform(TagGen.pickaxeOnly())
                .blockstate(BlockStateGen.directionalBlockProvider(true))
                .item()
                .transform(ModelGen.customItemModel()))
                .lang(kind.displayName())
                .register();
    }
}
