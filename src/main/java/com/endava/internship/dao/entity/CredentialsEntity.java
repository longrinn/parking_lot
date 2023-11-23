package com.endava.internship.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Credentials")
public class CredentialsEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private UserEntity userEntity;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;
}