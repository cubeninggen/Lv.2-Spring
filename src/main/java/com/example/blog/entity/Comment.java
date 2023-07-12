package com.example.blog.entity;

import com.example.blog.dto.CommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


// 댓글 DB 저장값 -> DB와 매핑된다.
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "comment")
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String username;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public Comment(CommentRequestDto requestDto, String username, Post post) {
        this.content = requestDto.getContent();
        this.username = username;
        this.post = post;
    }

    public Comment(String content, String username, Post post) {
        super();
        this.content = content;
        this.username = username;
        this.post = post;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
