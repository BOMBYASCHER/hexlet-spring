package io.spring.dto.page;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageCreateDTO {
    @Pattern(regexp = "^[a-z0-9]+(?:([-_])[a-z0-9]+)*$")
    private String slug;

    @NotBlank
    private String name;

    @Size(min = 10)
    private String body;
}
