package com.hostelfinder.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "hostel_images")
@Getter @Setter @NoArgsConstructor
public class HostelImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "hostel_id")
    private Hostel hostel;
    @Column(nullable = false, length = 500) private String imageUrl;
}
