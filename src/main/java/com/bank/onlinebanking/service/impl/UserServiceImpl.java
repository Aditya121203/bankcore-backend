package com.bank.onlinebanking.service.impl;

import java.math.BigDecimal;
import java.util.Random;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.onlinebanking.dto.UpdateProfileRequest;
import com.bank.onlinebanking.dto.ChangePasswordRequest;
import com.bank.onlinebanking.dto.LoginRequest;
import com.bank.onlinebanking.dto.LoginResponse;
import com.bank.onlinebanking.dto.MessageResponse;
import com.bank.onlinebanking.dto.ProfileResponse;
import com.bank.onlinebanking.dto.RegisterRequest;
import com.bank.onlinebanking.dto.UserResponse;
import com.bank.onlinebanking.entity.Account;
import com.bank.onlinebanking.entity.AccountStatus;
import com.bank.onlinebanking.entity.AccountType;
import com.bank.onlinebanking.entity.Role;
import com.bank.onlinebanking.entity.User;
import com.bank.onlinebanking.exception.ResourceNotFoundException;
import com.bank.onlinebanking.exception.UserAlreadyExistsException;
import com.bank.onlinebanking.repository.AccountRepository;
import com.bank.onlinebanking.repository.UserRepository;
import com.bank.onlinebanking.security.JwtUtil;
import com.bank.onlinebanking.service.AuditLogService;
import com.bank.onlinebanking.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuditLogService auditLogService;

    public UserServiceImpl(UserRepository userRepository,
                           AccountRepository accountRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           AuditLogService auditLogService) {

        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.auditLogService = auditLogService;
    }

    // ================= REGISTER =================

    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with this email.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        account.setUser(savedUser);

        accountRepository.save(account);

        savedUser.setAccount(account);

        // Audit Log
        auditLogService.saveLog(
                savedUser.getEmail(),
                "REGISTER",
                "New user registered successfully.");

        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setFullName(savedUser.getFullName());
        response.setEmail(savedUser.getEmail());
        response.setRole(savedUser.getRole().name());

        return response;
    }

    // ================= LOGIN =================

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid email or password.");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        // Audit Log
        auditLogService.saveLog(
                user.getEmail(),
                "LOGIN",
                "User logged into the application.");

        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setToken(token);
        response.setMessage("Login Successful");

        return response;
    }

    // ================= PROFILE =================

    @Override
    public ProfileResponse getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found."));

        ProfileResponse response = new ProfileResponse();

        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setMemberSince(user.getCreatedAt());

        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType().name());
        response.setBalance(account.getBalance());
        response.setStatus(account.getStatus().name());

        return response;
    }

    // ================= CHANGE PASSWORD =================

    @Override
    public MessageResponse changePassword(String email,
                                          ChangePasswordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            throw new ResourceNotFoundException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

        // Audit Log
        auditLogService.saveLog(
                email,
                "CHANGE_PASSWORD",
                "Password changed successfully.");

        return new MessageResponse("Password changed successfully.");
    }

    // ================= ACCOUNT NUMBER =================

    private String generateAccountNumber() {

        Random random = new Random();

        return String.valueOf(
                1000000000L +
                        (long) (random.nextDouble() * 9000000000L));
    }
    
 // ================= UPDATE PROFILE =================

    @Override
    public ProfileResponse updateProfile(
            String email,
            UpdateProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found."));

        // Check whether the new email belongs to another user —
        // only relevant if an email was actually sent (this edit form doesn't send one).
        if (request.getEmail() != null && !request.getEmail().isBlank()) {

            if (!user.getEmail().equalsIgnoreCase(request.getEmail())
                    && userRepository.existsByEmail(request.getEmail())) {

                throw new UserAlreadyExistsException(
                        "Email is already registered with another account.");
            }

            user.setEmail(request.getEmail());
        }

        // Update user details
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setDateOfBirth(request.getDateOfBirth());

        userRepository.save(user);

        // Audit Log
        auditLogService.saveLog(
                user.getEmail(),
                "PROFILE_UPDATE",
                "Profile details were updated.");

        // Fetch account
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found."));

        // Prepare response
        ProfileResponse response = new ProfileResponse();

        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setDateOfBirth(user.getDateOfBirth());
        response.setMemberSince(user.getCreatedAt());

        response.setAccountNumber(account.getAccountNumber());
        response.setAccountType(account.getAccountType().name());
        response.setBalance(account.getBalance());
        response.setStatus(account.getStatus().name());

        return response;
    }
}