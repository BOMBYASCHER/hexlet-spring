package io.spring.dto.page;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PageDTO {
    private Long id;
    private String slug;
    private String name;
    private String body;
    private LocalDate createdAt;
}
