// src/main/java/backend/blog/api/service/CategoryService.java
package backend.blog.api.service;

import backend.blog.api.dto.CategoryResponse;
import backend.blog.api.model.Category;
import backend.blog.api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PostService postService;
    
    public CategoryService(CategoryRepository categoryRepository, @Lazy PostService postService) {
        this.categoryRepository = categoryRepository;
        this.postService = postService;
    }

    // Convert MongoDB Category → CategoryResponse
    // Calculate postCount dynamically by counting posts for each category
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll()
                .stream()
                .map(c -> {
                    // Calculate actual post count for this category
                    long actualPostCount = postService.countByCategory(c.getName());
                    return new CategoryResponse(
                            c.getCategoryid(),
                            c.getName(),
                            (int) actualPostCount
                    );
                })
                .toList();
    }

    public Category create(String name) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new RuntimeException("Category already exists");
        }

        Category c = new Category();
        c.setName(name);
        c.setPostCount(0);
        c.setLastUsedDate(Instant.now());
        return categoryRepository.save(c);
    }

    /**
     * Update the lastUsedDate for a category when it's used in a post
     */
    @Transactional
    public void updateLastUsedDate(String categoryName) {
        categoryRepository.findByNameIgnoreCase(categoryName)
                .ifPresent(category -> {
                    category.setLastUsedDate(Instant.now());
                    categoryRepository.save(category);
                    log.debug("Updated lastUsedDate for category: {}", categoryName);
                });
    }

    /**
     * Delete categories that haven't been used in the last 2 days and have no posts
     */
    @Transactional
    public void deleteUnusedCategories() {
        Instant twoDaysAgo = Instant.now().minus(2, ChronoUnit.DAYS);
        List<Category> allCategories = categoryRepository.findAll();
        
        int deletedCount = 0;
        for (Category category : allCategories) {
            // Check if category has no posts and hasn't been used in 2 days
            long postCount = postService.countByCategory(category.getName());
            if (postCount == 0 && category.getLastUsedDate().isBefore(twoDaysAgo)) {
                categoryRepository.delete(category);
                deletedCount++;
                log.info("Deleted unused category: {} (last used: {}, no posts)", 
                        category.getName(), category.getLastUsedDate());
            }
        }
        
        if (deletedCount > 0) {
            log.info("Deleted {} unused categories", deletedCount);
        }
    }
}
