package microservices.authservice.authservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservices.authservice.authservice.entity.User;
import microservices.authservice.authservice.repository.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User createOrUpdateUserFromOAuth2(OAuth2User oAuth2User) {
        String githubId = oAuth2User.getAttribute("id").toString();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");
        String githubUsername = oAuth2User.getAttribute("login");

        Optional<User> existingUser = userRepository.findByGithubId(githubId);
        
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update user information
            user.setEmail(email);
            user.setName(name);
            user.setAvatarUrl(avatarUrl);
            user.setGithubUsername(githubUsername);
            log.info("Updated existing user: {}", email);
            return userRepository.save(user);
        } else {
            // Create new user
            User newUser = new User();
            newUser.setGithubId(githubId);
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setAvatarUrl(avatarUrl);
            newUser.setGithubUsername(githubUsername);
            newUser.setRole(User.Role.USER);
            newUser.setIsActive(true);
            
            log.info("Created new user: {}", email);
            return userRepository.save(newUser);
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByGithubId(String githubId) {
        return userRepository.findByGithubId(githubId);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserRole(Long userId, User.Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    public User deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        return userRepository.save(user);
    }

    public User activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(true);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
