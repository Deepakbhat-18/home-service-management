package com.homeservice.controller;

import com.homeservice.dto.Dto;
import com.homeservice.model.User;
import com.homeservice.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Dto.MessageResponse> register(@Valid @RequestBody Dto.RegisterRequest req) {
        try {
            userService.register(req);
            return ResponseEntity.ok(new Dto.MessageResponse("Registration successful! Please login.", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new Dto.MessageResponse(e.getMessage(), false));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Dto.LoginRequest req, HttpSession session) {
        return userService.login(req.getEmail(), req.getPassword())
            .map(user -> {
                // Save user ID in server-side session
                // Browser will store the session cookie automatically
                session.setAttribute("userId", user.getId());
                session.setAttribute("userRole", user.getRole());
                return ResponseEntity.ok((Object) new Dto.LoginResponse(
                    user.getId(), user.getEmail(), user.getFullname(),
                    user.getRole(), "Login successful"
                ));
            })
            .orElseGet(() -> ResponseEntity.status(401)
                .body(new Dto.MessageResponse("Invalid email or password", false)));
    }


    @PostMapping("/logout")
    public ResponseEntity<Dto.MessageResponse> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(new Dto.MessageResponse("Logged out successfully", true));
    }


    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401)
                .body(new Dto.MessageResponse("Not logged in", false));
        }
        return userService.findById(userId)
            .map(user -> ResponseEntity.ok((Object) userService.toResponse(user)))
            .orElseGet(() -> ResponseEntity.status(401)
                .body(new Dto.MessageResponse("Session invalid", false)));
    }
}
