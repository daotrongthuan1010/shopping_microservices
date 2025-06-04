package microservices.productservice.productservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "microservices.productservice.productservice.repository.write")
@EnableTransactionManagement
public class JpaConfig {
}
