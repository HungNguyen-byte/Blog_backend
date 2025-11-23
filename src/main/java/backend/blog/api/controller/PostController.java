//src/main/java/backend/blog/api/controller/PostController.java
package backend.blog.api.controller;
import backend.blog.api.model.Post;
import backend.blog.api.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "http://localhost:3000")
public class PostController {
    private final PostService postService;
    public PostController(PostService postService) { this.postService = postService; }

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Post post, HttpServletRequest req) {
        return ResponseEntity.ok(postService.createPost(post, req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable String id, @RequestBody Post post, HttpServletRequest req) {
        return ResponseEntity.ok(postService.updatePost(id, post, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id, HttpServletRequest req) {
        postService.deletePost(id, req);
        return ResponseEntity.ok("deleted");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable String id) { return ResponseEntity.ok(postService.getPost(id)); }

    @GetMapping
    public ResponseEntity<List<Post>> getPosts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String cat) {
        return ResponseEntity.ok(postService.getAllPosts(user, cat, q));
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable String id, HttpServletRequest req) {
        return ResponseEntity.ok(postService.toggleLike(id, req));
    }
}
