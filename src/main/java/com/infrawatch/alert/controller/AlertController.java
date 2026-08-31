package com.infrawatch.alert.controller;

import com.infrawatch.alert.domain.AlertStatus;
import com.infrawatch.alert.dto.AlertResponse;
import com.infrawatch.alert.service.AlertService;
import com.infrawatch.auth.security.MemberPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public List<AlertResponse> getAlerts(@RequestParam(required = false) AlertStatus status) {
        return alertService.getAlerts(status);
    }

    @PatchMapping("/{alertId}/acknowledge")
    public AlertResponse acknowledge(@PathVariable Long alertId, @AuthenticationPrincipal MemberPrincipal principal) {
        return alertService.acknowledge(alertId, principal.getMemberId());
    }

    @PatchMapping("/{alertId}/resolve")
    public AlertResponse resolve(@PathVariable Long alertId, @AuthenticationPrincipal MemberPrincipal principal) {
        return alertService.resolve(alertId, principal.getMemberId());
    }
}
