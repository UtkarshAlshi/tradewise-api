package com.tradewise.api.controller;

import com.tradewise.api.dto.response.UserResponse;
import com.tradewise.api.model.User;
import com.tradewise.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users") // Note the new base path
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/users/me
     * Gets the details of the currently authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetails userDetails) {

        // 1. Spring gives us the authenticated user's "principal" (UserDetails)
        // We get the email (which is our username) from it.
        String email = userDetails.getUsername();

        // 2. Fetch our full User object from the database
        User user = userService.findByEmail(email);

        // 3. Map to our safe UserResponse DTO
        UserResponse response = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }
}