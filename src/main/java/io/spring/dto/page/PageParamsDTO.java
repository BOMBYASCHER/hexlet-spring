package io.spring.dto.page;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PageParamsDTO {
    private String nameCont;
    private LocalDate createdAtGt;
    private LocalDate createdAtLt;
}
