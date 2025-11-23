//src/main/java/backend/blog/api/service/CommentService.java

package backend.blog.api.service;

import backend.blog.api.model.Comment;
import backend.blog.api.model.Post;
import backend.blog.api.repository.CommentRepository;
import backend.blog.api.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final JwtService jwtService;
    
    public List<Comment> getCommentsByPostId(String postid) {
        return commentRepository.findByPostid(postid);
    }
    
    @Transactional
    public Comment createComment(String postid, String text, jakarta.servlet.http.HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        if (token == null) {
            throw new RuntimeException("Authentication required");
        }
        String username = jwtService.extractUsername(token);
        
        Comment comment = new Comment();
        comment.setPostid(postid);
        comment.setUsername(username);
        comment.setText(text);
        comment.setCreatedAt(new java.util.Date().toString());
        
        Comment saved = commentRepository.save(comment);
        
        // Update comment count in post
        Post post = postRepository.findById(postid)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        long commentCount = commentRepository.countByPostid(postid);
        post.setCommentCount((int) commentCount);
        postRepository.save(post);
        
        return saved;
    }
    
    @Transactional
    public void deleteComment(String commentid, jakarta.servlet.http.HttpServletRequest request) {
        Comment comment = commentRepository.findById(commentid)
            .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        String token = jwtService.getTokenFromRequest(request);
        if (token == null) {
            throw new RuntimeException("Authentication required");
        }
        String currentUsername = jwtService.extractUsername(token);
        
        if (!comment.getUsername().equals(currentUsername)) {
            throw new RuntimeException("Not comment owner");
        }
        
        String postid = comment.getPostid();
        commentRepository.delete(comment);
        
        // Update comment count in post
        Post post = postRepository.findById(postid)
            .orElseThrow(() -> new RuntimeException("Post not found"));
        long commentCount = commentRepository.countByPostid(postid);
        post.setCommentCount((int) commentCount);
        postRepository.save(post);
    }
    
    @Transactional
    public void deleteCommentsByPostId(String postid) {
        commentRepository.deleteByPostid(postid);
    }
}

