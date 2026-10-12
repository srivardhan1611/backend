package com.hostelfinder.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "users")
@Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String email;
    private String phone;
    @Column(nullable = false) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role;
    private boolean blocked = false;
}
