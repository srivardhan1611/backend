package com.hostelfinder.controller;

import com.hostelfinder.entity.Report;
import com.hostelfinder.exception.ApiException;
import com.hostelfinder.repository.ReportRepository;
import com.hostelfinder.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/** Students report hostels/reviews; admins view and resolve reports. */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReportController {
    public record ReportRequest(@NotBlank String targetType, @NotNull Long targetId, @NotBlank String reason) { }

    private final ReportRepository reports;
    private final UserRepository users;

    @PostMapping("/student/reports")
    public Map<String, Object> create(Authentication a, @Valid @RequestBody ReportRequest r) {
        if (!r.targetType().equals("HOSTEL") && !r.targetType().equals("REVIEW"))
            throw new ApiException(HttpStatus.BAD_REQUEST, "targetType must be HOSTEL or REVIEW");
        Report rep = new Report();
        rep.setReporter(users.findByEmail(a.getName()).orElseThrow());
        rep.setTargetType(r.targetType());
        rep.setTargetId(r.targetId());
        rep.setReason(r.reason());
        reports.save(rep);
        return Map.of("reportId", rep.getReportId(), "status", rep.getStatus());
    }

    @GetMapping("/admin/reports")
    @Transactional(readOnly = true)
    public List<Map<String, Object>> list(@RequestParam(defaultValue = "OPEN") String status) {
        return reports.findByStatusOrderByCreatedAtDesc(status).stream().map(r -> Map.<String, Object>of(
                "reportId", r.getReportId(), "targetType", r.getTargetType(), "targetId", r.getTargetId(),
                "reason", String.valueOf(r.getReason()), "reporter", r.getReporter().getName(),
                "status", r.getStatus())).toList();
    }

    @PatchMapping("/admin/reports/{id}/resolve")
    @Transactional
    public Map<String, Object> resolve(@PathVariable Long id) {
        Report r = reports.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Report not found"));
        r.setStatus("RESOLVED");
        return Map.of("reportId", id, "status", "RESOLVED");
    }
}
