package microservices.authservice.authservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservices.authservice.authservice.entity.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long id;
    private String email;
    private String name;
    private String githubUsername;
    private String avatarUrl;
    private String role;
    private Boolean isActive;

    public static UserInfo fromUser(User user) {
        return new UserInfo(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getGithubUsername(),
            user.getAvatarUrl(),
            user.getRole().name(),
            user.getIsActive()
        );
    }
}
