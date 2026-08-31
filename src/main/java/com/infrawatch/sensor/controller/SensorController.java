package com.infrawatch.sensor.controller;

import com.infrawatch.auth.security.MemberPrincipal;
import com.infrawatch.sensor.dto.SensorRequest;
import com.infrawatch.sensor.dto.SensorResponse;
import com.infrawatch.sensor.service.SensorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @GetMapping("/api/v1/facilities/{facilityId}/sensors")
    public List<SensorResponse> getSensors(@PathVariable Long facilityId) {
        return sensorService.getSensors(facilityId);
    }

    @PostMapping("/api/v1/facilities/{facilityId}/sensors")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public SensorResponse create(@PathVariable Long facilityId, @Valid @RequestBody SensorRequest request,
                                  @AuthenticationPrincipal MemberPrincipal principal) {
        return sensorService.create(facilityId, request, principal.getMemberId());
    }

    @GetMapping("/api/v1/sensors/{sensorId}")
    public SensorResponse getSensor(@PathVariable Long sensorId) {
        return sensorService.getSensor(sensorId);
    }

    @PutMapping("/api/v1/sensors/{sensorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public SensorResponse update(@PathVariable Long sensorId, @Valid @RequestBody SensorRequest request,
                                  @AuthenticationPrincipal MemberPrincipal principal) {
        return sensorService.update(sensorId, request, principal.getMemberId());
    }

    @DeleteMapping("/api/v1/sensors/{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long sensorId, @AuthenticationPrincipal MemberPrincipal principal) {
        sensorService.delete(sensorId, principal.getMemberId());
    }
}
