package com.endava.internship.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdatedRoleResponse {

    private String name;

    @Schema(example = "Admin")
    private RoleDto role;
}