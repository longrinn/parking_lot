package com.endava.internship.dao.repository;

import com.endava.internship.dao.entity.WorkingTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkingTimeRepository extends JpaRepository<WorkingTimeEntity, Integer> {
}
