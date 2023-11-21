package com.endava.internship.dao.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parking_level")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ParkingLevelEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLotEntity parkingLot;

    @NotNull
    @Column(nullable = false)
    private Integer floor;

    @NotNull
    @Column(name = "total_spots", nullable = false)
    private Integer totalSpots;

    @OneToMany(mappedBy = "parkingLevel")
    private Set<ParkingSpotEntity> parkingSpots;
}