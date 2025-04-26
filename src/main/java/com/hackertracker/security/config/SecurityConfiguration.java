package com.hackertracker.security.config;

import com.hackertracker.security.dao.UserDAO;
import com.hackertracker.security.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final UserDAO userDao;
    private final JwtService jwtService;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider, UserDAO userDao, JwtService jwtService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "user/*", "/api/calendar/**", "/dashboard", "/register", "/login", "/WEB-INF/views/**", "/error", "/css/**", "/js/**", "/assets/**", "/tinymce/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .addLogoutHandler(new LogoutHandler() {
                            @Override
                            public void logout(HttpServletRequest request, HttpServletResponse response,
                                               Authentication authentication) {
                                // First get token from cookie
                                String jwtToken = null;
                                Cookie[] cookies = request.getCookies();
                                if (cookies != null) {
                                    for (Cookie c : cookies) {
                                        if ("jwtToken".equals(c.getName())) {
                                            jwtToken = c.getValue();
                                            break;
                                        }
                                    }
                                }

                                // Process user token version increment
                                if (jwtToken != null && !jwtToken.isEmpty()) {
                                    try {
                                        // Extract username from token
                                        String username = jwtService.extractUsername(jwtToken);
                                        User myUser = userDao.getUserByUserName(username);
                                        if (myUser != null) {
                                            myUser.incrementTokenVersion();
                                            userDao.updateUser(myUser);
                                            //System.out.println("My updated user: \n\n" + myUser);
                                        }
                                    } catch (Exception e) {
                                        System.out.println("Exception in logout handler: " + e.getMessage());
                                        e.printStackTrace();
                                    }
                                }

                                // THEN delete the JWT cookie after we've used it
                                Cookie cookie = new Cookie("jwtToken", "");
                                cookie.setPath("/");
                                cookie.setMaxAge(0);
                                cookie.setHttpOnly(false);
                                response.addCookie(cookie);
                            }
                        })
                );

        return httpSecurity.build();
    }
}
