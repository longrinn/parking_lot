package com.endava.internship.dao.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.endava.internship.dao.entity.WorkingTimeEntity;

public interface WorkingTimeRepository extends JpaRepository<WorkingTimeEntity, Integer> {

    Optional<Set<WorkingTimeEntity>> findByParkingLot_Id(Integer id);

    void deleteByParkingLotId(Integer id);

    WorkingTimeEntity getWorkingTimeEntityByParkingLotId(Integer id);
}