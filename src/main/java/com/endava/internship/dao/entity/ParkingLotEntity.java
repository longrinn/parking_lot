package com.endava.internship.dao.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.time.LocalTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "parking_lot")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @NotNull
    @Column(length = 70, nullable = false, unique = true)
    private String name;

    @NotNull
    @Column(length = 70, nullable = false, unique = true)
    private String address;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @OneToMany(mappedBy = "parkingLot")
    private Set<WorkingTimeEntity> workingTimes;

    @OneToMany(mappedBy = "parkingLot")
    private Set<ParkingLevelEntity> parkingLevels;
}