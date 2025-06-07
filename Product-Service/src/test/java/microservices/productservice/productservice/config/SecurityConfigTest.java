package microservices.productservice.productservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Test
    public void contextLoads() {
        // This test will verify that the Spring context loads successfully
        // with our security configuration
    }
}
