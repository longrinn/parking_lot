package com.endava.internship.dao.entity;

import java.io.Serializable;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "client")
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @OneToOne(mappedBy = "userEntity", cascade = ALL)
    private CredentialsEntity credential;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "role")
    private RoleEntity role;

    @OneToOne
    @JoinTable(
            name = "client_spot",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "spot_id")
    )
    private ParkingSpotEntity parkingSpot;

    @ManyToMany
    @JoinTable(
            name = "user_parking_lot",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "parking_lot_id")
    )
    private Set<ParkingLotEntity> parkingLots;
}