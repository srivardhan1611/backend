package com.hostelfinder.controller;

import com.hostelfinder.dto.Dtos.*;
import com.hostelfinder.entity.College;
import com.hostelfinder.entity.HostelType;
import com.hostelfinder.repository.CollegeRepository;
import com.hostelfinder.service.HostelService;
import com.hostelfinder.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PublicController {
    private final CollegeRepository colleges;
    private final HostelService hostels;
    private final StudentService students;

    @GetMapping("/colleges")
    public List<College> colleges(@RequestParam(required = false, defaultValue = "") String search) {
        return colleges.findByCollegeNameContainingIgnoreCase(search);
    }

    @GetMapping("/colleges/{id}/hostels")
    public List<HostelResponse> byCollege(@PathVariable Long id,
                                          @RequestParam(required = false) BigDecimal maxRent,
                                          @RequestParam(required = false) Double maxDistance,
                                          @RequestParam(required = false) HostelType hostelType,
                                          @RequestParam(required = false) Boolean food,
                                          @RequestParam(required = false) Boolean wifi,
                                          @RequestParam(defaultValue = "false") boolean availableOnly,
                                          @RequestParam(required = false) String sortBy) {
        return hostels.search(id, maxRent, maxDistance, hostelType, food, wifi, availableOnly, sortBy);
    }

    @GetMapping("/hostels/{id}")
    public HostelResponse details(@PathVariable Long id) { return hostels.getApproved(id); }

    @GetMapping("/hostels/{id}/reviews")
    public List<ReviewResponse> reviews(@PathVariable Long id) { return students.reviewsFor(id); }
}
