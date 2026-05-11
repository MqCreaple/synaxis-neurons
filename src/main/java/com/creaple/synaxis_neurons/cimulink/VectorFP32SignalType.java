package com.creaple.synaxis_neurons.cimulink;

import com.verr1.synaxis.foundation.cimulink.core.signal.SignalKind;
import com.verr1.synaxis.foundation.cimulink.core.signal.SignalType;

import java.lang.reflect.Field;

/**
 * Utility accessor for a VECTOR_FP32 {@link SignalKind} instance and its
 * corresponding {@link SignalType}.
 * <p>
 * Since the {@code SignalKind} enum is fixed at compile time, the VECTOR_FP32
 * member is created at runtime via {@code Unsafe.allocateInstance()} and cached
 * here. It is <b>not</b> part of the {@code SignalKind.valueOf()} / {@code values()}
 * enumeration — it exists only as a standalone instance for use by this addon.
 * <p>
 * Components that handle VECTOR_FP32 signals should compare kinds using
 * {@code kind == VectorFP32SignalType.kind()} (reference equality).
 */
public final class VectorFP32SignalType {

    private static final SignalKind KIND;
    private static final SignalType TYPE;

    static {
        try {
            KIND = createSignalKindInstance("VECTOR_FP32");
            TYPE = new SignalType(KIND);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize VectorFP32SignalType", e);
        }
    }

    private VectorFP32SignalType() {
    }

    /** Returns the standalone VECTOR_FP32 SignalKind instance. */
    public static SignalKind kind() {
        return KIND;
    }

    /** Returns a pre-built VECTOR_FP32 SignalType. */
    public static SignalType type() {
        return TYPE;
    }

    /** Convenience: create a default VectorFP32Value with the given dimension count. */
    public static VectorFP32Value defaultValue(int dimensions) {
        return new VectorFP32Value(new float[dimensions]);
    }

    // ---- Unsafe-based instance creation ----

    private static SignalKind createSignalKindInstance(String name) throws Exception {
        Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        sun.misc.Unsafe unsafe = (sun.misc.Unsafe) unsafeField.get(null);

        SignalKind instance = (SignalKind) unsafe.allocateInstance(SignalKind.class);

        // Use Unsafe memory-offset writes to bypass Java module access restrictions.
        // Calling setAccessible(true) on java.lang.Enum fields is blocked by
        // "module java.base does not 'opens java.lang'".
        long nameOffset = unsafe.objectFieldOffset(Enum.class.getDeclaredField("name"));
        unsafe.putObject(instance, nameOffset, name);

        long ordinalOffset = unsafe.objectFieldOffset(Enum.class.getDeclaredField("ordinal"));
        unsafe.putInt(instance, ordinalOffset, -1); // sentinel ordinal; not in any official array

        return instance;
    }
}
