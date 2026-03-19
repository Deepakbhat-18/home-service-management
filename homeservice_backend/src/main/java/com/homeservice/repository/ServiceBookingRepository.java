package com.homeservice.repository;

import com.homeservice.model.ServiceBooking;
import com.homeservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {
    List<ServiceBooking> findByCustomerOrderByCreatedAtDesc(User customer);
    List<ServiceBooking> findByProviderOrderByCreatedAtDesc(User provider);
    long countByProvider(User provider);
    long countByStatus(String status);
}
