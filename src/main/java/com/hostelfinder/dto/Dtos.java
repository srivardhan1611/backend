package com.hostelfinder.dto;

import com.hostelfinder.entity.HostelType;
import com.hostelfinder.entity.Role;
import com.hostelfinder.entity.Status;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Dtos {
    private Dtos() { }

    public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email, String phone,
                                  @Size(min = 6, message = "min 6 characters") String password,
                                  @NotNull Role role) { }
    public record LoginRequest(@NotBlank String email, @NotBlank String password) { }
    public record AuthResponse(String token, Role role, String name, Long userId) { }

    public record HostelRequest(@NotBlank String name, String address, double latitude, double longitude,
                                @NotNull @Positive BigDecimal rent, @NotNull HostelType hostelType,
                                boolean foodAvailable, @NotBlank String contact, @NotNull Long collegeId) { }
    public record RoomRequest(@NotBlank String roomType, @Min(1) int totalBeds, @Min(0) int availableBeds,
                              @NotNull @Positive BigDecimal rent) { }
    public record ReviewRequest(@Min(1) @Max(5) int rating, @Size(max = 1000) String comment) { }
    public record StatusRequest(@NotNull Status status, String reason) { }
    public record CollegeRequest(@NotBlank String collegeName, String address, double latitude, double longitude) { }

    public record PreferenceRequest(@NotNull Long collegeId, @NotNull @Positive BigDecimal maxBudget,
                                    @NotNull @Positive Double maxDistance, Boolean foodRequired,
                                    Boolean wifiRequired, List<String> facilities, HostelType hostelType,
                                    Boolean availabilityRequired) { }

    public record RoomDto(Long roomId, String roomType, int totalBeds, int availableBeds, BigDecimal rent) { }
    public record HostelResponse(Long hostelId, String name, String address, double latitude, double longitude,
                                 BigDecimal rent, HostelType hostelType, boolean foodAvailable, String contact,
                                 Status status, Long collegeId, double distanceKm, Double avgRating,
                                 int availableBeds, List<String> facilities, List<String> images,
                                 List<RoomDto> rooms) { }
    public record ReviewResponse(Long reviewId, Long studentId, String studentName, int rating, String comment,
                                 LocalDateTime createdAt) { }
    public record ScoredHostel(HostelResponse hostel, double score, List<String> reasons) { }
}
