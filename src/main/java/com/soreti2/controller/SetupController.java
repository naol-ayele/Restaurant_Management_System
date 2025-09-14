package com.soreti2.controller;

import com.soreti2.dto.UserRegistrationRequest;
import com.soreti2.dto.UserResponse;
import com.soreti2.exception.AdminAlreadyExistsException;
import com.soreti2.model.Role;
import com.soreti2.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class SetupController {

    private final UserService userService;

    @PostMapping("/admin")
    public ResponseEntity<UserResponse> setupAdmin(
            @RequestPart("user") @Valid UserRegistrationRequest request,
            @RequestPart("nationalId") MultipartFile nationalIdFile) throws Exception {

        // Force role to ADMIN regardless of what the DTO has
        request.setRole(Role.ADMIN);

        // Check if any Admin exists
        if (userService.adminExists()) {
            throw new AdminAlreadyExistsException("An admin user already exists");
        }

        // Register admin (store file, etc.)
        UserResponse admin = userService.registerUser(request, nationalIdFile);

        return ResponseEntity.ok(admin);
    }
}
