package io.spring.mapper;

import io.spring.dto.user.UserCreateDTO;
import io.spring.dto.user.UserDTO;
import io.spring.dto.user.UserUpdateDTO;
import io.spring.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class UserMapper {
    public abstract User map(UserCreateDTO data);
    public abstract UserDTO map(User user);
    public abstract void update(UserUpdateDTO data, @MappingTarget User user);
}
