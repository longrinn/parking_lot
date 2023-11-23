package com.endava.internship.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.endava.internship.dao.entity.ParkingSpotEntity;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpotEntity, Integer> {
}
