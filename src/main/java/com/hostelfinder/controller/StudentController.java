package com.hostelfinder.controller;

import com.hostelfinder.dto.Dtos.*;
import com.hostelfinder.service.RecommendationService;
import com.hostelfinder.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService students;
    private final RecommendationService recommender;

    @PostMapping("/recommendations")
    public List<ScoredHostel> recommend(@Valid @RequestBody PreferenceRequest p,
                                        @RequestParam(defaultValue = "10") int limit) {
        return recommender.recommend(p, limit);
    }

    @GetMapping("/compare")
    public List<HostelResponse> compare(@RequestParam List<Long> ids) { return students.compare(ids); }

    @GetMapping("/saved")
    public List<HostelResponse> saved(Authentication a) { return students.savedList(a.getName()); }

    @PostMapping("/saved/{hostelId}")
    public void save(Authentication a, @PathVariable Long hostelId) { students.save(a.getName(), hostelId); }

    @DeleteMapping("/saved/{hostelId}")
    public void unsave(Authentication a, @PathVariable Long hostelId) { students.unsave(a.getName(), hostelId); }

    @PostMapping("/hostels/{id}/reviews")
    public ReviewResponse review(Authentication a, @PathVariable Long id, @Valid @RequestBody ReviewRequest r) {
        return students.addReview(a.getName(), id, r);
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteReview(Authentication a, @PathVariable Long id) { students.deleteReview(a.getName(), id); }
}
