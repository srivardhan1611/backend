package com.hostelfinder.controller;

import com.hostelfinder.dto.Dtos.*;
import com.hostelfinder.entity.*;
import com.hostelfinder.exception.ApiException;
import com.hostelfinder.repository.*;
import com.hostelfinder.service.CollegeService;
import com.hostelfinder.service.HostelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final HostelRepository hostels;
    private final UserRepository users;
    private final CollegeService collegeService;
    private final ReviewRepository reviews;
    private final HostelService hostelService;

    @GetMapping("/hostels")
    public List<HostelResponse> listings(@RequestParam(defaultValue = "PENDING") Status status) {
        return hostels.findByStatus(status).stream().map(hostelService::toResponse).toList();
    }

    @PatchMapping("/hostels/{id}/status")
    @Transactional
    public HostelResponse setStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest r) {
        Hostel h = hostels.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Hostel not found"));
        if (r.status() == Status.REJECTED && (r.reason() == null || r.reason().isBlank()))
            throw new ApiException(HttpStatus.BAD_REQUEST, "A reason is required when rejecting");
        h.setStatus(r.status());
        h.setRejectionReason(r.status() == Status.REJECTED ? r.reason() : null);
        return hostelService.toResponse(h);
    }

    @GetMapping("/users")
    public List<Map<String, Object>> users() {
        return users.findAll().stream().map(u -> Map.<String, Object>of(
                "userId", u.getUserId(), "name", u.getName(), "email", u.getEmail(),
                "role", u.getRole(), "blocked", u.isBlocked())).toList();
    }

    @PatchMapping("/users/{id}/block")
    @Transactional
    public Map<String, Object> toggleBlock(@PathVariable Long id) {
        User u = users.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        if (u.getRole() == Role.ADMIN) throw new ApiException(HttpStatus.BAD_REQUEST, "Cannot block an admin");
        u.setBlocked(!u.isBlocked());
        return Map.of("userId", u.getUserId(), "blocked", u.isBlocked());
    }

    @PostMapping(value = "/colleges", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public College addCollege(@RequestParam("collegeName") String collegeName,
                              @RequestParam(value = "address", required = false) String address,
                              @RequestParam(value = "latitude", defaultValue = "0") double latitude,
                              @RequestParam(value = "longitude", defaultValue = "0") double longitude,
                              @RequestParam(value = "image", required = false) MultipartFile image) {
        return collegeService.create(collegeName, address, latitude, longitude, image);
    }

    @PostMapping(value = "/colleges/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public College collegeImage(@PathVariable Long id, @RequestParam("image") MultipartFile image) {
        return collegeService.setImage(id, image);
    }

    @DeleteMapping("/colleges/{id}")
    public void deleteCollege(@PathVariable Long id) { collegeService.delete(id); }

    @DeleteMapping("/reviews/{id}")
    public void deleteReview(@PathVariable Long id) { reviews.deleteById(id); }
}
