package com.hostelfinder.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "hostels")
@Getter @Setter @NoArgsConstructor
public class Hostel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hostelId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "owner_id")
    private User owner;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "college_id")
    private College college;
    @Column(nullable = false) private String name;
    private String address;
    private double latitude;
    private double longitude;
    @Column(nullable = false) private BigDecimal rent;
    @Enumerated(EnumType.STRING) private HostelType hostelType;
    private boolean foodAvailable;
    private String contact;
    @Enumerated(EnumType.STRING) private Status status = Status.PENDING;
    private String rejectionReason;

    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();
    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Facility> facilities = new ArrayList<>();
    @OneToMany(mappedBy = "hostel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HostelImage> images = new ArrayList<>();
}
