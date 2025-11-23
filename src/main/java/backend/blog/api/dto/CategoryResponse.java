// src/main/java/backend/blog/api/dto/CategoryResponse.java
package backend.blog.api.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    private String categoryid;
    private String name;
    private int postCount;
}

