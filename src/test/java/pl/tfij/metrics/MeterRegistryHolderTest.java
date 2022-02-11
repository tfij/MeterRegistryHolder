package pl.tfij.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class MeterRegistryHolderTest {

    @AfterEach
    void cleanup() {
        MeterRegistryHolder.reset();
    }

    @Test
    void shouldReturnInitializedMeterRegistry() {
        // given initialized holder
        MeterRegistry initMeterRegistry = mock(MeterRegistry.class);
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
        MeterRegistry initMeterRegistry = mock(MeterRegistry.class);
        MeterRegistryHolder.init(initMeterRegistry);

        // then metric before initialization is dropped
        verify(initMeterRegistry, never()).counter(anyString());
    }

    @Test
    void shouldThrowExceptionWhenTryToInitializeAlreadyInitializedHolder() {
        // given initialized holder
        MeterRegistry initMeterRegistry = mock(MeterRegistry.class);
        MeterRegistryHolder.init(initMeterRegistry);

        // expect IllegalStateException when try to initialize holder again
        assertThrows(IllegalStateException.class, () -> MeterRegistryHolder.init(initMeterRegistry));
    }
}