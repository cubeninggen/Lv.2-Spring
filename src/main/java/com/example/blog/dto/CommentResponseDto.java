package com.example.blog.dto;

import com.example.blog.entity.Comment;
import lombok.Getter;
import java.time.LocalDateTime;

// 댓글 저장 및 반환 DTO
@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getUsername();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
