package com.example.blog.dto;

import lombok.Getter;


// 댓글 DB 요청 DTO
@Getter
public class CommentRequestDto {
    private String content;

    public CommentRequestDto(){

    }
}
