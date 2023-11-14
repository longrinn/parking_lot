package com.endava.internship.infrastructure.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class User {

    private Integer id;
    private String name;
    private String phone;
    private Role role;
}
