package com.soreti2.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateEmployeeRequest {

    @NotBlank(message = "Fullname must not be blank")
    private String fullname;

    @NotBlank(message = "Email must not be blank")
    private String email;

    @NotBlank(message = "Phone must not be blank")
    private String phone;

    private String password; // optional for update

    @NotBlank(message = "Role must not be blank")
    private String role; // keep as String

    // Getters & Setters
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
