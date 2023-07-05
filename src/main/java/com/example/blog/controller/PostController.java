package com.example.blog.controller;

import com.example.blog.dto.MessageResponseDto;
import com.example.blog.dto.PostRequestDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public PostResponseDto createPost(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue, @RequestBody PostRequestDto requestDto) {
        return postService.createPost(tokenValue, requestDto);
    }
    //  요청 헤더에서 JWT 토큰 값을 매개변수로 받아온다.
    //  JwtUtil.AUTHORIZATION_HEADER 는 JWT 토큰이 들어있는 헤더의 이름을 나타내는 상수이다.

    // 게시글 수정 API
    @PutMapping("/posts/{id}")
    public PostResponseDto updatePost(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue, @PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        return postService.updatePost(tokenValue, id, requestDto);
    }

    // 게시글 삭제 API
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<MessageResponseDto> deletePost(@RequestHeader(JwtUtil.AUTHORIZATION_HEADER) String tokenValue, @PathVariable Long id) {
        return postService.deletePost(tokenValue, id);
    }
}
