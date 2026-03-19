package com.homeservice.controller;

import com.homeservice.dto.Dto;
import com.homeservice.model.User;
import com.homeservice.service.BookingService;
import com.homeservice.service.ServiceCatalogService;
import com.homeservice.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
class ServicesCatalogController {

    private final ServiceCatalogService catalogService;

    @GetMapping
    public ResponseEntity<List<Dto.ServiceResponse>> getAllServices(
            @RequestParam(required = false) String q) {
        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(catalogService.search(q));
        }
        return ResponseEntity.ok(catalogService.getAll());
    }
}


@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    private User requireUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) throw new RuntimeException("UNAUTHORIZED");
        return userService.findById(userId)
            .orElseThrow(() -> new RuntimeException("UNAUTHORIZED"));
    }

    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody Dto.BookingRequest req,
            HttpSession session) {
        try {
            User customer = requireUser(session);
            if (!"customer".equals(customer.getRole()))
                return ResponseEntity.status(403).body(new Dto.MessageResponse("Only customers can book", false));
            var booking = bookingService.create(req, customer);
            return ResponseEntity.ok(bookingService.toResponse(booking));
        } catch (RuntimeException e) {
            if ("UNAUTHORIZED".equals(e.getMessage()))
                return ResponseEntity.status(401).body(new Dto.MessageResponse("Please login", false));
            return ResponseEntity.badRequest().body(new Dto.MessageResponse(e.getMessage(), false));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBookings(HttpSession session) {
        try {
            User user = requireUser(session);
            var bookings = bookingService.getByCustomer(user).stream()
                .map(bookingService::toResponse).collect(Collectors.toList());
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new Dto.MessageResponse("Please login", false));
        }
    }

    @GetMapping("/provider")
    public ResponseEntity<?> getProviderBookings(HttpSession session) {
        try {
            User user = requireUser(session);
            var bookings = bookingService.getByProvider(user).stream()
                .map(bookingService::toResponse).collect(Collectors.toList());
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new Dto.MessageResponse("Please login", false));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings(HttpSession session) {
        try {
            User user = requireUser(session);
            if (!"admin".equals(user.getRole()))
                return ResponseEntity.status(403).body(new Dto.MessageResponse("Admin only", false));
            var bookings = bookingService.getAll().stream()
                .map(bookingService::toResponse).collect(Collectors.toList());
            return ResponseEntity.ok(bookings);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new Dto.MessageResponse("Please login", false));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Dto.StatusUpdateRequest req,
            HttpSession session) {
        try {
            User user = requireUser(session);
            var booking = bookingService.updateStatus(id, req.getStatus(), user);
            return ResponseEntity.ok(bookingService.toResponse(booking));
        } catch (RuntimeException e) {
            if ("UNAUTHORIZED".equals(e.getMessage()))
                return ResponseEntity.status(401).body(new Dto.MessageResponse("Please login", false));
            return ResponseEntity.badRequest().body(new Dto.MessageResponse(e.getMessage(), false));
        }
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignProvider(
            @PathVariable Long id,
            @RequestBody Dto.AssignProviderRequest req,
            HttpSession session) {
        try {
            User user = requireUser(session);
            if (!"admin".equals(user.getRole()))
                return ResponseEntity.status(403).body(new Dto.MessageResponse("Admin only", false));
            var booking = bookingService.assignProvider(id, req.getProviderId());
            return ResponseEntity.ok(bookingService.toResponse(booking));
        } catch (RuntimeException e) {
            if ("UNAUTHORIZED".equals(e.getMessage()))
                return ResponseEntity.status(401).body(new Dto.MessageResponse("Please login", false));
            return ResponseEntity.badRequest().body(new Dto.MessageResponse(e.getMessage(), false));
        }
    }
}
