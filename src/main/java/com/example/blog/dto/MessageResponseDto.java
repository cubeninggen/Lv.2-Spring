package com.example.blog.dto;

import lombok.Getter;


// 응답 관련 DTO
@Getter
public class MessageResponseDto {
    private String message;
    private String statusCode;

    public MessageResponseDto(String message, String statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}