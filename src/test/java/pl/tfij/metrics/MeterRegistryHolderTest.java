package pl.tfij.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class MeterRegistryHolderTest {
    private final MeterRegistry meterRegistryMock = mock(MeterRegistry.class);

    @BeforeEach
    void setup() {
        Mockito.when(meterRegistryMock.counter(anyString())).thenReturn(mock(Counter.class));
    }

    @AfterEach
    void cleanup() {
        meterRegistryMock.clear();
        MeterRegistryHolder.reset();
    }

    @Test
    void shouldReturnInitializedMeterRegistry() {
        // given initialized holder
        MeterRegistry initMeterRegistry = meterRegistryMock;
        MeterRegistryHolder.init(initMeterRegistry);

        // when get meterRegistry from holder
        MeterRegistry meterRegistry = MeterRegistryHolder.meterRegistry();

        // then holder returns init instance
        assertEquals(initMeterRegistry, meterRegistry);
    }

    @Test
    void shouldNotReturnNullEvenIfHolderWasNotInitialized() {
        // when get meterRegistry from holder
        MeterRegistry meterRegistry = MeterRegistryHolder.meterRegistry();

        // then holder returns not null instance
        assertNotNull(meterRegistry);
    }

    @Test
    void shouldDropMetricsRegisteredBeforeInitialization() {
        // when I register some metric on MeterRegistry returned by holder
        MeterRegistryHolder.meterRegistry().counter("some.metric").increment();

        // and I initialized the holder
        MeterRegistry initMeterRegistry = meterRegistryMock;
        MeterRegistryHolder.init(initMeterRegistry);

        // then metric before initialization is dropped
        verify(initMeterRegistry, never()).counter(anyString());
    }

    @Test
    void shouldThrowExceptionWhenTryToInitializeAlreadyInitializedHolder() {
        // given initialized holder
        MeterRegistry initMeterRegistry = meterRegistryMock;
        MeterRegistryHolder.init(initMeterRegistry);

        // expect IllegalStateException when try to initialize holder again
        assertThrows(IllegalStateException.class, () -> MeterRegistryHolder.init(initMeterRegistry));
    }

    @Test
    void shouldExecuteMetricConsumerOnInitializedHolder() {
        // given initialized holder
        MeterRegistry initMeterRegistry = meterRegistryMock;
        MeterRegistryHolder.init(initMeterRegistry);

        // when register a metric by lateInitMeterRegistry method
        MeterRegistryHolder.lateInitMeterRegistry(meterRegistry -> meterRegistry.counter("some.metric").increment());

        // then metric is registered on initMeterRegistry
        verify(initMeterRegistry, exactlyOnce()).counter("some.metric");
    }

    @Test
    void shouldExecuteMetricConsumerHandledBeforeInitialization() {
        // when register a metric by lateInitMeterRegistry method
        MeterRegistryHolder.lateInitMeterRegistry(meterRegistry -> meterRegistry.counter("some.metric").increment());

        // and initialize the holder
        MeterRegistry initMeterRegistry = meterRegistryMock;
        MeterRegistryHolder.init(initMeterRegistry);

        // then metric is registered on initMeterRegistry
        verify(initMeterRegistry, exactlyOnce()).counter("some.metric");
    }

    private VerificationMode exactlyOnce() {
        return times(1);
    }
}