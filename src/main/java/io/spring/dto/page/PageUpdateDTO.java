package io.spring.dto.page;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageUpdateDTO {
    @NotBlank
    private String name;

    @Size(min = 10)
    private String body;
}
