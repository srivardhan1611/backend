package com.hostelfinder.service;

import com.hostelfinder.dto.Dtos.*;
import com.hostelfinder.entity.Role;
import com.hostelfinder.entity.User;
import com.hostelfinder.exception.ApiException;
import com.hostelfinder.repository.UserRepository;
import com.hostelfinder.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthResponse register(RegisterRequest r) {
        if (r.role() == Role.ADMIN) throw new ApiException(HttpStatus.BAD_REQUEST, "Role must be STUDENT or OWNER");
        if (users.existsByEmail(r.email())) throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
        User u = new User();
        u.setName(r.name());
        u.setEmail(r.email().toLowerCase());
        u.setPhone(r.phone());
        u.setPasswordHash(encoder.encode(r.password()));
        u.setRole(r.role());
        users.save(u);
        return new AuthResponse(jwt.generate(u), u.getRole(), u.getName(), u.getUserId());
    }

    public AuthResponse login(LoginRequest r) {
        User u = users.findByEmail(r.email().toLowerCase())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!encoder.matches(r.password(), u.getPasswordHash()))
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        if (u.isBlocked()) throw new ApiException(HttpStatus.FORBIDDEN, "Account is blocked");
        return new AuthResponse(jwt.generate(u), u.getRole(), u.getName(), u.getUserId());
    }
}
