package microservices.productservice.productservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.productservice.productservice.cqrs.events.ProductEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private static final String PRODUCT_EVENTS_TOPIC = "product-events";


    public void publishEvent(ProductEvent event) {
        try {
            log.info("Publishing event: {} for product: {}", event.getEventType(), event.getProductId());
            
            // Use product ID as partition key for ordering
            String partitionKey = event.getProductId().toString();
            
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(PRODUCT_EVENTS_TOPIC, partitionKey, event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Event published successfully: {} to partition: {} with offset: {}", 
                            event.getEventType(), 
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish event: {} for product: {}", 
                            event.getEventType(), event.getProductId(), ex);
                    // In production, you might want to implement retry logic or dead letter queue
                }
            });
            
        } catch (Exception e) {
            log.error("Error publishing event: {} for product: {}", 
                    event.getEventType(), event.getProductId(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}
