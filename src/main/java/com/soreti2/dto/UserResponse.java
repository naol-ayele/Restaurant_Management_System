package com.soreti2.dto;

import com.soreti2.model.Role;
import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String fullname;
    private String phone;
    private String email;
    private String nationalId;
    private Role role;
}
