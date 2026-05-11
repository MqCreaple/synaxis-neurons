package com.creaple.synaxis_neurons;

import com.creaple.synaxis_neurons.cimulink.VectorConstantKinds;
import com.creaple.synaxis_neurons.cimulink.component.VectorConstantComponent;
import com.creaple.synaxis_neurons.cimulink.component.VectorConstantConfig;
import com.creaple.synaxis_neurons.registry.NeuronsBlockEntities;
import com.creaple.synaxis_neurons.registry.NeuronsBlocks;
import com.creaple.synaxis_neurons.registry.NeuronsCreativeTabs;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.verr1.synaxis.foundation.cimulink.game.circuit.ComponentConfigCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mod(SynaxisNeurons.MODID)
public class SynaxisNeurons {
    public static final String MODID = "synaxis_neurons";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    private static final NonNullSupplier<CreateRegistrate> REGISTRATE =
            NonNullSupplier.lazy(() -> CreateRegistrate.create(MODID));

    public SynaxisNeurons(IEventBus modEventBus, ModContainer modContainer) {
        NeuronsCreativeTabs.register(modEventBus);
        modEventBus.addListener(NeuronsCreativeTabs::addItems);
        getRegistrate().setCreativeTab(NeuronsCreativeTabs.NEURONS);

        NeuronsBlocks.register();
        NeuronsBlockEntities.register();

        getRegistrate().registerEventListeners(modEventBus);

        // Register config codec so VectorConstantConfig can be serialised over the network
        ComponentConfigCodecs.register(
                VectorConstantComponent.ID,
                VectorConstantConfig.CODEC,
                () -> new VectorConstantConfig(List.of(1.0, 2.0, 3.0))
        );

        LOGGER.info("Synaxis Neurons initialized");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static CreateRegistrate getRegistrate() {
        return REGISTRATE.get();
    }
}
