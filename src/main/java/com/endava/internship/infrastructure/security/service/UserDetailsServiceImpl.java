package com.endava.internship.infrastructure.security.service;

import com.endava.internship.dao.entity.UserEntity;
import com.endava.internship.dao.repository.UserRepository;
import com.endava.internship.infrastructure.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByCredential_Email(username)
                .orElseThrow(() -> new UsernameNotFoundException("No such user in the system"));

        return new UserDetailsImpl(user);
    }
}