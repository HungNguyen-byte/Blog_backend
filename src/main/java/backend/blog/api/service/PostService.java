//src/main/java/backend/blog/api/service/PostService.java

package backend.blog.api.service;

import backend.blog.api.model.Post;
import backend.blog.api.repository.PostRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class PostService {
    private final PostRepository repo;
    private final JwtService jwtService;
    private final CategoryService categoryService;
    
    public PostService(PostRepository repo, JwtService jwtService, @Lazy CategoryService categoryService) { 
        this.repo = repo; 
        this.jwtService = jwtService;
        this.categoryService = categoryService;
    }

    public long countByCategory(String category) { return repo.countByCategoriesContaining(category); }

    @Transactional
    public Post createPost(Post p, HttpServletRequest request) {
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        p.setUsername(jwtService.extractUsername(token));
        p.setOwnerId(jwtService.extractUserId(token));
        if (p.getCreatedAt()==null) p.setCreatedAt(new java.util.Date().toString());
        Post saved = repo.save(p);
        
        // Update lastUsedDate for all categories in this post
        if (saved.getCategories() != null) {
            saved.getCategories().forEach(categoryService::updateLastUsedDate);
        }
        
        return saved;
    }

    @Transactional
    public Post updatePost(String id, Post updated, HttpServletRequest request) {
        Post existing = getPost(id);
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        String currentUserId = jwtService.extractUserId(token);
        String currentUsername = jwtService.extractUsername(token);

        // Prefer ownerId; fallback to username for legacy posts
        boolean isOwner = (existing.getOwnerId() != null && existing.getOwnerId().equals(currentUserId))
                || existing.getUsername().equals(currentUsername);
        if (!isOwner) throw new RuntimeException("Not post owner");
        existing.setTitle(updated.getTitle());
        existing.setDesc(updated.getDesc());
        existing.setPhoto(updated.getPhoto());
        existing.setCategories(updated.getCategories());
        Post saved = repo.save(existing);
        
        // Update lastUsedDate for all categories in this post
        if (saved.getCategories() != null) {
            saved.getCategories().forEach(categoryService::updateLastUsedDate);
        }
        
        return saved;
    }

    @Transactional
    public void deletePost(String id, HttpServletRequest request) {
        Post p = getPost(id);
        String token = jwtService.getTokenFromHeader(request.getHeader("Authorization"));
        String currentUserId = jwtService.extractUserId(token);
        String currentUsername = jwtService.extractUsername(token);

        boolean isOwner = (p.getOwnerId() != null && p.getOwnerId().equals(currentUserId))
                || p.getUsername().equals(currentUsername);
        if (!isOwner) throw new RuntimeException("Not post owner");
        
        // Delete all comments for this post
        // Note: CommentService will be injected if needed, but for now we'll handle it in controller
        repo.delete(p);
    }
    
    @Transactional
    public Post toggleLike(String postid, HttpServletRequest request) {
        Post post = getPost(postid);
        String token = jwtService.getTokenFromRequest(request);
        if (token == null) {
            throw new RuntimeException("Authentication required");
        }
        String userid = jwtService.extractUserId(token);
        
        List<String> likedBy = post.getLikedBy();
        
        if (likedBy == null) {
            likedBy = new java.util.ArrayList<>();
            post.setLikedBy(likedBy);
        }
        
        if (likedBy.contains(userid)) {
            // Unlike
            likedBy.remove(userid);
        } else {
            // Like
            likedBy.add(userid);
        }
        
        post.setLikeCount(likedBy.size());
        post.setLikedBy(likedBy);
        return repo.save(post);
    }

    public Post getPost(String id) { return repo.findById(id).orElseThrow(() -> new RuntimeException("Post not found")); }

    public List<Post> getAllPosts(String user, String cat, String q) {
        // Handle user filter (independent)
        if (user != null && !user.isBlank()) {
            List<Post> userPosts = repo.findByUsername(user);
            // Apply additional filters on user posts if needed
            if (cat != null && !cat.isBlank()) {
                userPosts = userPosts.stream()
                    .filter(p -> p.getCategories() != null && p.getCategories().contains(cat))
                    .toList();
            }
            if (q != null && !q.isBlank()) {
                final String query = q.toLowerCase();
                userPosts = userPosts.stream()
                    .filter(p -> (p.getTitle() != null && p.getTitle().toLowerCase().contains(query))
                        || (p.getDesc() != null && p.getDesc().toLowerCase().contains(query)))
                    .toList();
            }
            return userPosts;
        }
        
        // Handle category filter
        if (cat != null && !cat.isBlank()) {
            List<Post> categoryPosts = repo.findByCategoriesContaining(cat);
            // If text search is also provided, filter category posts by text
            if (q != null && !q.isBlank()) {
                final String query = q.toLowerCase();
                categoryPosts = categoryPosts.stream()
                    .filter(p -> (p.getTitle() != null && p.getTitle().toLowerCase().contains(query))
                        || (p.getDesc() != null && p.getDesc().toLowerCase().contains(query)))
                    .toList();
            }
            return categoryPosts;
        }
        
        // Handle text search only
        if (q != null && !q.isBlank()) {
            return repo.findByTitleContainingIgnoreCaseOrDescContainingIgnoreCase(q, q);
        }
        
        // No filters - return all posts
        return repo.findAll();
    }
}
