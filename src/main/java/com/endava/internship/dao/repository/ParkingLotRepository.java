package com.endava.internship.dao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.endava.internship.dao.entity.ParkingLotEntity;

public interface ParkingLotRepository extends JpaRepository<ParkingLotEntity, Integer> {

    Optional<ParkingLotEntity> findByName(String parkingLotName);

    boolean existsByName(String name);

    List<ParkingLotEntity> findAll();
}