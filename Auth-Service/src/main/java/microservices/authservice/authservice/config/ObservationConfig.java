package microservices.authservice.authservice.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservationConfig {

    @Bean
    @ConditionalOnProperty(value = "management.tracing.enabled", havingValue = "false", matchIfMissing = true)
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.NOOP;
    }
}
