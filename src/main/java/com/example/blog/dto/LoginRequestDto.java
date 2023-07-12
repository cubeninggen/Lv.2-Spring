package com.example.blog.dto;

import lombok.Getter;
    // 로그인 요청 DTO
    @Getter
    public class LoginRequestDto {
    private String username;
    private String password;

        public LoginRequestDto() {

        }
    }