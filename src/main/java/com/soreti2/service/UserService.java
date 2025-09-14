package com.soreti2.service;

import com.soreti2.dto.UserRegistrationRequest;
import com.soreti2.dto.UserResponse;
import com.soreti2.exception.ResourceAlreadyExistsException;
import com.soreti2.model.Role;
import com.soreti2.model.User;
import com.soreti2.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StorageService storageService;

    public UserResponse registerUser(UserRegistrationRequest request, MultipartFile nationalIdFile) throws IOException {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ResourceAlreadyExistsException("Phone number already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }


        // Store the file using StorageService
        String storedFileName = storageService.storeFile(nationalIdFile);

        User user = User.builder()
                .fullname(request.getFullname())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .nationalId(storedFileName)
                .build();

        User saved = userRepository.save(user);

        UserResponse response = new UserResponse();
        response.setId(saved.getId());
        response.setFullname(saved.getFullname());
        response.setPhone(saved.getPhone());
        response.setEmail(saved.getEmail());
        response.setNationalId(saved.getNationalId());
        response.setRole(saved.getRole());

        return response;
    }



    public boolean adminExists() {
        return userRepository.existsByRole(Role.ADMIN);
    }


}
