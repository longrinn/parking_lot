package com.endava.internship.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {

    private Integer id;
    private String name;
    private String phone;
    private RoleDto role;
}
