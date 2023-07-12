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

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public ResponseEntity<MessageResponseDto> createUser(SignUpRequestDto signUpRequestDto) {
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
        Set<User.Role> roles = user.getRoles();
        List<String> roleStrings = roles.stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        String token = jwtUtil.createToken(username, roleStrings);


        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AUTHORIZATION_HEADER, token);

        return new ResponseEntity<>(new MessageResponseDto("로그인 성공!", HttpStatus.OK.toString()), headers, HttpStatus.OK);
    }
    // 사용자명(username)이 데이터베이스에 존재하는지 확인
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    // 새로운 사용자를 생성하여 데이터베이스에 저장
    public void createUser(User user) {
        userRepository.save(user);
    }
    //  사용자명(username)에 해당하는 사용자를 가져옴 
    public User getUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        return userOptional.orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));
    }
}
