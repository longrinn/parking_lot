package com.endava.internship.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.endava.internship.dao.entity.ParkingLevelEntity;

public interface ParkingLevelRepository extends JpaRepository<ParkingLevelEntity, Integer> {

    List<ParkingLevelEntity> getByParkingLotId(Integer id);
}
