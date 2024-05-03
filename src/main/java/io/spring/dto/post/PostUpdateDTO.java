package io.spring.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class PostUpdateDTO {
    @NotNull
    private JsonNullable<Long> authorId;

    @NotBlank
    private JsonNullable<String> slug;

    @NotBlank
    private JsonNullable<String> name;

    @NotBlank
    private JsonNullable<String> body;
}
