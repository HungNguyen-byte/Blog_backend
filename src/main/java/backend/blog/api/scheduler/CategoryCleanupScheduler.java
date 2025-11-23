// src/main/java/backend/blog/api/scheduler/CategoryCleanupScheduler.java

package backend.blog.api.scheduler;

import backend.blog.api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryCleanupScheduler {

    private final CategoryService categoryService;

    /**
     * Run every day at 2 AM to delete unused categories
     * Categories with no posts that haven't been used in 2 days will be deleted
     */
    @Scheduled(cron = "0 0 2 * * ?") // Every day at 2 AM
    public void cleanupUnusedCategories() {
        log.info("Starting scheduled cleanup of unused categories...");
        try {
            categoryService.deleteUnusedCategories();
            log.info("Completed scheduled cleanup of unused categories");
        } catch (Exception e) {
            log.error("Error during category cleanup: ", e);
        }
    }
}

