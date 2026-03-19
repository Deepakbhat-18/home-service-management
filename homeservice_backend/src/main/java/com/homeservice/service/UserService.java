package com.homeservice.service;

import com.homeservice.dto.Dto;
import com.homeservice.model.User;
import com.homeservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(Dto.RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already registered");
        }
        User user = new User();
        user.setEmail(req.getEmail());
        user.setFullname(req.getFullname());
        user.setAddress(req.getAddress());
        user.setPhone(req.getPhone());
        user.setRole(req.getRole() == null ? "customer" : req.getRole());

        user.setPassword(passwordEncoder.encode(req.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
            .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()));
    }

    public Optional<User> findById(Long id) { return userRepository.findById(id); }
    public List<User> getAllProviders() { return userRepository.findByRole("provider"); }
    public List<User> getAllUsers() { return userRepository.findAll(); }
    public long countCustomers() { return userRepository.countByRole("customer"); }
    public long countProviders() { return userRepository.countByRole("provider"); }

    public Dto.UserResponse toResponse(User u) {
        return new Dto.UserResponse(u.getId(), u.getEmail(), u.getFullname(),
                u.getAddress(), u.getPhone(), u.getRole());
    }
}
