package com.endava.internship.dao.entity;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "working_time")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkingTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLotEntity parkingLot;

    @NotNull
    @Column(name = "name_day", nullable = false)
    private String nameDay;
}