package com.endava.internship.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.endava.internship.dao.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByCredential_Email(String email);

    Optional<UserEntity> findUserEntityByParkingSpot_Id(Integer parkingSpotId);
}
