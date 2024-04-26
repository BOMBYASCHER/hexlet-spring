package io.spring.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {
    @NotBlank
    private String username;

    @Email
    private String email;

    @Size(min = 8)
    private String password;

    @Size(min = 2)
    private String firstName;

    @Size(min = 2)
    private String lastName;
}
