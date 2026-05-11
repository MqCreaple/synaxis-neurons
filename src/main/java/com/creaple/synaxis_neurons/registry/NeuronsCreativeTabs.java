package com.creaple.synaxis_neurons.registry;

import com.creaple.synaxis_neurons.SynaxisNeurons;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeuronsCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SynaxisNeurons.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> NEURONS =
            REGISTER.register("neurons", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + SynaxisNeurons.MODID + ".neurons"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> NeuronsBlocks.VECTOR_CONSTANT.asStack())
                    .build());

    private NeuronsCreativeTabs() {
    }

    public static void addItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == NEURONS.getKey()) {
            accept(event, NeuronsBlocks.VECTOR_CONSTANT.asStack());
        }
    }

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }

    private static void accept(BuildCreativeModeTabContentsEvent event, ItemStack stack) {
        if (!stack.isEmpty()) {
            event.accept(stack, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
        }
    }
}
