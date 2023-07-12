package com.example.blog.controller;

import com.example.blog.dto.*;
import com.example.blog.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 게시물 관련 Controller
@RequestMapping("/api")
@RestController
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 목록 조회 API
    @GetMapping("/posts")
    public List<PostResponseDto> getPosts() {
        return postService.getPosts();
    }

    // 선택한 게시글 조회 API
    @GetMapping("/posts/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }

    // 게시글 작성 API
    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestHeader("Authorization") String tokenValue, @RequestBody PostRequestDto requestDto) {
        return postService.createPost(tokenValue, requestDto);
    }

    // 게시글 수정 API
    @PutMapping("/posts/{id}")
    public PostResponseDto updatePost(@RequestHeader("Authorization") String tokenValue, @PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        if (postService.hasPermissionForPost(tokenValue, id)) {
            return postService.updatePost(tokenValue, id, requestDto);
        } else {
            throw new IllegalArgumentException("해당 게시물을 수정할 권한이 없습니다.");
        }
    }

    // 게시글 삭제 API
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<MessageResponseDto> deletePost(@RequestHeader("Authorization") String tokenValue, @PathVariable Long id) {
        if (postService.hasPermissionForPost(tokenValue, id)) {
            return postService.deletePost(tokenValue, id);
        } else {
            throw new IllegalArgumentException("해당 게시물을 삭제할 권한이 없습니다.");
        }
    }

    // 댓글 작성 API
    @PostMapping("/posts/{postId}/comments")
    public CommentResponseDto createComment(@RequestHeader("Authorization") String tokenValue, @PathVariable Long postId, @RequestBody CommentRequestDto requestDto) {
        return postService.createComment(tokenValue, postId, requestDto);
    }

    // 댓글 수정 APIw
    @PutMapping("/posts/{postId}/comments/{commentId}")
    public CommentResponseDto updateComment(@RequestHeader("Authorization") String tokenValue, @PathVariable Long postId, @PathVariable Long commentId, @RequestBody CommentRequestDto requestDto) {
        if (postService.hasPermissionForComment(tokenValue, commentId)) {
            return postService.updateComment(tokenValue, commentId, requestDto);
        } else {
            throw new IllegalArgumentException("해당 댓글을 수정할 권한이 없습니다.");
        }
    }

    // 댓글 삭제 API
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<MessageResponseDto> deleteComment(@RequestHeader("Authorization") String tokenValue, @PathVariable Long postId, @PathVariable Long commentId) {
        if (postService.hasPermissionForComment(tokenValue, commentId)) {
            return postService.deleteComment(tokenValue, postId, commentId);
        } else {
            throw new IllegalArgumentException("해당 댓글을 삭제할 권한이 없습니다.");
        }
    }

    // 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<MessageResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(new MessageResponseDto(e.getMessage(), "401"), HttpStatus.UNAUTHORIZED);
    }
}
