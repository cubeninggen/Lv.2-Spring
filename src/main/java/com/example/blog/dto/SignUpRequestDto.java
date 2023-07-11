package com.example.blog.dto;

import com.example.blog.entity.User;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class SignUpRequestDto {
    @Pattern(regexp = "[a-z0-9]{4,10}")
    private String username;
    @Pattern(regexp = "[a-zA-Z0-9]{8,15}")
    private String password;

    private Set<User.Role> roles; // 사용자 역할 정보

    public SignUpRequestDto(String username, String password, Set<User.Role> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;

    }
}
