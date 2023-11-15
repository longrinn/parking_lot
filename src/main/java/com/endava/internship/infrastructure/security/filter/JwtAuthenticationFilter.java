package com.endava.internship.infrastructure.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.endava.internship.infrastructure.security.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static java.util.Collections.emptyList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && !authHeader.isBlank() && authHeader.startsWith("Bearer ")) {

            final String jwt = authHeader.substring(7);

            if (jwt.isBlank()) {
                response.sendError(SC_BAD_REQUEST, "Invalid jwt token in bearer header");
            } else {
                try {
                    final String email = jwtUtils.validateTokenAndRetrieveClaim(jwt);
                    final UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    final UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), emptyList());

                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (JWTVerificationException e) {
                    response.sendError(SC_BAD_REQUEST, "Invalid jwt token in bearer header");
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}