package com.bank.onlinebanking.controller;

import java.security.Principal;
import com.bank.onlinebanking.dto.UpdateProfileRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.onlinebanking.dto.ChangePasswordRequest;
import com.bank.onlinebanking.dto.MessageResponse;
import com.bank.onlinebanking.dto.ProfileResponse;
import com.bank.onlinebanking.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ================= Profile =================

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(
            Principal principal) {

        ProfileResponse response =
                userService.getProfile(principal.getName());

        return ResponseEntity.ok(response);
    }

    // ================= Change Password =================

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            Principal principal,
            @Valid @RequestBody ChangePasswordRequest request) {

        MessageResponse response =
                userService.changePassword(principal.getName(), request);

        return ResponseEntity.ok(response);
    }
    
 // ================= UPDATE PROFILE =================

    @PutMapping("/profile")
    public ResponseEntity<ProfileResponse> updateProfile(
            Principal principal,
            @Valid @RequestBody UpdateProfileRequest request) {

        ProfileResponse response =
                userService.updateProfile(
                        principal.getName(),
                        request);

        return ResponseEntity.ok(response);
    }
}