package com.endava.internship.infrastructure.mapper;

import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DtoMapper {

    UserUpdatedRoleResponse map(User user);
}