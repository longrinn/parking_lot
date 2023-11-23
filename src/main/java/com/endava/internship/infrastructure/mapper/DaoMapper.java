package com.endava.internship.infrastructure.mapper;

import org.mapstruct.Mapper;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.infrastructure.domain.Credentials;
import com.endava.internship.infrastructure.domain.Role;
import com.endava.internship.infrastructure.domain.User;

@Mapper(componentModel = "spring")
public interface DaoMapper {

    Role map(RoleEntity roleEntity);

    CredentialsEntity map(Credentials credentials);

    UserEntity map(User user);

    User map(UserEntity user);

    RoleEntity map(Role role);
}