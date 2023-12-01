package com.endava.internship.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.endava.internship.dao.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {

    Optional<RoleEntity> findRoleEntityByName(String name);
}
