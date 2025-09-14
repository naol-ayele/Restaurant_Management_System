package com.soreti2.controller;

import com.soreti2.dto.OrderHistoryResponse;
import com.soreti2.dto.UserRegistrationRequest;
import com.soreti2.dto.UserResponse;
import com.soreti2.exception.AdminAlreadyExistsException;
import com.soreti2.service.OrderService;
import com.soreti2.service.StorageService;
import com.soreti2.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // Frontend URL
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;
    private final StorageService storageService;

    // ------------------- REGISTER EMPLOYEE -------------------
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @RequestPart("user") @Valid UserRegistrationRequest request,
            @RequestPart(value = "nationalId", required = false) MultipartFile nationalIdFile
    ) throws Exception {

        // Prevent creating multiple admins
        if (request.getRole() == com.soreti2.model.Role.ADMIN && userService.adminExists()) {
            throw new AdminAlreadyExistsException("An admin user already exists");
        }

        // Call service to register user
        UserResponse response = userService.registerUser(request, nationalIdFile);
        return ResponseEntity.ok(response);
    }

    // ------------------- GET ALL EMPLOYEES -------------------
    @GetMapping("/employees")
    public ResponseEntity<List<UserResponse>> getAllEmployees() {
        List<UserResponse> employees = userService.getAllUsers();
        return ResponseEntity.ok(employees);
    }

    // ------------------- UPDATE EMPLOYEE -------------------
    @PutMapping("/employees/{id}")
    public ResponseEntity<UserResponse> updateEmployee(
            @PathVariable Long id,
            @RequestPart("user") @Valid UserRegistrationRequest request,
            @RequestPart(value = "nationalId", required = false) MultipartFile nationalIdFile
    ) throws Exception {

        UserResponse updated = userService.updateUser(id, request, nationalIdFile);
        return ResponseEntity.ok(updated);
    }

    // ------------------- DELETE EMPLOYEE -------------------
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }

    // ------------------- ORDER HISTORY -------------------
    @GetMapping("/orders/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
