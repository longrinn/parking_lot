package com.endava.internship.infrastructure.service;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.repository.CredentialRepository;
import com.endava.internship.dao.repository.RoleRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.infrastructure.domain.Credentials;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.listeners.UserRoleChangeEmailListener;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.mapper.DtoMapper;
import com.endava.internship.infrastructure.security.JwtUtils;
import com.endava.internship.infrastructure.security.UserDetailsImpl;
import com.endava.internship.infrastructure.service.api.UserService;
import com.endava.internship.web.dto.AuthenticationRequest;
import com.endava.internship.web.dto.AuthenticationResponse;
import com.endava.internship.web.dto.RegistrationRequest;
import com.endava.internship.web.dto.UserUpdatedRoleResponse;
import com.endava.internship.web.request.ChangeRoleRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.endava.internship.infrastructure.util.ParkingLotConstants.CREDENTIALS_NOT_FOUND_ERROR_MESSAGE;
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
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRoleChangeEmailListener userRoleChangeEmailListener;


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

        final RoleEntity role = roleRepository.findRoleEntityByName(USER).get();

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

        final UserEntity user = userRepository.findByCredential_Email(requestCredentials.getEmail()).get();
        final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(user.getRole().getName());

        final UsernamePasswordAuthenticationToken requestToken = new UsernamePasswordAuthenticationToken(
                requestCredentials.getEmail(), requestCredentials.getPassword(), singletonList(grantedAuthority));

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

        final UserEntity userEntity = userRepository.findById(changeRoleRequest.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(USER_NOT_FOUND_ERROR_MESSAGE, changeRoleRequest.getUserId())));

        final RoleEntity newRole = roleRepository.findRoleEntityByName(changeRoleRequest.getNewRole())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format(ROLE_NOT_FOUND_ERROR_MESSAGE, changeRoleRequest.getNewRole())
                ));
        String oldRole = userEntity.getRole().getName();
        userEntity.setRole(newRole);

        final UserEntity userNewRole = userRepository.save(userEntity);
        final User user = daoMapper.map(userNewRole);

        if (user != null && !oldRole.equals(newRole.getName()) && newRole.getName().equals(ADMINROLE)){
            String email = credentialRepository.findById(user.getId()).orElseThrow(() -> new EntityNotFoundException(
                    String.format(CREDENTIALS_NOT_FOUND_ERROR_MESSAGE, changeRoleRequest.getUserId()))).getEmail();
            userRoleChangeEmailListener.handleUserRoleChangeEvent(email);
        }
        return dtoMapper.map(user);
    }
}
