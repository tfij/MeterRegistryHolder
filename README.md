# MeterRegistryHolder

The MeterRegistryHolder class is designed to hold MeterRegistry as a static field.

## Goal

The goal is to simplify using MeterRegistry in a spring application.
By this holder, you can use metrics outside the bean classes.

## Usage

Initialize holder on application start

```java
@Configuration
public class MeterRegistryHolderInitializer {
    MeterRegistryHolderInitializer(MeterRegistry meterRegistry) {
        MeterRegistryHolder.init(meterRegistry);
    }
}
```

Use holder instead of regular injected MeterRegistry, e.g.

```java
MeterRegistryHolder.meterRegistry().counter("my.metric").increment();
```

When initializing the context, before the holder is initialized, you can register metrics via lateInitMeterRegistry method, e.g.

```java
MeterRegistryHolder.lateInitMeterRegistry(meterRegistry -> meterRegistry.counter("my.metric").increment());
```

If the holder is initialized, the lambda will be executed immediately. 
Otherwise, the lambda will be remembered and executed at the time of initialization.
