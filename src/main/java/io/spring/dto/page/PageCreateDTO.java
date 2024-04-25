package io.spring.dto.page;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageCreateDTO {
    private String slug;
    private String name;
    private String body;
}
