package com.endava.internship.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.endava.internship.infrastructure.service.api.UserService;
import com.endava.internship.web.dto.AuthenticationDto;
import com.endava.internship.web.dto.UserUpdatedRoleDto;
import com.endava.internship.web.request.AuthenticationRequest;
import com.endava.internship.web.request.ChangeRoleRequest;
import com.endava.internship.web.request.RegistrationRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            responses = @ApiResponse(
                    description = "Created",
                    responseCode = "201"
            ),
            description = "This endpoint is used to create an account"
    )
    @PostMapping("/registration")
    public ResponseEntity<AuthenticationDto> registration(@RequestBody @Valid RegistrationRequest request) {
        return ResponseEntity.status(CREATED).body(userService.registration(request));
    }

    @Operation(
            description = "This endpoint is used to log in"
    )
    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationDto> authentication(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.status(OK).body(userService.authentication(request));
    }

    @Operation(
            description = "This endpoint is used to change role"
    )
    @PatchMapping("/update-role")
    public ResponseEntity<UserUpdatedRoleDto> updateUserRole(@RequestBody @Valid ChangeRoleRequest changeRoleRequest) {
        final UserUpdatedRoleDto updatedUser = userService.updateUserRole(changeRoleRequest);
        return ResponseEntity.status(OK).body(updatedUser);
    }
}