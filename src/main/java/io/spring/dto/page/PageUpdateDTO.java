package io.spring.dto.page;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class PageUpdateDTO {
    @NotBlank
    private JsonNullable<String> name;

    @Size(min = 10)
    private JsonNullable<String> body;
}
