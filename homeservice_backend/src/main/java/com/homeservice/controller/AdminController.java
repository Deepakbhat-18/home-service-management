package com.homeservice.controller;

import com.homeservice.dto.Dto;
import com.homeservice.model.User;
import com.homeservice.service.BookingService;
import com.homeservice.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final BookingService bookingService;

    private User requireAdmin(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) throw new RuntimeException("UNAUTHORIZED");
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("UNAUTHORIZED"));
        if (!"admin".equals(user.getRole())) throw new RuntimeException("FORBIDDEN");
        return user;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(HttpSession session) {
        try {
            requireAdmin(session);
            return ResponseEntity.ok(Map.of(
                "totalBookings",  bookingService.getTotal(),
                "pendingBookings", bookingService.countPending(),
                "totalCustomers", userService.countCustomers(),
                "totalProviders", userService.countProviders()
            ));
        } catch (RuntimeException e) {
            int status = "UNAUTHORIZED".equals(e.getMessage()) ? 401 : 403;
            return ResponseEntity.status(status).body(new Dto.MessageResponse(e.getMessage(), false));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        try {
            requireAdmin(session);
            var users = userService.getAllUsers().stream()
                .map(userService::toResponse).collect(Collectors.toList());
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            int status = "UNAUTHORIZED".equals(e.getMessage()) ? 401 : 403;
            return ResponseEntity.status(status).body(new Dto.MessageResponse(e.getMessage(), false));
        }
    }

    @GetMapping("/providers")
    public ResponseEntity<?> getProviders(HttpSession session) {
        try {
            requireAdmin(session);
            var providers = userService.getAllProviders().stream()
                .map(userService::toResponse).collect(Collectors.toList());
            return ResponseEntity.ok(providers);
        } catch (RuntimeException e) {
            int status = "UNAUTHORIZED".equals(e.getMessage()) ? 401 : 403;
            return ResponseEntity.status(status).body(new Dto.MessageResponse(e.getMessage(), false));
        }
    }
}
