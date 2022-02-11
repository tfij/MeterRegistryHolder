package pl.tfij.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class MeterRegistryHolder {
    private static MeterRegistry METER_REGISTRY = new SimpleMeterRegistry();
    private static boolean isInitialized = false;

    private MeterRegistryHolder() {
        throw new IllegalStateException("Can't create instance of util class");
    }

    static void init(MeterRegistry meterRegistry) {
        if (isInitialized) {
            throw new IllegalStateException("MeterRegistryHolder already initialized");
        }
        METER_REGISTRY = requireNonNull(meterRegistry);
        isInitialized = true;
    }

    static public MeterRegistry meterRegistry() {
        return METER_REGISTRY;
    }

    static void reset() {
        METER_REGISTRY = new SimpleMeterRegistry();
        isInitialized = false;
    }
}
