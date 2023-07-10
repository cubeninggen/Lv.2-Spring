package com.example.blog.dto;

import com.example.blog.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.author = comment.getAuthor();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
