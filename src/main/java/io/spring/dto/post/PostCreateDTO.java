package io.spring.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCreateDTO {
    @NotNull
    private Long authorId;

    @NotBlank
    private String slug;

    @NotBlank
    private String name;

    @NotBlank
    private String body;
}
