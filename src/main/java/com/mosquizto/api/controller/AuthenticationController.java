package com.mosquizto.api.controller;

import com.mosquizto.api.dto.request.SignInRequest;
import com.mosquizto.api.dto.response.TokenResponse;
import com.mosquizto.api.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> access(@Valid @RequestBody SignInRequest signIndata) {
        return new ResponseEntity<>(this.authenticationService.authenticate(signIndata), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(HttpServletRequest request) {
        return new ResponseEntity<>(this.authenticationService.refreshToken(request), HttpStatus.OK);
    }

}
