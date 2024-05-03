package io.spring.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {
    @NotBlank
    private JsonNullable<String> username;

    @Email
    private JsonNullable<String> email;

    @Size(min = 8)
    private JsonNullable<String> password;
}
