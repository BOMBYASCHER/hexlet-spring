package io.spring.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
