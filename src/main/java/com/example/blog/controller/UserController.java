package com.example.blog.controller;

import com.example.blog.dto.*;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


// 회원 관련 Controller
@RequestMapping("/api/auth")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    // 회원가입 API
    @PostMapping("/sign-up")
    public ResponseEntity<MessageResponseDto> createUser(@RequestBody @Valid SignUpRequestDto signUpRequestDto) {
        return userService.createUser(signUpRequestDto);
    }
    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<MessageResponseDto> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        return userService.loginUser(loginRequestDto);
    }

}