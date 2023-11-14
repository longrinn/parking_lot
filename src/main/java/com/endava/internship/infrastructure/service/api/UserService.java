package com.endava.internship.infrastructure.service.api;

import com.endava.internship.web.dto.AuthenticationRequest;
import com.endava.internship.web.dto.AuthenticationResponse;
import com.endava.internship.web.dto.RegistrationRequest;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;
import com.endava.internship.web.request.ChangeRoleRequest;

public interface UserService {

    AuthenticationResponse authentication(AuthenticationRequest request);

    AuthenticationResponse registration(RegistrationRequest request);

    UserUpdatedRoleResponse updateUserRole(ChangeRoleRequest changeRoleRequest);
}
