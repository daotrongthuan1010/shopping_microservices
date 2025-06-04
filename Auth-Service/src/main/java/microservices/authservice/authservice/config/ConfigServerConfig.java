package microservices.authservice.authservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@Slf4j
public class ConfigServerConfig {

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Auth Service started successfully!");
        log.info("Config Server: Attempting to connect to http://localhost:9296");
        log.info("If Config Server is not available, using local configuration");
    }
}
