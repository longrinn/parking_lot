package com.endava.internship.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "parking_spot", indexes = @Index(name = "unavailable_spot_idx", columnList = "state"))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ParkingSpotEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "level_id", nullable = false)
    private ParkingLevelEntity parkingLevel;

    @Column(nullable = false)
    private String name;

    @Column(name = "state", nullable = false)
    private boolean available;

    @Column(nullable = false)
    private String type;

    @OneToOne(mappedBy = "parkingSpot")
    private UserEntity user;
}