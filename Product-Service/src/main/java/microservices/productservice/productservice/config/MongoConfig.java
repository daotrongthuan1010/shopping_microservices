package microservices.productservice.productservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "microservices.productservice.productservice.repository.read")
@Slf4j
public class MongoConfig extends AbstractMongoClientConfiguration {

    public MongoConfig() {
        log.info("MongoDB configuration enabled - connecting to MongoDB");
    }

    @Override
    protected String getDatabaseName() {
        return "product_db";
    }

    @Override
    protected boolean autoIndexCreation() {
        return false; // Disable auto index creation to avoid auth issues
    }
}
