package com.endava.internship.infrastructure.service;

import com.endava.internship.dao.entity.CredentialsEntity;
import com.endava.internship.dao.entity.RoleEntity;
import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.repository.RoleRepository;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.infrastructure.domain.Credentials;
import com.endava.internship.infrastructure.domain.User;
import com.endava.internship.infrastructure.mapper.DaoMapper;
import com.endava.internship.infrastructure.security.JwtUtils;
import com.endava.internship.infrastructure.security.UserDetailsImpl;
import com.endava.internship.infrastructure.service.api.UserService;
import com.endava.internship.web.dto.AuthenticationRequest;
import com.endava.internship.web.dto.AuthenticationResponse;
import com.endava.internship.web.dto.RegistrationRequest;
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

import static java.util.Collections.singletonList;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String USER = "User";

    private final JwtUtils jwtUtils;
    private final DaoMapper daoMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
}
