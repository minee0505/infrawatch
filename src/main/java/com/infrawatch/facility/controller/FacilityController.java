package com.infrawatch.facility.controller;

import com.infrawatch.facility.dto.FacilityRequest;
import com.infrawatch.facility.dto.FacilityResponse;
import com.infrawatch.facility.service.FacilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping
    public List<FacilityResponse> getFacilities() {
        return facilityService.getFacilities();
    }

    @GetMapping("/{facilityId}")
    public FacilityResponse getFacility(@PathVariable Long facilityId) {
        return facilityService.getFacility(facilityId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public FacilityResponse create(@Valid @RequestBody FacilityRequest request) {
        return facilityService.create(request);
    }

    @PutMapping("/{facilityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public FacilityResponse update(@PathVariable Long facilityId, @Valid @RequestBody FacilityRequest request) {
        return facilityService.update(facilityId, request);
    }

    @DeleteMapping("/{facilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long facilityId) {
        facilityService.delete(facilityId);
    }
}
