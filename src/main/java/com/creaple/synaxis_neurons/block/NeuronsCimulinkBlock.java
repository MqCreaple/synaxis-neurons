package com.creaple.synaxis_neurons.block;

import com.creaple.synaxis_neurons.registry.NeuronsBlockEntities;
import com.verr1.synaxis.content.blocks.cimulink.CimulinkEndpointBlock;
import com.verr1.synaxis.content.blocks.cimulink.CimulinkEndpointBlockEntity;
import com.verr1.synaxis.content.blocks.cimulink.CimulinkEndpointBlockKind;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class NeuronsCimulinkBlock extends CimulinkEndpointBlock {

    public NeuronsCimulinkBlock(BlockBehaviour.Properties properties, CimulinkEndpointBlockKind kind) {
        super(properties, kind);
    }

    @Override
    public BlockEntityType<? extends CimulinkEndpointBlockEntity> getBlockEntityType() {
        return NeuronsBlockEntities.CIMULINK_ENDPOINT_NEURONS.get();
    }
}
