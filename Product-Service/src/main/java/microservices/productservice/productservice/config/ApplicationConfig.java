package microservices.productservice.productservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    RedisConfig.class,
    KafkaConfig.class,
    MongoConfig.class,
    JpaConfig.class
})
public class ApplicationConfig {
}
