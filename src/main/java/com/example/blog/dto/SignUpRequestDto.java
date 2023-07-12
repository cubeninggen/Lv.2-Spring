package com.example.blog.dto;

import com.example.blog.entity.User;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.util.Set;

// 회원가입 요청 DTO // 상태값만 반환하면 되기에 Response 는 필요 X // 로그인도 마찬가지
@Getter
public class SignUpRequestDto {
    @Pattern(regexp = "[a-z0-9]{4,10}")
    private String username;

    @Pattern(regexp = "[a-zA-Z0-9]{8,15}")
    private String password;

    private Set<User.Role> roles;

    private boolean isAdmin;

    public SignUpRequestDto(String username, String password, Set<User.Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.isAdmin = roles.contains(User.Role.ADMIN);
    }
}
