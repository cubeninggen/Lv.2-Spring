package com.example.blog.controller;

import com.example.blog.dto.*;
import com.example.blog.entity.User;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<MessageResponseDto> createUser(@RequestBody SignUpRequestDto signUpRequestDto) {
        String username = signUpRequestDto.getUsername();
        String password = passwordEncoder.encode(signUpRequestDto.getPassword());

        User user = new User(username, password);
        user.setRoles(signUpRequestDto.getRoles()); // 역할 설정 및 isAdmin 값 설정

        userService.createUser(user);

        return ResponseEntity.ok(new MessageResponseDto("회원가입 성공!", HttpStatus.OK.toString()));
    }

    @PostMapping("/login")
    public ResponseEntity<MessageResponseDto> loginUser(@RequestBody LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("사용자가 없습니다.");
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성
        String token = jwtUtil.createToken(user);

        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AUTHORIZATION_HEADER, token);

        return new ResponseEntity<>(new MessageResponseDto("로그인 성공!", HttpStatus.OK.toString()), headers, HttpStatus.OK);
    }

    @PatchMapping("/users/{userId}/admin")
    public ResponseEntity<MessageResponseDto> grantAdminRole(@PathVariable Long userId, @RequestHeader("Authorization") String tokenValue) {
        if (!jwtUtil.validateToken(tokenValue)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        userService.grantAdminRole(userId);
        return ResponseEntity.ok(new MessageResponseDto("관리자 권한이 부여되었습니다.", HttpStatus.OK.toString()));
    }
}
