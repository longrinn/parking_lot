package com.endava.internship.infrastructure.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.repository.CredentialRepository;
import com.endava.internship.dao.repository.ParkingLotRepository;
import com.endava.internship.dao.repository.RoleRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.infrastructure.domain.Credentials;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.listeners.UserLinkToParkLotListener;
import com.endava.internship.infrastructure.listeners.UserRoleChangeEmailListener;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.mapper.DtoMapper;
import com.endava.internship.infrastructure.security.JwtUtils;
import com.endava.internship.infrastructure.security.UserDetailsImpl;
import com.endava.internship.infrastructure.service.api.UserService;
import com.endava.internship.web.dto.AuthenticationResponse;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;
import com.endava.internship.web.request.AuthenticationRequest;
import com.endava.internship.web.request.ChangeRoleRequest;
import com.endava.internship.web.request.RegistrationRequest;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import static com.endava.internship.infrastructure.util.ParkingLotConstants.ROLE_NOT_FOUND_ERROR_MESSAGE;
import static com.endava.internship.infrastructure.util.ParkingLotConstants.USER_NOT_FOUND_ERROR_MESSAGE;
import static java.util.Collections.singletonList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER = "User";
    private static final String ADMINROLE = "Admin";

    private final JwtUtils jwtUtils;
    private final DaoMapper daoMapper;
    private final DtoMapper dtoMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CredentialRepository credentialRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRoleChangeEmailListener userRoleChangeEmailListener;
    private final UserLinkToParkLotListener userLinkToParkLotListener;


    @Override
    @Transactional
    public AuthenticationResponse registration(RegistrationRequest request) {
        final User user = User.builder()
                .name(request.getName())
                .phone(request.getNumber())
                .build();
        final Credentials credentials = Credentials.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .build();

        final RoleEntity role = roleRepository.findRoleEntityByName(USER)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ROLE_NOT_FOUND_ERROR_MESSAGE, USER)
                ));

        final CredentialsEntity credentialsEntity = daoMapper.map(credentials);
        final UserEntity userEntity = daoMapper.map(user);
        userEntity.setCredential(credentialsEntity);
        userEntity.setRole(role);
        credentialsEntity.setUserEntity(userEntity);

        userRepository.save(userEntity);

        final String jwt = jwtUtils.generateToken(credentials.getEmail());

        return AuthenticationResponse.builder()
                .email(credentials.getEmail())
                .role(USER)
                .jwt(jwt)
                .build();
    }

    @Override
    @Transactional
    public AuthenticationResponse authentication(AuthenticationRequest request) {
        final Credentials requestCredentials = Credentials.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        final String email = requestCredentials.getEmail();

        final UserEntity user = userRepository.findByCredential_Email(email)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(USER_NOT_FOUND_ERROR_MESSAGE, email)));

        final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole().getName());

        final UsernamePasswordAuthenticationToken requestToken = new UsernamePasswordAuthenticationToken(
                email, requestCredentials.getPassword(), singletonList(grantedAuthority));

        final Authentication authentication = authenticationManager.authenticate(requestToken);

        final UserDetails userDetails = (UserDetailsImpl) authentication.getPrincipal();

        final String jwt = jwtUtils.generateToken(userDetails.getUsername());

        return AuthenticationResponse.builder()
                .email(userDetails.getUsername())
                .role(userDetails.getAuthorities().toString().replace("[", "").replace("]", ""))
                .jwt(jwt)
                .build();
    }

    @Override
    @Transactional
    public UserUpdatedRoleResponse updateUserRole(ChangeRoleRequest changeRoleRequest) {

        final String standardisedNewRole = StringUtils.capitalize(changeRoleRequest.getNewRole().toLowerCase().trim());
        final String userEmail = changeRoleRequest.getEmail();

        final UserEntity userEntity = userRepository.findByCredential_Email(userEmail)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(USER_NOT_FOUND_ERROR_MESSAGE, userEmail)));

        final RoleEntity newRole = roleRepository.findRoleEntityByName(standardisedNewRole)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ROLE_NOT_FOUND_ERROR_MESSAGE, standardisedNewRole)
                ));
        String oldRole = userEntity.getRole().getName();
        userEntity.setRole(newRole);

        final UserEntity userNewRole = userRepository.save(userEntity);
        final User user = daoMapper.map(userNewRole);
        if (!oldRole.equals(newRole.getName()) && newRole.getName().equals(ADMINROLE)) {
            userRoleChangeEmailListener.handleUserRoleChangeEvent(userEmail);
        }
        return dtoMapper.map(user);
    }
}