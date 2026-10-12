package com.hostelfinder.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "saved_hostels", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "hostel_id"}))
@Getter @Setter @NoArgsConstructor
public class SavedHostel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long savedId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "student_id")
    private User student;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "hostel_id")
    private Hostel hostel;
}
