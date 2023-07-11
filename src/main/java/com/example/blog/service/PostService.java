package com.example.blog.service;

import com.example.blog.dto.*;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    public PostService(PostRepository postRepository, CommentRepository commentRepository, JwtUtil jwtUtil) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 게시물 생성 메소드
     *
     * @param requestDto 게시물 생성 요청 DTO
     * @return 생성된 게시물의 응답 DTO
     */
    public PostResponseDto createPost(String tokenValue, PostRequestDto requestDto){
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
     *
     * @return 모든 게시물의 응답 DTO 리스트
     */
    public List<PostResponseDto> getPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(PostResponseDto::new).toList();
    }

    /**
     * 게시물 조회 메소드
     *
     * @param id 게시물 ID
     * @return 조회된 게시물의 응답 DTO
     */
    public PostResponseDto getPost(Long id) {
        Post post = findPost(id);
        return new PostResponseDto(post);
    }

    /**
     * 게시물 수정 메소드
     *
     * @param tokenValue  JWT 토큰 값
     * @param id          게시물 ID
     * @param requestDto  게시물 수정 요청 DTO
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

        if (!username.equals(post.getUsername())) {
            throw new IllegalArgumentException("해당 게시글을 작성한 사용자가 아닙니다.");
        }

        post.update(requestDto);

        return new PostResponseDto(post);
    }

    /**
     * 게시물 삭제 메소드
     *
     * @param tokenValue JWT 토큰 값
     * @param id         게시물 ID
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

        if (!username.equals(post.getUsername())) {
            throw new IllegalArgumentException("해당 게시글을 작성한 사용자가 아닙니다.");
        }

        postRepository.delete(post);

        return new ResponseEntity<>(new MessageResponseDto("게시글 삭제 성공", "200"), HttpStatus.OK);
    }

    /**
     * 게시물 조회 메소드 (내부적으로 사용)
     *
     * @param id 게시물 ID
     * @return 조회된 게시물
     * @throws IllegalArgumentException 선택한 게시물이 존재하지 않을 경우 예외 발생
     */
    private Post findPost(Long id) {
        return postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 포스트는 존재하지 않습니다.")
        );
    }

    // 댓글 작성 메소드
    public CommentResponseDto createComment(String tokenValue, Long postId, CommentRequestDto requestDto) {
        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();

        Post post = findPostById(postId);

        Comment comment = new Comment(requestDto.getContent(), username, post);
        Comment savedComment = commentRepository.save(comment);

        return new CommentResponseDto(savedComment);
    }

    // 댓글 수정 메소드
    @Transactional
    public CommentResponseDto updateComment(String tokenValue, Long commentId, CommentRequestDto requestDto) {
        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();

        Comment comment = findCommentById(commentId);

        if (!comment.getUsername().equals(username)) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        comment.setContent(requestDto.getContent());

        return new CommentResponseDto(comment);
    }

    // 댓글 삭제 메소드
    public ResponseEntity<MessageResponseDto> deleteComment(String tokenValue, Long postId, Long commentId) {
        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다.");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();


        Comment comment = findCommentById(commentId);

        if (!comment.getUsername().equals(username)) {
            throw new IllegalArgumentException("작성자만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);

        return ResponseEntity.ok(new MessageResponseDto("댓글 삭제 성공", "200"));
    }

    // Helper method: Post 조회
    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("선택한 게시물이 존재하지 않습니다."));
    }

    // Helper method: Comment 조회
    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("선택한 댓글이 존재하지 않습니다."));
    }

    public boolean hasPermission(String token, Long postId, Long commentId) {
        Claims claims = jwtUtil.getUserInfoFromToken(token);
        String username = claims.getSubject();
        boolean isAdmin = jwtUtil.isAdmin(token);

        // 관리자는 모든 권한을 가지고 있음
        if (isAdmin) {
            return true;
        }

        // 게시물의 작성자와 현재 사용자가 일치하는 경우
        if (postRepository.existsByIdAndUsername(postId, username)) {
            return true;
        }
        // 댓글의 작성자와 현재 사용자가 일치하는 경우
        if (commentRepository.existsByIdAndUsername(commentId, username)) {
            return true;
        }

        // 권한이 없는 경우 false를 반환합니다.
        return false;
    }


}
