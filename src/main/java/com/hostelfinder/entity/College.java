package com.hostelfinder.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "colleges")
@Getter @Setter @NoArgsConstructor
public class College {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long collegeId;
    @Column(nullable = false) private String collegeName;
    @Column(length = 500) private String address;
    private double latitude;
    private double longitude;
    /** Relative URL of the uploaded college photo, e.g. /uploads/abc.jpg (null if none). */
    @Column(length = 500) private String imageUrl;
}
