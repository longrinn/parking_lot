package com.endava.internship.dao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.endava.internship.dao.entity.ParkingLotEntity;

public interface ParkingLotRepository extends JpaRepository<ParkingLotEntity, Integer> {

    Optional<ParkingLotEntity> findByName(String parkingLotName);

    boolean existsByName(String name);

    boolean existsByAddress(String name);

    List<ParkingLotEntity> findAll();

    Optional<ParkingLotEntity> findById(Integer parkingLotId);

    @Modifying
    @Transactional
    @Query
            (value = "DELETE FROM user_parking_lot up WHERE up.parking_lot_id = ?1",
                    nativeQuery = true)
    void deleteRelationUserSpotBySpotIds(@Param("parkingLotId") Integer parkingLotId);
}