package com.hostelfinder.service;

import com.hostelfinder.dto.Dtos.*;
import com.hostelfinder.entity.*;
import com.hostelfinder.exception.ApiException;
import com.hostelfinder.repository.HostelRepository;
import com.hostelfinder.repository.ReviewRepository;
import com.hostelfinder.util.DistanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HostelService {
    private final HostelRepository hostels;
    private final ReviewRepository reviews;

    public HostelResponse toResponse(Hostel h) {
        College c = h.getCollege();
        double dist = DistanceUtil.km(c.getLatitude(), c.getLongitude(), h.getLatitude(), h.getLongitude());
        Double avg = reviews.avgRating(h.getHostelId());
        if (avg != null) avg = Math.round(avg * 10.0) / 10.0;
        List<RoomDto> rooms = h.getRooms().stream()
                .map(r -> new RoomDto(r.getRoomId(), r.getRoomType(), r.getTotalBeds(), r.getAvailableBeds(), r.getRent()))
                .toList();
        int beds = rooms.stream().mapToInt(RoomDto::availableBeds).sum();
        return new HostelResponse(h.getHostelId(), h.getName(), h.getAddress(), h.getLatitude(), h.getLongitude(),
                h.getRent(), h.getHostelType(), h.isFoodAvailable(), h.getContact(), h.getStatus(),
                c.getCollegeId(), dist, avg, beds,
                h.getFacilities().stream().map(Facility::getFacilityName).toList(),
                h.getImages().stream().map(HostelImage::getImageUrl).toList(), rooms);
    }

    public HostelResponse getApproved(Long id) {
        Hostel h = hostels.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Hostel not found"));
        if (h.getStatus() != Status.APPROVED) throw new ApiException(HttpStatus.NOT_FOUND, "Hostel not found");
        return toResponse(h);
    }

    public List<HostelResponse> search(Long collegeId, BigDecimal maxRent, Double maxDistance, HostelType type,
                                       Boolean food, Boolean wifi, boolean availableOnly, String sortBy) {
        Stream<HostelResponse> s = hostels.findByCollegeCollegeIdAndStatus(collegeId, Status.APPROVED)
                .stream().map(this::toResponse);
        if (maxRent != null) s = s.filter(h -> h.rent().compareTo(maxRent) <= 0);
        if (maxDistance != null) s = s.filter(h -> h.distanceKm() <= maxDistance);
        if (type != null) s = s.filter(h -> h.hostelType() == type);
        if (Boolean.TRUE.equals(food)) s = s.filter(HostelResponse::foodAvailable);
        if (Boolean.TRUE.equals(wifi)) s = s.filter(h -> hasFacility(h.facilities(), "wifi"));
        if (availableOnly) s = s.filter(h -> h.availableBeds() > 0);

        Comparator<HostelResponse> cmp = switch (sortBy == null ? "distance" : sortBy) {
            case "rent" -> Comparator.comparing(HostelResponse::rent);
            case "rating" -> Comparator.comparing((HostelResponse h) -> h.avgRating() == null ? 0.0 : h.avgRating()).reversed();
            default -> Comparator.comparingDouble(HostelResponse::distanceKm);
        };
        return s.sorted(cmp).toList();
    }

    /** Compares facility names ignoring case, spaces and hyphens ("Wi-Fi" == "wifi"). */
    public static boolean hasFacility(List<String> facilities, String wanted) {
        String w = norm(wanted);
        return facilities.stream().anyMatch(f -> norm(f).equals(w));
    }

    public static String norm(String s) { return s.toLowerCase().replaceAll("[^a-z0-9]", ""); }
}
