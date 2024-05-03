package io.spring.mapper;

import io.spring.dto.page.PageCreateDTO;
import io.spring.dto.page.PageDTO;
import io.spring.dto.page.PageUpdateDTO;
import io.spring.model.Page;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = { JsonNullableMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public abstract class PageMapper {
    public abstract Page map(PageCreateDTO data);
    public abstract PageDTO map(Page page);
    public abstract void update(PageUpdateDTO data, @MappingTarget Page page);
}
