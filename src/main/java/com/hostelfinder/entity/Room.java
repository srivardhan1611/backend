package com.hostelfinder.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "rooms")
@Getter @Setter @NoArgsConstructor
public class Room {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "hostel_id")
    private Hostel hostel;
    private String roomType;
    private int totalBeds;
    private int availableBeds;
    private BigDecimal rent;
}
