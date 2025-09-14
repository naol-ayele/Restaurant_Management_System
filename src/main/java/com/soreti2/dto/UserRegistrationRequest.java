package com.soreti2.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.soreti2.model.Role;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRegistrationRequest {

    @NotBlank
    private String fullname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank
    @Pattern(
            regexp = "^(\\+\\d{1,3}[- ]?)?\\d{7,15}$",
            message = "Phone number must be valid"
    )
    private String phone;

    private MultipartFile nationalId;

    private Role role; // ADMIN will assign this
}
