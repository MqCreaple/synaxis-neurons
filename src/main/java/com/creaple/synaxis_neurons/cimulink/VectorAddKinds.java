package com.creaple.synaxis_neurons.cimulink;

import com.creaple.synaxis_neurons.SynaxisNeurons;
import com.creaple.synaxis_neurons.cimulink.component.VectorAddComponent;
import com.creaple.synaxis_neurons.cimulink.component.VectorAddConfig;
import com.verr1.synaxis.content.blocks.cimulink.CimulinkEndpointBlockEntity;
import com.verr1.synaxis.content.blocks.cimulink.CimulinkEndpointBlockKind;
import com.verr1.synaxis.content.blocks.cimulink.kinds.support.CimulinkKindSupport;
import com.verr1.synaxis.foundation.command.CommandKey;
import com.verr1.synaxis.foundation.command.CommandRegistry;
import com.verr1.synaxis.foundation.state.StateKey;
import com.verr1.synaxis.foundation.state.StateSchema;
import com.verr1.synaxis.foundation.state.SynaxisCodecs;
import com.verr1.synaxis.foundation.state.SyncTarget;
import com.verr1.synaxis.foundation.ui.SynaxisUiText;
import com.verr1.synaxis.foundation.ui.UiSchema;
import com.verr1.synaxis.foundation.ui.Unit;
import com.verr1.synaxis.foundation.ui.ldlib.LdLibUiBehaviors;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public final class VectorAddKinds {

    private static final int MAX_INPUTS = 6;

    // ── input count ──

    public static final StateKey<Integer> INPUT_COUNT =
            StateKey.integer(SynaxisNeurons.id("vector_add/input_count"), 2);
    public static final CommandKey<Integer> SET_INPUT_COUNT =
            CommandKey.fromState(SynaxisNeurons.id("vector_add/set_input_count"), INPUT_COUNT);

    // ── weights (fixed pool of 6, shown/hidden by input count) ──

    public static final StateKey<Double> WEIGHT_0 =
            StateKey.doubleKey(SynaxisNeurons.id("vector_add/weight_0"), 1.0);
    public static final StateKey<Double> WEIGHT_1 =
            StateKey.doubleKey(SynaxisNeurons.id("vector_add/weight_1"), 1.0);
    public static final StateKey<Double> WEIGHT_2 =
            StateKey.doubleKey(SynaxisNeurons.id("vector_add/weight_2"), 1.0);
    public static final StateKey<Double> WEIGHT_3 =
            StateKey.doubleKey(SynaxisNeurons.id("vector_add/weight_3"), 1.0);
    public static final StateKey<Double> WEIGHT_4 =
            StateKey.doubleKey(SynaxisNeurons.id("vector_add/weight_4"), 1.0);
    public static final StateKey<Double> WEIGHT_5 =
            StateKey.doubleKey(SynaxisNeurons.id("vector_add/weight_5"), 1.0);

    public static final CommandKey<Double> SET_WEIGHT_0 =
            CommandKey.fromState(SynaxisNeurons.id("vector_add/set_weight_0"), WEIGHT_0);
    public static final CommandKey<Double> SET_WEIGHT_1 =
            CommandKey.fromState(SynaxisNeurons.id("vector_add/set_weight_1"), WEIGHT_1);
    public static final CommandKey<Double> SET_WEIGHT_2 =
            CommandKey.fromState(SynaxisNeurons.id("vector_add/set_weight_2"), WEIGHT_2);
    public static final CommandKey<Double> SET_WEIGHT_3 =
            CommandKey.fromState(SynaxisNeurons.id("vector_add/set_weight_3"), WEIGHT_3);
    public static final CommandKey<Double> SET_WEIGHT_4 =
            CommandKey.fromState(SynaxisNeurons.id("vector_add/set_weight_4"), WEIGHT_4);
    public static final CommandKey<Double> SET_WEIGHT_5 =
            CommandKey.fromState(SynaxisNeurons.id("vector_add/set_weight_5"), WEIGHT_5);

    private static final List<StateKey<Double>> WEIGHTS =
            List.of(WEIGHT_0, WEIGHT_1, WEIGHT_2, WEIGHT_3, WEIGHT_4, WEIGHT_5);
    private static final List<CommandKey<Double>> SET_WEIGHTS =
            List.of(SET_WEIGHT_0, SET_WEIGHT_1, SET_WEIGHT_2, SET_WEIGHT_3, SET_WEIGHT_4, SET_WEIGHT_5);

    // ── add / remove input actions ──

    public static final CommandKey<Unit> ADD_INPUT =
            CommandKey.of(SynaxisNeurons.id("vector_add/add_input"), SynaxisCodecs.UNIT);
    public static final CommandKey<Unit> REMOVE_INPUT =
            CommandKey.of(SynaxisNeurons.id("vector_add/remove_input"), SynaxisCodecs.UNIT);

    // ── the kind ──

    public static final CimulinkEndpointBlockKind VECTOR_ADD =
            CimulinkKindSupport.configurable(
                    "vector_add",
                    "Vector Add",
                    CimulinkKindSupport.minecraftTexture("block/smooth_stone"),
                    VectorAddComponent.ID,
                    VectorAddKinds::configFactory,
                    VectorAddKinds::defineState,
                    VectorAddKinds::defineCommands,
                    VectorAddKinds::defineUi
            );

    private VectorAddKinds() {
    }

    // ── config factory ──

    private static VectorAddConfig configFactory(CimulinkEndpointBlockEntity entity) {
        return new VectorAddConfig(
                entity.getState(INPUT_COUNT),
                WEIGHTS.stream().map(entity::getState).toList()
        );
    }

    // ── state ──

    private static void defineState(CimulinkEndpointBlockEntity entity, StateSchema.Builder builder) {
        entity.bindConfig(
                builder.field(INPUT_COUNT).persistent().editable().sync(SyncTarget.UI)
                        .validate(v -> v != null && v >= 1 && v <= MAX_INPUTS),
                INPUT_COUNT);
        for (StateKey<Double> weight : WEIGHTS) {
            entity.bindConfig(
                    builder.field(weight).persistent().editable().sync(SyncTarget.UI)
                            .validate(v -> v != null && Double.isFinite(v)),
                    weight);
        }
    }

    // ── commands ──

    private static void defineCommands(CimulinkEndpointBlockEntity entity, CommandRegistry commands) {
        commands.registerSetField(SET_INPUT_COUNT, INPUT_COUNT);
        for (int i = 0; i < WEIGHTS.size(); i++) {
            commands.registerSetField(SET_WEIGHTS.get(i), WEIGHTS.get(i));
        }
        commands.register(ADD_INPUT, (ctx, ignored) -> {
            ctx.requireEditable(INPUT_COUNT);
            int current = ctx.blockEntity().state().get(INPUT_COUNT);
            ctx.blockEntity().state().set(INPUT_COUNT, Math.min(MAX_INPUTS, current + 1));
        });
        commands.register(REMOVE_INPUT, (ctx, ignored) -> {
            ctx.requireEditable(INPUT_COUNT);
            int current = ctx.blockEntity().state().get(INPUT_COUNT);
            ctx.blockEntity().state().set(INPUT_COUNT, Math.max(1, current - 1));
        });
    }

    // ── UI ──

    private static void defineUi(CimulinkEndpointBlockEntity entity, UiSchema.Builder ui) {
        // Weights page
        ui.page("weights", SynaxisUiText.text("cimulink.weighted_sum.page.weights"))
                .group("weights", SynaxisUiText.text("cimulink.weighted_sum.group.weights"));
        for (int i = 0; i < WEIGHTS.size(); i++) {
            ui.number(WEIGHTS.get(i))
                    .id("weight_" + i)
                    .label(Component.literal("W" + i))
                    .format("%.3f")
                    .command(SET_WEIGHTS.get(i));
        }

        // Ports page
        ui.page("ports", SynaxisUiText.text("cimulink.common.page.ports"))
                .group("inputs", SynaxisUiText.text("cimulink.common.group.inputs"));
        ui.number(INPUT_COUNT)
                .id("input_count")
                .label(SynaxisUiText.text("cimulink.common.field.inputs"))
                .range(1.0, 6.0)
                .command(SET_INPUT_COUNT);
        ui.action("add_input")
                .label(SynaxisUiText.text("cimulink.common.action.add_input"))
                .command(ADD_INPUT);
        ui.action("remove_input")
                .label(SynaxisUiText.text("cimulink.common.action.remove_input"))
                .command(REMOVE_INPUT);

        // Conditional visibility for weights based on input count
        for (int i = 0; i < WEIGHTS.size(); i++) {
            ui.behavior(LdLibUiBehaviors.showWhenAtLeast(INPUT_COUNT, i + 1, "weight_" + i));
        }
        ui.behavior(LdLibUiBehaviors.setGap(2.0f, "weight_0", "weight_1", "weight_2", "weight_3", "weight_4", "weight_5"));

        // No error label — error is exposed via the HAS_ERROR signal output port
        // for downstream consumers to read at the signal level.
    }
}
