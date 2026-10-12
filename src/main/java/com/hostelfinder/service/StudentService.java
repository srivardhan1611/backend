package com.hostelfinder.service;

import com.hostelfinder.dto.Dtos.*;
import com.hostelfinder.entity.*;
import com.hostelfinder.exception.ApiException;
import com.hostelfinder.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {
    private final UserRepository users;
    private final HostelRepository hostels;
    private final ReviewRepository reviews;
    private final SavedHostelRepository saved;
    private final HostelService hostelService;

    private User me(String email) {
        return users.findByEmail(email).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Hostel approved(Long id) {
        Hostel h = hostels.findById(id).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Hostel not found"));
        if (h.getStatus() != Status.APPROVED) throw new ApiException(HttpStatus.NOT_FOUND, "Hostel not found");
        return h;
    }

    public List<HostelResponse> compare(List<Long> ids) {
        if (ids.size() < 2 || ids.size() > 3) throw new ApiException(HttpStatus.BAD_REQUEST, "Provide 2 or 3 hostel ids");
        return ids.stream().map(id -> hostelService.toResponse(approved(id))).toList();
    }

    public List<HostelResponse> savedList(String email) {
        return saved.findByStudent(me(email)).stream().map(s -> hostelService.toResponse(s.getHostel())).toList();
    }

    public void save(String email, Long hostelId) {
        User u = me(email);
        Hostel h = approved(hostelId);
        if (saved.findByStudentAndHostelHostelId(u, hostelId).isPresent()) return;
        SavedHostel s = new SavedHostel();
        s.setStudent(u);
        s.setHostel(h);
        saved.save(s);
    }

    public void unsave(String email, Long hostelId) {
        saved.findByStudentAndHostelHostelId(me(email), hostelId).ifPresent(saved::delete);
    }

    public ReviewResponse addReview(String email, Long hostelId, ReviewRequest req) {
        User u = me(email);
        Hostel h = approved(hostelId);
        if (reviews.existsByHostelHostelIdAndStudentUserId(hostelId, u.getUserId()))
            throw new ApiException(HttpStatus.CONFLICT, "You have already reviewed this hostel");
        Review r = new Review();
        r.setHostel(h);
        r.setStudent(u);
        r.setRating(req.rating());
        r.setComment(req.comment());
        reviews.save(r);
        return toDto(r);
    }

    public void deleteReview(String email, Long reviewId) {
        Review r = reviews.findById(reviewId).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review not found"));
        if (!r.getStudent().getEmail().equals(email)) throw new ApiException(HttpStatus.FORBIDDEN, "Not your review");
        reviews.delete(r);
    }

    public List<ReviewResponse> reviewsFor(Long hostelId) {
        return reviews.findByHostelHostelIdOrderByCreatedAtDesc(hostelId).stream().map(this::toDto).toList();
    }

    private ReviewResponse toDto(Review r) {
        return new ReviewResponse(r.getReviewId(), r.getStudent().getUserId(), r.getStudent().getName(), r.getRating(), r.getComment(), r.getCreatedAt());
    }
}
