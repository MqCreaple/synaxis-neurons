package com.creaple.synaxis_neurons.cimulink;

import com.creaple.synaxis_neurons.SynaxisNeurons;
import com.creaple.synaxis_neurons.cimulink.component.VectorConstantComponent;
import com.creaple.synaxis_neurons.cimulink.component.VectorConstantConfig;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Button;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.codeeditor.CodeEditor;
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
import com.verr1.synaxis.foundation.ui.ldlib.LdLibCommandBridge;
import com.verr1.synaxis.foundation.ui.ldlib.LdLibStateBindings;
import com.verr1.synaxis.foundation.ui.ldlib.SynaxisGuiTextures;
import net.minecraft.network.chat.Component;
import com.verr1.synaxis.foundation.state.SynaxisCodecs;

import java.util.List;

public final class VectorConstantKinds {
    public static final int MAX_DIM = 256;

    // ── state keys ──────────────────────────────────────────

    public static final StateKey<String> INPUT_TEXT = StateKey.string(
            SynaxisNeurons.id("vector_constant/input_text"),
            "1.0\n2.0\n3.0");

    // APPLY_VECTOR carries the full text content as payload (String, not Unit).
    // The TextArea content can be large, so we avoid syncing INPUT_TEXT on every
    // keystroke and instead send the text once when the user clicks Apply.
    public static final CommandKey<String> APPLY_VECTOR = CommandKey.of(
            SynaxisNeurons.id("vector_constant/apply"), SynaxisCodecs.STRING);

    public static final StateKey<String> ERROR_TEXT = StateKey.string(
            SynaxisNeurons.id("vector_constant/error_text"), "");

    // ── the kind ────────────────────────────────────────────

    public static final CimulinkEndpointBlockKind VECTOR_CONSTANT =
            CimulinkKindSupport.configurable(
                    "vector_constant",
                    "Vector Constant",
                    CimulinkKindSupport.minecraftTexture("block/smooth_stone"),
                    VectorConstantComponent.ID,
                    VectorConstantKinds::configFactory,
                    VectorConstantKinds::defineState,
                    VectorConstantKinds::defineCommands,
                    VectorConstantKinds::defineUi
            );

    // ── config factory ──────────────────────────────────────

    private static VectorConstantConfig configFactory(CimulinkEndpointBlockEntity entity) {
        String text = entity.getState(INPUT_TEXT);
        if (text == null || text.isBlank()) {
            return new VectorConstantConfig(List.of(1.0, 2.0, 3.0));
        }
        double[] values = parseLines(text);
        int dim = Math.min(values.length, MAX_DIM);
        double[] trimmed = new double[dim];
        System.arraycopy(values, 0, trimmed, 0, dim);
        return new VectorConstantConfig(trimmed);
    }

    // ── state ───────────────────────────────────────────────

    private static void defineState(CimulinkEndpointBlockEntity entity, StateSchema.Builder builder) {
        entity.bindConfig(
                builder.field(INPUT_TEXT).persistent().editable().sync(SyncTarget.UI),
                INPUT_TEXT);
        entity.bindReplicaLocal(
                builder.field(ERROR_TEXT).runtime().sync(SyncTarget.UI),
                ERROR_TEXT);
    }

    // ── commands ────────────────────────────────────────────

    private static void defineCommands(CimulinkEndpointBlockEntity entity, CommandRegistry commands) {
        // The payload is the raw text from the client-side TextArea.
        // We use this instead of reading INPUT_TEXT from server state, because
        // the TextArea is a custom widget that doesn't auto-sync edits to the server.
        commands.register(APPLY_VECTOR, (ctx, text) -> {
            CimulinkEndpointBlockEntity be = (CimulinkEndpointBlockEntity) ctx.blockEntity();
            if (text == null) text = "";

            String[] lines = text.split("\n", -1);
            String firstError = null;
            double[] values = new double[lines.length];

            if (lines.length > MAX_DIM) {
                firstError = "Dim " + lines.length + " exceeds max " + MAX_DIM;
            }

            for (int i = 0; i < lines.length; i++) {
                String trimmed = lines[i].trim();
                if (trimmed.isEmpty()) {
                    values[i] = 0.0;
                    continue;
                }
                try {
                    values[i] = Double.parseDouble(trimmed);
                    if (!Double.isFinite(values[i])) {
                        if (firstError == null) {
                            firstError = "L" + (i + 1) + ": not finite";
                        }
                    }
                } catch (NumberFormatException e) {
                    if (firstError == null) {
                        firstError = "L" + (i + 1) + ": invalid number";
                    }
                }
            }

            if (firstError != null) {
                be.state().set(ERROR_TEXT, firstError);
            } else {
                be.state().set(ERROR_TEXT, "");
                StringBuilder clean = new StringBuilder();
                for (int i = 0; i < values.length; i++) {
                    if (i > 0) clean.append("\n");
                    if (values[i] == (long) values[i]) {
                        clean.append((long) values[i]);
                    } else {
                        clean.append(values[i]);
                    }
                }
                be.state().set(INPUT_TEXT, clean.toString());
            }
        });
    }

