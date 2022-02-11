package pl.tfij.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class MeterRegistryHolder {
    private static MeterRegistry METER_REGISTRY = new SimpleMeterRegistry();
    private static final List<Consumer<MeterRegistry>> LATE_INIT_METRICS = new ArrayList<>();
    private static boolean IS_INITIALIZED = false;
    private static final Lock LOCK = new ReentrantLock();

    private MeterRegistryHolder() {
        throw new IllegalStateException("Can't create instance of util class");
    }

    static void init(MeterRegistry meterRegistry) {
        LOCK.lock();
        try {
            synchronizeInit(meterRegistry);
        } finally {
            LOCK.unlock();
        }
    }

    private static void synchronizeInit(MeterRegistry meterRegistry) {
        if (IS_INITIALIZED) {
            throw new IllegalStateException("MeterRegistryHolder already initialized");
        }
        METER_REGISTRY = requireNonNull(meterRegistry);
        IS_INITIALIZED = true;
        LATE_INIT_METRICS.forEach(consumer -> consumer.accept(METER_REGISTRY));
    }

    static public MeterRegistry meterRegistry() {
        return METER_REGISTRY;
    }

    static public void lateInitMeterRegistry(Consumer<MeterRegistry> meterRegistryConsumer) {
        LOCK.lock();
        try {
            synchronizeLateInitMeterRegistry(meterRegistryConsumer);
        } finally {
            LOCK.unlock();
        }
    }

    private static void synchronizeLateInitMeterRegistry(Consumer<MeterRegistry> meterRegistryConsumer) {
        if (IS_INITIALIZED) {
            meterRegistryConsumer.accept(METER_REGISTRY);
        } else {
            LATE_INIT_METRICS.add(meterRegistryConsumer);
        }
    }

    static void reset() {
        METER_REGISTRY = new SimpleMeterRegistry();
        IS_INITIALIZED = false;
        LATE_INIT_METRICS.clear();
    }
}
