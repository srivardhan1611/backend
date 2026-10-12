package com.hostelfinder.repository;

import com.hostelfinder.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByHostelHostelIdOrderByCreatedAtDesc(Long hostelId);
    boolean existsByHostelHostelIdAndStudentUserId(Long hostelId, Long studentId);

    @Query("select avg(r.rating) from Review r where r.hostel.hostelId = :hostelId")
    Double avgRating(@Param("hostelId") Long hostelId);
}