    // ── UI ──────────────────────────────────────────────────

    private static final int TEXT_COLOR = 0xFFFFFF;

    private static void defineUi(CimulinkEndpointBlockEntity entity, UiSchema.Builder ui) {
        // Apply action defined via ui.action() — renders as label + ✓ button,
        // placed above the TextArea. No command is bound here because APPLY_VECTOR
        // is CommandKey<String> (not Unit); we override the button's onClick in behavior.
        ui.page("control", SynaxisUiText.text("common.page.control"))
                .group("vector", Component.literal("Vector"));
        ui.action("apply").label(SynaxisUiText.text("common.action.apply"));

        ui.behavior(ctx -> {
            CimulinkEndpointBlockEntity be = (CimulinkEndpointBlockEntity) ctx.blockEntity();

            // Code editor for multi-line vector input (with line numbers).
            // We don't sync INPUT_TEXT to the server on every keystroke because the
            // content can be large; instead the full text is sent once via APPLY_VECTOR.
            CodeEditor editor = new CodeEditor();
            editor.setId("vector_input_area");
            editor.getTextAreaStyle()
                    .textColor(TEXT_COLOR)
                    .cursorColor(TEXT_COLOR)
                    .focusOverlay(SynaxisGuiTextures.inputFocusOverlay())
                    .placeholder(Component.literal("Values, one per line (empty = 0)"));
            editor.style(style -> style.backgroundTexture(SynaxisGuiTextures.inputBackground()));
            editor.layout(layout -> {
                layout.widthPercent(100.0f);
                layout.height(120.0f);
                layout.marginBottom(4.0f);
            });
            // No StyleManager → no syntax highlighting

            String initial = be.getState(INPUT_TEXT);
            if (initial != null && !initial.isEmpty()) {
                editor.setValue(initial.split("\n", -1), false);
            }

            // Override the action button's onClick to send TextArea content as payload,
            // since APPLY_VECTOR is CommandKey<String> and ui.action() only binds Unit commands.
            ctx.element("apply_button").ifPresent(el -> {
                if (el instanceof Button btn) {
                    btn.setActive(true);
                    btn.setOnClick(event -> {
                        String currentText = String.join("\n", editor.getValue());
                        LdLibCommandBridge.invoke(be, ctx.modularUI().player, APPLY_VECTOR, currentText);
                    });
                }
            });

            // Error label — only first error, single line
            Label errorLabel = new Label();
            errorLabel.setId("vector_error_label");
            errorLabel.textStyle(style -> style
                    .textColor(0xFF4444));
            errorLabel.layout(layout -> {
                layout.widthPercent(100.0f);
                layout.height(13.0f);
                layout.marginTop(2.0f);
            });

            LdLibStateBindings.observe(
                    ctx.subscriptions(), be, ERROR_TEXT,
                    error -> {
                        boolean visible = error != null && !error.isEmpty();
                        errorLabel.setText(visible
                                ? Component.literal(error)
                                : Component.empty());
                        errorLabel.setVisible(visible);
                        errorLabel.setDisplay(visible);
                    });

            String initialError = be.getState(ERROR_TEXT);
            boolean hasError = initialError != null && !initialError.isEmpty();
            errorLabel.setText(hasError
                    ? Component.literal(initialError)
                    : Component.empty());
            errorLabel.setVisible(hasError);
            errorLabel.setDisplay(hasError);

            ctx.element("page_control_group_vector").ifPresent(group -> {
                group.addChildren(editor, errorLabel);
            });
        });
    }

    // ── parsing helpers ─────────────────────────────────────

    private static double[] parseLines(String text) {
        if (text == null || text.isBlank()) return new double[]{1.0, 2.0, 3.0};
        String[] lines = text.split("\n", -1);
        double[] values = new double[lines.length];
        for (int i = 0; i < lines.length; i++) {
            String trimmed = lines[i].trim();
            if (trimmed.isEmpty()) {
                values[i] = 0.0;
            } else {
                try {
                    values[i] = Double.parseDouble(trimmed);
                } catch (NumberFormatException e) {
                    values[i] = 0.0;
                }
            }
        }
        return values;
    }
}
