package com.hostelfinder.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "facilities")
@Getter @Setter @NoArgsConstructor
public class Facility {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "hostel_id")
    private Hostel hostel;
    @Column(nullable = false) private String facilityName;
}
