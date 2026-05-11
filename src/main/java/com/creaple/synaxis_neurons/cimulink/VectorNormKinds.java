package com.creaple.synaxis_neurons.cimulink;

import com.creaple.synaxis_neurons.SynaxisNeurons;
import com.creaple.synaxis_neurons.cimulink.component.VectorNormComponent;
import com.creaple.synaxis_neurons.cimulink.component.VectorNormConfig;
import com.mojang.serialization.Codec;
import com.verr1.synaxis.content.blocks.cimulink.CimulinkEndpointBlockEntity;
import com.verr1.synaxis.content.blocks.cimulink.CimulinkEndpointBlockKind;
import com.verr1.synaxis.content.blocks.cimulink.kinds.support.CimulinkKindSupport;
import com.verr1.synaxis.foundation.command.CommandKey;
import com.verr1.synaxis.foundation.command.CommandRegistry;
import com.verr1.synaxis.foundation.state.StateKey;
import com.verr1.synaxis.foundation.state.StateSchema;
import com.verr1.synaxis.foundation.state.SyncTarget;
import com.verr1.synaxis.foundation.ui.SynaxisUiText;
import com.verr1.synaxis.foundation.ui.UiSchema;
import com.verr1.synaxis.foundation.ui.ldlib.LdLibUiBehaviors;
import net.minecraft.network.chat.Component;

public final class VectorNormKinds {

    private static final Codec<VectorNormConfig.NormMode> NORM_MODE_CODEC =
            CimulinkKindSupport.enumCodec(VectorNormConfig.NormMode.class, VectorNormConfig.NormMode.L2);

    public static final StateKey<VectorNormConfig.NormMode> NORM_MODE =
            StateKey.codec(SynaxisNeurons.id("vector_norm/mode"), NORM_MODE_CODEC, VectorNormConfig.NormMode.L2);
    public static final CommandKey<VectorNormConfig.NormMode> SET_NORM_MODE =
            CommandKey.fromState(SynaxisNeurons.id("vector_norm/set_mode"), NORM_MODE);

    public static final StateKey<Boolean> SQUARED =
            StateKey.bool(SynaxisNeurons.id("vector_norm/squared"), false);
    public static final CommandKey<Boolean> SET_SQUARED =
            CommandKey.fromState(SynaxisNeurons.id("vector_norm/set_squared"), SQUARED);

    public static final CimulinkEndpointBlockKind VECTOR_NORM =
            CimulinkKindSupport.configurable(
                    "vector_norm",
                    "Vector Norm",
                    CimulinkKindSupport.minecraftTexture("block/smooth_stone"),
                    VectorNormComponent.ID,
                    VectorNormKinds::configFactory,
                    VectorNormKinds::defineState,
                    VectorNormKinds::defineCommands,
                    VectorNormKinds::defineUi
            );

    private VectorNormKinds() {
    }

    private static VectorNormConfig configFactory(CimulinkEndpointBlockEntity entity) {
        return new VectorNormConfig(
                entity.getState(NORM_MODE),
                entity.getState(SQUARED)
        );
    }

    private static void defineState(CimulinkEndpointBlockEntity entity, StateSchema.Builder builder) {
        entity.bindConfig(
                builder.field(NORM_MODE).persistent().editable().sync(SyncTarget.UI),
                NORM_MODE);
        entity.bindConfig(
                builder.field(SQUARED).persistent().editable().sync(SyncTarget.UI),
                SQUARED);
    }

    private static void defineCommands(CimulinkEndpointBlockEntity entity, CommandRegistry commands) {
        commands.registerSetField(SET_NORM_MODE, NORM_MODE);
        commands.registerSetField(SET_SQUARED, SQUARED);
    }

    private static void defineUi(CimulinkEndpointBlockEntity entity, UiSchema.Builder ui) {
        ui.page("control", SynaxisUiText.text("common.page.control"))
                .group("norm", Component.literal("Norm"))
            .option(NORM_MODE, VectorNormConfig.NormMode.class)
                .id("mode").label(Component.literal("Mode")).command(SET_NORM_MODE)
            .toggle(SQUARED)
                .id("squared").label(Component.literal("Squared")).command(SET_SQUARED);
        ui.behavior(LdLibUiBehaviors.showWhen(NORM_MODE, mode -> mode == VectorNormConfig.NormMode.L2, "squared"));
    }
}
