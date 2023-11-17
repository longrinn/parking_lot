package com.endava.internship.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CredentialsDto {

    private Integer id;
    private String email;
    private String password;
}