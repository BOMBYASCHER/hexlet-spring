package io.spring.mapper;

import io.spring.dto.post.PostCreateDTO;
import io.spring.dto.post.PostDTO;
import io.spring.dto.post.PostUpdateDTO;
import io.spring.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class PostMapper {
    @Mapping(source = "authorId", target = "author")
    public abstract Post map(PostCreateDTO createDTO);

    @Mapping(source = "author.id", target = "authorId")
    public abstract PostDTO map(Post post);

    @Mapping(source = "authorId", target = "author")
    public abstract void update(PostUpdateDTO dto, @MappingTarget Post model);
}
