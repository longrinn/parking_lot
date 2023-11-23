package com.endava.internship.infrastructure.service.api;

import com.endava.internship.web.dto.AuthenticationResponse;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;
import com.endava.internship.web.request.AuthenticationRequest;
import com.endava.internship.web.request.ChangeRoleRequest;
import com.endava.internship.web.request.RegistrationRequest;

public interface UserService {

    AuthenticationResponse authentication(AuthenticationRequest request);

    AuthenticationResponse registration(RegistrationRequest request);

    UserUpdatedRoleResponse updateUserRole(ChangeRoleRequest changeRoleRequest);
}
