package pl.tfij.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class MeterRegistryHolder {
    private static MeterRegistry METER_REGISTRY = new SimpleMeterRegistry();
    private static List<Consumer<MeterRegistry>> LATE_INIT_METRICS = new ArrayList<>();
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
        LATE_INIT_METRICS.forEach(consumer -> consumer.accept(METER_REGISTRY));
    }

    static public MeterRegistry meterRegistry() {
        return METER_REGISTRY;
    }

    static public void lateInitMeterRegistry(Consumer<MeterRegistry> meterRegistryConsumer) {
        if (isInitialized) {
            meterRegistryConsumer.accept(METER_REGISTRY);
        } else {
            LATE_INIT_METRICS.add(meterRegistryConsumer);
        }
    }

    static void reset() {
        METER_REGISTRY = new SimpleMeterRegistry();
        isInitialized = false;
        LATE_INIT_METRICS.clear();
    }
}
