package com.endava.internship.infrastructure.service.api;

import com.endava.internship.web.dto.AuthenticationDto;
import com.endava.internship.web.dto.UserUpdatedRoleDto;
import com.endava.internship.web.request.AuthenticationRequest;
import com.endava.internship.web.request.ChangeRoleRequest;
import com.endava.internship.web.request.RegistrationRequest;

public interface UserService {

    AuthenticationDto authentication(AuthenticationRequest request);

    AuthenticationDto registration(RegistrationRequest request);

    UserUpdatedRoleDto updateUserRole(ChangeRoleRequest changeRoleRequest);
}