package microservices.productservice.productservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class MessageErrorResponse {

    @JsonProperty(namespace = "message_error")
    private String messageError;


    @JsonProperty(namespace = "error_code")
    private String errorCode;
}
