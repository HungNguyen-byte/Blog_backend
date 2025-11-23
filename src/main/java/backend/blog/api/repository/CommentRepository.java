//src/main/java/backend/blog/api/repository/CommentRepository.java

package backend.blog.api.repository;

import backend.blog.api.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostid(String postid);
    long countByPostid(String postid);
    void deleteByPostid(String postid); // Delete all comments when post is deleted
}

