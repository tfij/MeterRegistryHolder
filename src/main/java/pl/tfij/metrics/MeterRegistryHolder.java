package pl.tfij.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import static java.util.Objects.requireNonNull;

public class MeterRegistryHolder {
    private static MeterRegistry METER_REGISTRY = new SimpleMeterRegistry();

    private MeterRegistryHolder() {
        throw new IllegalStateException("Can't create instance of util class");
    }

    static void init(MeterRegistry meterRegistry) {
        METER_REGISTRY = requireNonNull(meterRegistry);
    }

    static public MeterRegistry meterRegistry() {
        return METER_REGISTRY;
    }

    static void reset() {
        METER_REGISTRY = new SimpleMeterRegistry();
    }
}
