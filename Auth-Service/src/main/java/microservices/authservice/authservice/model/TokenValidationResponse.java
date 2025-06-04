package microservices.authservice.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {
    private boolean valid;
    private Long userId;
    private String email;
    private String role;
    private String message;

    public TokenValidationResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public TokenValidationResponse(boolean valid, Long userId, String email, String role) {
        this.valid = valid;
        this.userId = userId;
        this.email = email;
        this.role = role;
    }
}
