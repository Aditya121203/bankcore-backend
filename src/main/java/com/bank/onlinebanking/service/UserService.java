package com.bank.onlinebanking.service;

import com.bank.onlinebanking.dto.ChangePasswordRequest;
import com.bank.onlinebanking.dto.LoginRequest;
import com.bank.onlinebanking.dto.LoginResponse;
import com.bank.onlinebanking.dto.MessageResponse;
import com.bank.onlinebanking.dto.ProfileResponse;
import com.bank.onlinebanking.dto.RegisterRequest;
import com.bank.onlinebanking.dto.UpdateProfileRequest;
import com.bank.onlinebanking.dto.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    ProfileResponse getProfile(String email);

    MessageResponse changePassword(String email,
                                   ChangePasswordRequest request);
    ProfileResponse updateProfile(
            String email,
            UpdateProfileRequest request);
}