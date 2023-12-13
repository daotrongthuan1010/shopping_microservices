package microservices.productservice.productservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class MessageErrorResponse {

    @JsonProperty(namespace = "message_error")
    private String messageError;


    @JsonProperty(namespace = "status_code")
    private int statusCode;
}
