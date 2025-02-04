package com.example.outburst.controller;

import com.example.outburst.payload.request.LoginRequest;
import com.example.outburst.payload.request.SignUpRequest;
import com.example.outburst.payload.response.JWTTokenSuccessResponse;
import com.example.outburst.payload.response.MessageResponse;
import com.example.outburst.security.JWTProvider;
import com.example.outburst.security.SecurityConstants;
import com.example.outburst.service.UserService;
import com.example.outburst.validations.ResponseErrorValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    private final JWTProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final ResponseErrorValidator responseErrorValidator;
    private final UserService userService;

    public AuthController(JWTProvider jwtProvider, AuthenticationManager authenticationManager, ResponseErrorValidator responseErrorValidator, UserService userService) {
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.responseErrorValidator = responseErrorValidator;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody SignUpRequest signUpRequest, BindingResult bindingResult) {

        ResponseEntity<Object> errors = responseErrorValidator.mapValidationService(bindingResult);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        userService.createUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!!!"));
    }

}
