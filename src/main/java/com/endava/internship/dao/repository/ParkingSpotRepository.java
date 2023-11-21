package com.endava.internship.dao.repository;

import com.endava.internship.dao.entity.ParkingSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpotEntity, Integer> {
}
