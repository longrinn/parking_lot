package com.endava.internship.web.controller;

import com.endava.internship.infrastructure.service.api.UserService;
import com.endava.internship.web.dto.AuthenticationRequest;
import com.endava.internship.web.dto.AuthenticationResponse;
import com.endava.internship.web.dto.RegistrationRequest;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;
import com.endava.internship.web.request.ChangeRoleRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<AuthenticationResponse> registration(@RequestBody RegistrationRequest request) {
        return ResponseEntity.status(CREATED).body(userService.registration(request));
    }

    @PostMapping("/authentication")
    public ResponseEntity<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.status(OK).body(userService.authentication(request));
    }

    @PatchMapping("/update-role")
    public ResponseEntity<UserUpdatedRoleResponse> updateUserRole(@RequestBody @Valid ChangeRoleRequest changeRoleRequest) {
        final UserUpdatedRoleResponse updatedUser = userService.updateUserRole(changeRoleRequest);
        return ResponseEntity.ok(updatedUser);
    }
}
