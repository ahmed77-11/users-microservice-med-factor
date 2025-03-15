package com.medfactor.factorusers.security;

import com.medfactor.factorusers.repos.UserRepository;
import com.medfactor.factorusers.service.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
public class ForcePasswordChangeFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                UserDetailsImpl userDetails = (UserDetailsImpl) principal;
                String requestURI = request.getRequestURI();
                // Allow both login, logout and the change-password-first-time endpoints
                String[] allowedPaths = { "/api/auth/change-password-first-time", "/api/auth/login", "/api/auth/logout" };
                boolean isAllowed = Arrays.stream(allowedPaths)
                        .anyMatch(path -> requestURI.endsWith(path));
                if (userDetails.isForceChangePassword() && !isAllowed) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Password change required");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
