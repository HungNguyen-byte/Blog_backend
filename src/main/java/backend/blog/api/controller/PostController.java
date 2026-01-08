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
@io.swagger.v3.oas.annotations.tags.Tag(name = "Posts", description = "Post management endpoints")
public class PostController {
    private final PostService postService;
    public PostController(PostService postService) { this.postService = postService; }

    @io.swagger.v3.oas.annotations.Operation(summary = "Create a new post", description = "Create a new blog post (requires authentication)")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody Post post, HttpServletRequest req) {
        return ResponseEntity.ok(postService.createPost(post, req));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Update a post", description = "Update an existing post (requires authentication, owner only)")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable String id, @RequestBody Post post, HttpServletRequest req) {
        return ResponseEntity.ok(postService.updatePost(id, post, req));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Delete a post", description = "Delete a post (requires authentication, owner only)")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id, HttpServletRequest req) {
        postService.deletePost(id, req);
        return ResponseEntity.ok("deleted");
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get a post by ID", description = "Retrieve a single post by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable String id) { return ResponseEntity.ok(postService.getPost(id)); }

    @io.swagger.v3.oas.annotations.Operation(
        summary = "Get all posts", 
        description = "Retrieve posts with optional filters: q (search), user (username), cat (category)"
    )
    @GetMapping
    public ResponseEntity<List<Post>> getPosts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String cat) {
        return ResponseEntity.ok(postService.getAllPosts(user, cat, q));
    }
    
    @io.swagger.v3.oas.annotations.Operation(summary = "Like/Unlike a post", description = "Toggle like status for a post (requires authentication)")
    @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/{id}/like")
    public ResponseEntity<?> toggleLike(@PathVariable String id, HttpServletRequest req) {
        return ResponseEntity.ok(postService.toggleLike(id, req));
    }
}
