package com.soreti2.controller;

import com.soreti2.dto.OrderHistoryResponse;
import com.soreti2.dto.UserRegistrationRequest;
import com.soreti2.dto.UserResponse;
import com.soreti2.exception.AdminAlreadyExistsException;
import com.soreti2.model.Role;
import com.soreti2.service.OrderService;
import com.soreti2.service.StorageService;
import com.soreti2.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;
    private final StorageService storageService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @RequestPart("user") @Valid UserRegistrationRequest request,
            @RequestPart("nationalId") MultipartFile nationalIdFile) throws Exception {

        // If this is admin registration endpoint, force role to ADMIN
        if (request.getRole() == Role.ADMIN) {
            if (userService.adminExists()) {
                throw new AdminAlreadyExistsException("An admin user already exists");
            }

        }

        // Call service which handles file storage
        UserResponse response = userService.registerUser(request, nationalIdFile);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/orders/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

}
