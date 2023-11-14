package com.endava.internship.web.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChangeRoleRequest {

    @NotNull
    private final Integer userId;
    @NotNull
    private final String newRole;
}
