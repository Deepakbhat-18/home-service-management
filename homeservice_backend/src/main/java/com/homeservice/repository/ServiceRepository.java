package com.homeservice.repository;

import com.homeservice.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    // SELECT * FROM services WHERE LOWER(name) LIKE LOWER('%keyword%')
    List<Service> findByNameContainingIgnoreCase(String keyword);
}
