package com.endava.internship.infrastructure.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {

    private Integer id;
    private String name;
    private String phone;
    private Role role;
}
