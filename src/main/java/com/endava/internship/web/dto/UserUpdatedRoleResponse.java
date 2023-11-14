package com.endava.internship.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdatedRoleResponse {

    private String name;
    private RoleDto role;
}