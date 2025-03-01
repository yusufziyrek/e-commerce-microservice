package com.yusufziyrek.authservice.service;

import com.yusufziyrek.authservice.dto.LoginRequest;
import com.yusufziyrek.authservice.dto.RegisterRequest;
import com.yusufziyrek.authservice.model.User;
import com.yusufziyrek.authservice.model.Role;
import com.yusufziyrek.authservice.repository.IUserRepository;
import com.yusufziyrek.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail()) || userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Email or Username already in use!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        return "User registered successfully!";
    }

    public String login(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByEmailOrUsername(request.getUsernameOrEmail(), request.getUsernameOrEmail());

        if (userOptional.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOptional.get().getPassword())) {
            throw new IllegalArgumentException("Invalid email/username or password!");
        }

        return jwtUtil.generateToken(userOptional.get().getEmail());
    }
}
