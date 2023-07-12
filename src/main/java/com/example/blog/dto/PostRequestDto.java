package com.example.blog.dto;

import lombok.Getter;


// 게시물 -> 서버로 부터 요청 DTO
@Getter
public class PostRequestDto {
    private String title;
    private String content;

    public PostRequestDto(){

    }
}