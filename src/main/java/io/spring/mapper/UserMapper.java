package io.spring.mapper;

import io.spring.dto.user.UserCreateDTO;
import io.spring.dto.user.UserDTO;
import io.spring.dto.user.UserUpdateDTO;
import io.spring.model.User;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = { JsonNullableMapper.class, ReferenceMapper.class },
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class UserMapper {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeMapping
    public void encryptPassword(UserCreateDTO data) {
        var password = data.getPassword();
        data.setPassword(passwordEncoder.encode(password));
    }

    @Mapping(source = "password", target = "passwordDigest")
    public abstract User map(UserCreateDTO data);
    public abstract UserDTO map(User user);
    public abstract void update(UserUpdateDTO data, @MappingTarget User user);
}
