package com.infrawatch.facility.service;

import com.infrawatch.alert.domain.AlertStatus;
import com.infrawatch.alert.repository.AlertRepository;
import com.infrawatch.facility.domain.Facility;
import com.infrawatch.facility.dto.FacilityRequest;
import com.infrawatch.facility.dto.FacilityResponse;
import com.infrawatch.facility.repository.FacilityRepository;
import com.infrawatch.global.error.BusinessException;
import com.infrawatch.global.error.ErrorCode;
import com.infrawatch.sensor.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final SensorRepository sensorRepository;
    private final AlertRepository alertRepository;

    public List<FacilityResponse> getFacilities() {
        return facilityRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public FacilityResponse getFacility(Long facilityId) {
        return toResponse(findFacility(facilityId));
    }

    @Transactional
    public FacilityResponse create(FacilityRequest request) {
        Facility facility = Facility.create(request.name(), request.type(), request.location(), request.description());
        facilityRepository.save(facility);
        return toResponse(facility);
    }

    @Transactional
    public FacilityResponse update(Long facilityId, FacilityRequest request) {
        Facility facility = findFacility(facilityId);
        facility.update(request.name(), request.type(), request.location(), request.description());
        return toResponse(facility);
    }

    @Transactional
    public void delete(Long facilityId) {
        Facility facility = findFacility(facilityId);
        facilityRepository.delete(facility);
    }

    private Facility findFacility(Long facilityId) {
        return facilityRepository.findById(facilityId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FACILITY_NOT_FOUND));
    }

    private FacilityResponse toResponse(Facility facility) {
        long sensorCount = sensorRepository.countByFacilityId(facility.getId());
        long openAlertCount = alertRepository.countByFacilityIdAndStatus(facility.getId(), AlertStatus.OPEN);
        return FacilityResponse.of(facility, sensorCount, openAlertCount);
    }
}
