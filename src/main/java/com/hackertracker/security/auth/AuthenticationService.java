package com.hackertracker.security.auth;

import com.hackertracker.security.config.JwtService;
import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.problem.UserProblemPriorityService;
import com.hackertracker.security.user.Role;
import lombok.RequiredArgsConstructor;
import com.hackertracker.security.user.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserDAO userDao;
    private final UserProblemPriorityService priorityService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder().
                firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userDao.saveUser(user);
        priorityService.initializeAllPrioritiesForNewUser(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUserName(),
                        request.getPassword()
                )
        );

        var user = userDao.getUserByUserName(request.getUserName());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }
}
