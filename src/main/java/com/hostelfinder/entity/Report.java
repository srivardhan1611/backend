package com.hostelfinder.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "reports")
@Getter @Setter @NoArgsConstructor
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "reporter_id")
    private User reporter;
    @Column(nullable = false) private String targetType;   // HOSTEL or REVIEW
    @Column(nullable = false) private Long targetId;
    @Column(length = 500) private String reason;
    private String status = "OPEN";                          // OPEN or RESOLVED
    private LocalDateTime createdAt;

    @PrePersist void onCreate() { createdAt = LocalDateTime.now(); }
}
