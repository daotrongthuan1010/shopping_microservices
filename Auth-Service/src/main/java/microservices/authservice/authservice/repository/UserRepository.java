package microservices.authservice.authservice.repository;

import microservices.authservice.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByGithubId(String githubId);
    
    Optional<User> findByGithubUsername(String githubUsername);
    
    boolean existsByEmail(String email);
    
    boolean existsByGithubId(String githubId);
}
