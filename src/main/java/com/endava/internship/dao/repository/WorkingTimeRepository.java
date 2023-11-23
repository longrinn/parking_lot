package com.endava.internship.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.endava.internship.dao.entity.WorkingTimeEntity;

public interface WorkingTimeRepository extends JpaRepository<WorkingTimeEntity, Integer> {
}
