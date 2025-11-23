/* api/repository/UserRepository.java */

package backend.blog.api.repository;

import backend.blog.api.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Fixed: "existBy" → "existsBy"
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}