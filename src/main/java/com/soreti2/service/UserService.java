package com.soreti2.service;

import com.soreti2.dto.UserRegistrationRequest;
import com.soreti2.dto.UserResponse;
import com.soreti2.exception.ResourceAlreadyExistsException;
import com.soreti2.exception.ResourceNotFoundException;
import com.soreti2.model.Role;
import com.soreti2.model.User;
import com.soreti2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;

    // REGISTER EMPLOYEE
    public UserResponse registerUser(UserRegistrationRequest request, MultipartFile nationalIdFile) throws IOException {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ResourceAlreadyExistsException("Phone number already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        String storedFileName = null;
        if (nationalIdFile != null) {
            storedFileName = storageService.storeFile(nationalIdFile);
        }

        User user = User.builder()
                .fullname(request.getFullname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .nationalId(storedFileName)
                .build();

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    // UPDATE EMPLOYEE
    public UserResponse updateUser(Long id, UserRegistrationRequest request, MultipartFile nationalIdFile) throws IOException {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

        // Check for unique email/phone if changed
        if (!existing.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        if (!existing.getPhone().equals(request.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
            throw new ResourceAlreadyExistsException("Phone number already exists");
        }

        existing.setFullname(request.getFullname());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        existing.setRole(request.getRole());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (nationalIdFile != null) {
            String storedFileName = storageService.storeFile(nationalIdFile);
            existing.setNationalId(storedFileName);
        }

        User updated = userRepository.save(existing);
        return mapToResponse(updated);
    }

    // DELETE EMPLOYEE
    public void deleteUser(Long id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
        userRepository.delete(existing);
    }

    // GET ALL EMPLOYEES
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    // CHECK IF ADMIN EXISTS
    public boolean adminExists() {
        return userRepository.existsByRole(Role.ADMIN);
    }

    // HELPER METHOD TO MAP USER -> USER RESPONSE
    private UserResponse mapToResponse(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setFullname(user.getFullname());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setRole(user.getRole());
        res.setNationalId(user.getNationalId());
        return res;
    }
}
