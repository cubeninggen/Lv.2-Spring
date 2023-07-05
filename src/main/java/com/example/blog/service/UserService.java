package com.example.blog.service;

import com.example.blog.dto.LoginRequestDto;
import com.example.blog.dto.MessageResponseDto;
import com.example.blog.dto.SignUpRequestDto;
import com.example.blog.entity.User;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class UserService {
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<MessageResponseDto> createUser(SignUpRequestDto signUpRequestDto) {
        String username = signUpRequestDto.getUsername();
        String password = passwordEncoder.encode(signUpRequestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if(checkUsername.isPresent()){
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // 회원 등록
        User user = new User(username, password);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponseDto("회원가입 성공!" , HttpStatus.OK.toString()));
    }


    public ResponseEntity<MessageResponseDto> loginUser(@RequestBody LoginRequestDto requestDto) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("사용자가 없습니다.")
        );

        // 비밀번호 확인
        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성
        String token = jwtUtil.createToken(user.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AUTHORIZATION_HEADER, token);

        return new ResponseEntity<>(new MessageResponseDto("로그인 성공!", HttpStatus.OK.toString()), headers, HttpStatus.OK);
    }
}