package com.hostelfinder.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "reviews", uniqueConstraints = @UniqueConstraint(columnNames = {"hostel_id", "student_id"}))
@Getter @Setter @NoArgsConstructor
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "hostel_id")
    private Hostel hostel;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "student_id")
    private User student;
    private int rating;
    @Column(length = 1000) private String comment;
    private LocalDateTime createdAt;

    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
}
