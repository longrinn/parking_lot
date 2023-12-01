package com.endava.internship.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.endava.internship.dao.entity.WorkingTimeEntity;

import java.util.Optional;
import java.util.Set;

@Repository
public interface WorkingTimeRepository extends JpaRepository<WorkingTimeEntity, Integer> {

    Optional<Set<WorkingTimeEntity>> findByParkingLot_Id(Integer id);
}
