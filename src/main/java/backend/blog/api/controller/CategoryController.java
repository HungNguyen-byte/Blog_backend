// src/main/java/backend/blog/api/controller/CategoryController.java
package backend.blog.api.controller;

import backend.blog.api.dto.CategoryResponse;
import backend.blog.api.model.Category;
import backend.blog.api.service.CategoryService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Category body) {
        if (body.getName() == null || body.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid category name");
        }

        Category created = categoryService.create(body.getName().trim());
        return ResponseEntity.ok(created);
    }
}
