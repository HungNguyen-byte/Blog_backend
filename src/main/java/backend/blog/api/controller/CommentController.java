//src/main/java/backend/blog/api/controller/CommentController.java

package backend.blog.api.controller;

import backend.blog.api.model.Comment;
import backend.blog.api.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @GetMapping("/post/{postid}")
    public ResponseEntity<List<Comment>> getCommentsByPostId(@PathVariable String postid) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postid));
    }
    
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String postid = body.get("postid");
        String text = body.get("text");
        return ResponseEntity.ok(commentService.createComment(postid, text, request));
    }
    
    @DeleteMapping("/{commentid}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentid, HttpServletRequest request) {
        commentService.deleteComment(commentid, request);
        return ResponseEntity.ok("deleted");
    }
}

