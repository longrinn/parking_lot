package com.endava.internship.dao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.endava.internship.dao.entity.ParkingSpotEntity;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpotEntity, Integer> {

Optional<List<ParkingSpotEntity>> findByParkingLevelId(Integer id);

    @Modifying
    @Transactional
    @Query
            (value = "DELETE FROM Client_Spot ps WHERE ps.spot_id IN :spotIds",
                    nativeQuery = true)
    void deleteRelationUserSpotBySpotIds(@Param("spotIds") List<Integer> spotIds);

    @Modifying
    @Transactional
    @Query
            (value = "DELETE FROM client_Spot ps WHERE ps.spot_id = :spotId",
                    nativeQuery = true)
    void deleteRelationUserSpotBySpotId(Integer spotId);
} 