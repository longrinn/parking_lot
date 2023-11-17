package com.endava.internship.infrastructure.service.api;

import com.endava.internship.web.dto.AuthenticationRequest;
import com.endava.internship.web.dto.AuthenticationResponse;
import com.endava.internship.web.dto.RegistrationRequest;

public interface UserService {

    AuthenticationResponse authentication(AuthenticationRequest request);

    AuthenticationResponse registration(RegistrationRequest request);
}
