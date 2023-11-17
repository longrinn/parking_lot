package com.endava.internship.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationRequest {

    private String name;
    private String email;
    private String password;
    private String number;
}
