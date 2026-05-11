package com.creaple.synaxis_neurons.registry;

import com.creaple.synaxis_neurons.SynaxisNeurons;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.verr1.synaxis.content.blocks.cimulink.CimulinkEndpointBlockEntity;

public final class NeuronsBlockEntities {

    public static final BlockEntityEntry<CimulinkEndpointBlockEntity> CIMULINK_ENDPOINT_NEURONS =
            SynaxisNeurons.getRegistrate()
                    .blockEntity("cimulink_endpoint_neurons", CimulinkEndpointBlockEntity::new)
                    .validBlocks(new NonNullSupplier[]{
                            NeuronsBlocks.VECTOR_CONSTANT,
                            NeuronsBlocks.VECTOR_NORM,
                            NeuronsBlocks.VECTOR_ADD
                    })
                    .register();

    private NeuronsBlockEntities() {
    }

    public static void register() {
        // triggers static initialiser
    }
}
