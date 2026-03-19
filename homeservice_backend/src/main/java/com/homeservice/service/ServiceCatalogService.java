package com.homeservice.service;

import com.homeservice.dto.Dto;
import com.homeservice.model.Service;
import com.homeservice.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;


@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceCatalogService {

    private final ServiceRepository serviceRepository;

    public List<Dto.ServiceResponse> getAll() {
        return serviceRepository.findAll().stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    public List<Dto.ServiceResponse> search(String keyword) {
        return serviceRepository.findByNameContainingIgnoreCase(keyword).stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    private Dto.ServiceResponse toResponse(Service s) {
        return new Dto.ServiceResponse(s.getId(), s.getName(), s.getDescription(),
            s.getPrice(), s.getIconClass(), s.getCategory());
    }
}
