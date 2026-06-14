package com.example.studentapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;

    public AuthController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        if ("admin".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            String accessToken = jwtUtil.generateToken(request.getUsername(), "ROLE_ADMIN");
            String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        }
        if ("user".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            String accessToken = jwtUtil.generateToken(request.getUsername(), "ROLE_USER");
            String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
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
