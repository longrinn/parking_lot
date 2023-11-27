package com.endava.internship.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.endava.internship.dao.entity.ParkingLotEntity;

public interface ParkingLotRepository extends JpaRepository<ParkingLotEntity, Integer> {
}