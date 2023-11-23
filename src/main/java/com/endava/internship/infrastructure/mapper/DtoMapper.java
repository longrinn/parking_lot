package com.endava.internship.infrastructure.mapper;

import org.mapstruct.Mapper;

import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    UserUpdatedRoleResponse map(User user);
}