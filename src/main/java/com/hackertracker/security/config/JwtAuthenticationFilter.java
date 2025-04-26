package com.hackertracker.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Get the request path
        String requestURI = request.getRequestURI();

        // Check if we're on a landing page (root or /home)
        boolean isLandingPage = requestURI.equals("/");

        // Try to get token from Authorization header first
        String authHeader = request.getHeader("Authorization");
        String jwt = null;

        // If no Authorization header, check cookies
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwtToken".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
        } else {
            jwt = authHeader.substring(7);
        }

        // Check if we need to redirect for authenticated users on landing page
        if (isLandingPage && SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            response.sendRedirect("/dashboard");
            return;
        }

        // If we have a JWT, try to authenticate with it
        if (jwt != null) {
            try {
                String userName = jwtService.extractUsername(jwt);

                if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
                    if(jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        SecurityContextHolder.getContext().setAuthentication(authToken);

                        // If user is now authenticated and on landing page, redirect
                        if (isLandingPage) {
                            response.sendRedirect("/dashboard");
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                // Token processing failed, continue with filter chain
            }
        }

        filterChain.doFilter(request, response);
    }
}
