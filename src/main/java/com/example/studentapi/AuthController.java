package com.example.studentapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final StudentRepository studentRepository;

    public AuthController(JwtUtil jwtUtil, StudentRepository studentRepository) {
        this.jwtUtil = jwtUtil;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // Admin login
        if ("admin".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            String accessToken = jwtUtil.generateToken("admin", "ROLE_ADMIN");
            String refreshToken = jwtUtil.generateRefreshToken("admin");
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        }

        // Student login: username = rollNumber, password = dateOfBirth (YYYY-MM-DD)
        Optional<Student> studentOpt = studentRepository.findByRollNumber(request.getUsername());
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (student.getDateOfBirth() != null &&
                    student.getDateOfBirth().toString().equals(request.getPassword())) {
                String accessToken = jwtUtil.generateToken(student.getRollNumber(), "ROLE_USER");
                String refreshToken = jwtUtil.generateRefreshToken(student.getRollNumber());
                return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || !jwtUtil.isTokenValid(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired refresh token"));
        }
        if (!"refresh".equals(jwtUtil.extractType(refreshToken))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not a refresh token"));
        }
        String username = jwtUtil.extractUsername(refreshToken);
        String role = "admin".equals(username) ? "ROLE_ADMIN" : "ROLE_USER";
        String newAccessToken = jwtUtil.generateToken(username, role);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }
}
