package com.homeservice.service;

import com.homeservice.dto.Dto;
import com.homeservice.model.ServiceBooking;
import com.homeservice.model.User;
import com.homeservice.repository.ServiceBookingRepository;
import com.homeservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookingService {

    private final ServiceBookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Transactional
    public ServiceBooking create(Dto.BookingRequest req, User customer) {
        ServiceBooking booking = new ServiceBooking();
        booking.setCustomer(customer);
        booking.setServiceType(req.getServiceType());
        booking.setDescription(req.getDescription());
        booking.setServiceDate(req.getServiceDate());
        booking.setStatus("Pending");

        List<User> providers = userRepository.findByRole("provider");
        if (!providers.isEmpty()) {
            User leastBusy = providers.stream()
                .min((p1, p2) -> Long.compare(
                    bookingRepository.countByProvider(p1),
                    bookingRepository.countByProvider(p2)
                ))
                .orElse(null);
            booking.setProvider(leastBusy);
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public ServiceBooking updateStatus(Long bookingId, String newStatus, User provider) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getProvider() == null || !booking.getProvider().getId().equals(provider.getId())) {
            throw new RuntimeException("You are not assigned to this booking");
        }
        booking.setStatus(newStatus);
        return bookingRepository.save(booking);
    }
    @Transactional
    public ServiceBooking assignProvider(Long bookingId, Long providerId) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Booking not found"));
        User provider = userRepository.findById(providerId)
            .orElseThrow(() -> new RuntimeException("Provider not found"));
        if (!"provider".equals(provider.getRole()))
            throw new RuntimeException("User is not a provider");
        booking.setProvider(provider);
        return bookingRepository.save(booking);
    }

    public List<ServiceBooking> getByCustomer(User customer) {
        return bookingRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    public List<ServiceBooking> getByProvider(User provider) {
        return bookingRepository.findByProviderOrderByCreatedAtDesc(provider);
    }

    public List<ServiceBooking> getAll() { return bookingRepository.findAll(); }
    public Optional<ServiceBooking> findById(Long id) { return bookingRepository.findById(id); }
    public long getTotal() { return bookingRepository.count(); }
    public long countPending() { return bookingRepository.countByStatus("Pending"); }

    public Dto.BookingResponse toResponse(ServiceBooking b) {
        return new Dto.BookingResponse(
            b.getId(),
            b.getServiceType(),
            b.getDescription(),
            b.getServiceDate(),
            b.getStatus(),
            b.getCreatedAt(),
            b.getCustomer().getFullname(),
            b.getCustomer().getPhone(),
            b.getCustomer().getAddress(),
            b.getProvider() != null ? b.getProvider().getFullname() : null
        );
    }
}
