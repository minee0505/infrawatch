package com.infrawatch.dashboard.controller;

import com.infrawatch.dashboard.dto.DashboardSummaryResponse;
import com.infrawatch.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        return dashboardService.getSummary();
    }
}
