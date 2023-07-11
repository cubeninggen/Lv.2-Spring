package com.example.blog.service;

import com.example.blog.dto.SignUpRequestDto;
import com.example.blog.dto.LoginRequestDto;
import com.example.blog.dto.MessageResponseDto;
import com.example.blog.entity.User;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<MessageResponseDto> createUser(@RequestBody SignUpRequestDto signUpRequestDto) {
        String username = signUpRequestDto.getUsername();
        String password = signUpRequestDto.getPassword();

        // 회원 중복 확인
        if (existsByUsername(username)) {
            return ResponseEntity.badRequest().body(new MessageResponseDto("중복된 사용자가 존재합니다.", HttpStatus.BAD_REQUEST.toString()));
        }

        User user = new User(username, passwordEncoder.encode(password));
        user.setRoles(signUpRequestDto.getRoles());

        createUser(user);

        return ResponseEntity.ok().body(new MessageResponseDto("회원가입 성공!", HttpStatus.OK.toString()));
    }



    @PostMapping("/login")
    public ResponseEntity<MessageResponseDto> loginUser(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 사용자 확인
        if (username == null) {
            throw new IllegalArgumentException("사용자가 없습니다.");
        }

        User user = getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("사용자가 없습니다.");
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 생성
        String token = jwtUtil.createToken(username);

        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AUTHORIZATION_HEADER, token);

        return new ResponseEntity<>(new MessageResponseDto("로그인 성공!", HttpStatus.OK.toString()), headers, HttpStatus.OK);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));
    }


    public void grantAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // ADMIN 권한 부여
        user.getRoles().add(User.Role.ADMIN);

        userRepository.save(user);
    }

    public boolean hasRole(Long userId, User.Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        return user.getRoles().contains(role);
    }
}
