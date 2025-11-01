package com.tradewise.api.security;

import com.tradewise.api.service.JwtUtil;
import com.tradewise.api.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. Check if the Authorization header is present and valid
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // If not, pass to the next filter
            return;
        }

        // 2. Extract the token (e.g., "Bearer eyJhbGci...")
        jwt = authHeader.substring(7);

        // 3. Extract the user's email from the token
        try {
            userEmail = jwtUtil.extractEmail(jwt);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            return;
        }

        // 4. If email exists and user is not already authenticated...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 5. Load the user's details from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 6. Validate the token against the user's details
            // 6. Validate the token against the user's details
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // 7. If valid, create an "authentication" object
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Credentials are not needed
                        userDetails.getAuthorities()
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 8. Set this user as the *current authenticated user* for this request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Pass the request to the next filter (or to the controller)
        filterChain.doFilter(request, response);
    }
}