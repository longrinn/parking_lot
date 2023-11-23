package com.endava.internship.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.endava.internship.dao.entity.ParkingLevelEntity;

public interface ParkingLevelRepository extends JpaRepository<ParkingLevelEntity, Integer> {
}
