package com.hostelfinder.service;

import com.hostelfinder.dto.Dtos.*;
import com.hostelfinder.entity.Hostel;
import com.hostelfinder.entity.Status;
import com.hostelfinder.repository.HostelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Rule-based recommendation.
 * Hard filters: budget, distance, hostel type, availability.
 * Score (0-100): budget 30, distance 25, food 10, facilities 15, availability 10, rating 10.
 */
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final HostelRepository hostels;
    private final HostelService hostelService;

    public List<ScoredHostel> recommend(PreferenceRequest p, int limit) {
        List<ScoredHostel> out = new ArrayList<>();
        double budget = p.maxBudget().doubleValue();

        for (Hostel h : hostels.findByCollegeCollegeIdAndStatus(p.collegeId(), Status.APPROVED)) {
            HostelResponse r = hostelService.toResponse(h);
            if (r.rent().doubleValue() > budget || r.distanceKm() > p.maxDistance()) continue;
            if (p.hostelType() != null && r.hostelType() != p.hostelType()) continue;
            if (Boolean.TRUE.equals(p.availabilityRequired()) && r.availableBeds() <= 0) continue;

            List<String> reasons = new ArrayList<>();
            double budgetScore = Math.max(0, 1 - r.rent().doubleValue() / budget);
            double distScore = Math.max(0, 1 - r.distanceKm() / p.maxDistance());
            double foodScore = (!Boolean.TRUE.equals(p.foodRequired()) || r.foodAvailable()) ? 1 : 0;

            List<String> wanted = new ArrayList<>();
            if (p.facilities() != null) wanted.addAll(p.facilities());
            if (Boolean.TRUE.equals(p.wifiRequired())) wanted.add("wifi");
            long matched = wanted.stream().distinct().filter(f -> HostelService.hasFacility(r.facilities(), f)).count();
            long total = wanted.stream().distinct().count();
            double facScore = total == 0 ? 1 : (double) matched / total;

            double availScore = r.availableBeds() > 0 ? 1 : 0;
            double ratingScore = r.avgRating() == null ? 0 : r.avgRating() / 5.0;

            double score = 30 * budgetScore + 25 * distScore + 10 * foodScore + 15 * facScore
                    + 10 * availScore + 10 * ratingScore;

            reasons.add("Rent " + r.rent() + " within budget");
            reasons.add(r.distanceKm() + " km from college");
            if (Boolean.TRUE.equals(p.foodRequired()) && r.foodAvailable()) reasons.add("Food available");
            if (total > 0) reasons.add(matched + " of " + total + " requested facilities");
            if (r.availableBeds() > 0) reasons.add(r.availableBeds() + " beds available");
            if (r.avgRating() != null) reasons.add("Rated " + r.avgRating() + "/5");

            out.add(new ScoredHostel(r, Math.round(score * 10.0) / 10.0, reasons));
        }
        return out.stream()
                .sorted(Comparator.comparingDouble(ScoredHostel::score).reversed()
                        .thenComparing(s -> s.hostel().avgRating() == null ? 0.0 : -s.hostel().avgRating())
                        .thenComparingDouble(s -> s.hostel().distanceKm()))
                .limit(limit).toList();
    }
}
