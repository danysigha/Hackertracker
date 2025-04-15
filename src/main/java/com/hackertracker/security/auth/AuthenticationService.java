package com.hackertracker.security.auth;

import com.hackertracker.security.config.JwtService;
import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.problem.UserProblemPriorityService;
import com.hackertracker.security.user.Role;
import com.hackertracker.security.user.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserDAO userDao;
    private final UserProblemPriorityService priorityService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserDAO userDao, UserProblemPriorityService priorityService, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userDao = userDao;
        this.priorityService = priorityService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request) {
        var user = new User(
                0,
                null,
                request.getFirstName(),
                request.getLastName(),
                request.getUserName(),
                passwordEncoder.encode(request.getPassword()),
                request.getEmail(),
                Role.USER);

        userDao.saveUser(user);
        priorityService.initializeAllPrioritiesForNewUser(user);

        var jwtToken = jwtService.generateToken(user);
        return new AuthenticationResponse(jwtToken);
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
        return new AuthenticationResponse(jwtToken);
    }
}
