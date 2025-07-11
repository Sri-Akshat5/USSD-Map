package com.application.map.ussd_map.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "location_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private String phoneNumber;
    private String fromLocation;
    private String toLocation;
    private int stepCount;
    private float responseTime;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
