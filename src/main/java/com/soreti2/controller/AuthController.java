package com.soreti2.controller;

import com.soreti2.dto.AuthRequest;
import com.soreti2.dto.AuthResponse;
import com.soreti2.security.JwtUtil;
import com.soreti2.config.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}) // ✅ Allow React dev servers
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        try {
            // ✅ Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // ✅ Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            // ✅ Extract role
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority())
                    .orElse("ROLE_USER");

            // ✅ Generate token with role
            String token = jwtUtil.generateToken(userDetails.getUsername(), role);

            return new AuthResponse(token, role);

        } catch (Exception e) {
            throw new RuntimeException("Invalid email or password!");
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Auth API is working ✅";
    }
}
