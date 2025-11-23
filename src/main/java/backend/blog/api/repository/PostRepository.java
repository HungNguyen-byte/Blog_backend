/* api/repository/PostRepository.java */

package backend.blog.api.repository;

import backend.blog.api.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByUsername(String username);
    List<Post> findByCategoriesContaining(String category);
    List<Post> findByTitleContainingIgnoreCaseOrDescContainingIgnoreCase(String title, String desc);
    long countByCategoriesContaining(String categoryName);
}
