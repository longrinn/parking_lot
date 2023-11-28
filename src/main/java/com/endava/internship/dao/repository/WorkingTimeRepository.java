package com.endava.internship.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.endava.internship.dao.entity.WorkingTimeEntity;

@Repository
public interface WorkingTimeRepository extends JpaRepository<WorkingTimeEntity, Integer> {
}
