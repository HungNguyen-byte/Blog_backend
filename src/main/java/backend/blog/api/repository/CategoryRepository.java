// src/main/java/backend/blog/api/repository/CategoryRepository.java

package backend.blog.api.repository;

import backend.blog.api.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Category> findByNameIgnoreCase(String name);
}
