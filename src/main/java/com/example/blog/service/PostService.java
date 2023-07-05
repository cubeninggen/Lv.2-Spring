package com.example.blog.service;

import com.example.blog.dto.MessageResponseDto;
import com.example.blog.dto.PostRequestDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.entity.Post;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.repository.PostRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    private PostRepository postRepository;
    private JwtUtil jwtUtil;

    public PostService(PostRepository postRepository, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 게시물 생성 메소드
     * @param tokenValue JWT 토큰 값
     * @param requestDto 게시물 생성 요청 DTO
     * @return 생성된 게시물의 응답 DTO
     */
    public PostResponseDto createPost(String tokenValue, PostRequestDto requestDto) {
        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Token Error");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();

        Post post = new Post(requestDto, username);
        Post savePost = postRepository.save(post);
        PostResponseDto postResponseDto = new PostResponseDto(savePost);
        return postResponseDto;
    }

    /**
     * 모든 게시물 조회 메소드
     * @return 모든 게시물의 응답 DTO 리스트
     */
    public List<PostResponseDto> getPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(PostResponseDto::new).toList();
    }

    /**
     * 게시물 조회 메소드
     * @param id 게시물 ID
     * @return 조회된 게시물의 응답 DTO
     */
    public PostResponseDto getPost(Long id) {
        Post post = findPost(id);
        return new PostResponseDto(post);
    }

    /**
     * 게시물 수정 메소드
     * @param tokenValue JWT 토큰 값
     * @param id 게시물 ID
     * @param requestDto 게시물 수정 요청 DTO
     * @return 수정된 게시물의 응답 DTO
     */
    @Transactional
    public PostResponseDto updatePost(String tokenValue, Long id, PostRequestDto requestDto) {
        Post post = findPost(id);

        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Token Error");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();

        if (!username.equals(post.getAuthor())) {
            throw new IllegalArgumentException("해당 게시글을 작성한 사용자가 아닙니다.");
        }

        post.update(requestDto);

        return new PostResponseDto(post);
    }

    /**
     * 게시물 삭제 메소드
     * @param tokenValue JWT 토큰 값
     * @param id 게시물 ID
     * @return 삭제 결과에 대한 응답 ResponseEntity
     */
    public ResponseEntity<MessageResponseDto> deletePost(String tokenValue, Long id) {
        Post post = findPost(id);

        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("Token Error");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();

        if (!username.equals(post.getAuthor())) {
            throw new IllegalArgumentException("해당 게시글을 작성한 사용자가 아닙니다.");
        }

        postRepository.delete(post);

        return new ResponseEntity<MessageResponseDto>(new MessageResponseDto("게시글 삭제 성공", "200"), HttpStatus.OK);
    }

    /**
     * 게시물 조회 메소드 (내부적으로 사용)
     * @param id 게시물 ID
     * @return 조회된 게시물
     * @throws IllegalArgumentException 선택한 게시물이 존재하지 않을 경우 예외 발생
     */
    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 포스트는 존재하지 않습니다.")
        );
    }
}
