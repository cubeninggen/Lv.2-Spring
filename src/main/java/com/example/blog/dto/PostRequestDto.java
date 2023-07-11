package com.example.blog.dto;

import com.example.blog.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
    private String title;
    private String content;

    public PostRequestDto(){

    }
}