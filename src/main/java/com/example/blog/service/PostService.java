package com.example.blog.service;

import com.example.blog.dto.*;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.jwt.JwtUtil;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    public PostService(PostRepository postRepository, CommentRepository commentRepository, JwtUtil jwtUtil, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // 게시물 생성
    public PostResponseDto createPost(String tokenValue, PostRequestDto requestDto) {
        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰 오류");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();

        Post post = new Post();
        post.setTitle(requestDto.getTitle());
        post.setUsername(username);
        post.setContent(requestDto.getContent());

        Post savedPost = postRepository.save(post);
        return new PostResponseDto(savedPost);
    }

    // 모든 게시물 조회
    public List<PostResponseDto> getPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(PostResponseDto::new)
                .toList();
    }

    // 특정 게시물 조회
    public PostResponseDto getPost(Long id) {
        Post post = findPost(id);
        return new PostResponseDto(post);
    }

    // 게시물 수정
    @Transactional
    public PostResponseDto updatePost(String tokenValue, Long id, PostRequestDto requestDto) {
        Post post = findPost(id);

        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰 오류");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();
        boolean isAdmin = jwtUtil.isAdmin(token);

        if (!isAdmin && !post.getUsername().equals(username)) {
            throw new IllegalArgumentException("해당 게시물을 수정할 권한이 없습니다.");
        }

        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());

        return new PostResponseDto(post);
    }

    // 게시물 삭제
    public ResponseEntity<MessageResponseDto> deletePost(String tokenValue, Long id) {
        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰 오류");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();
        boolean isAdmin = jwtUtil.isAdmin(token);

        Post post = findPost(id);

        if (!isAdmin && !post.getUsername().equals(username)) {
            throw new IllegalArgumentException("게시물 삭제 권한이 없습니다.");
        }

        postRepository.delete(post);

        return ResponseEntity.ok(new MessageResponseDto("게시물 삭제 성공", "200"));
    }

    // 게시물 조회 (내부 사용)
    private Post findPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("선택한 포스트가 존재하지 않습니다."));
    }

    // 댓글 작성
    @Transactional
    public CommentResponseDto createComment(String tokenValue, Long postId, CommentRequestDto requestDto) {
        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰 오류");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();

        Post post = findPostById(postId);

        Comment comment = new Comment(requestDto.getContent(), username, post);
        Comment savedComment = commentRepository.save(comment);

        // Post 엔티티에 댓글 추가
        post.getComments().add(savedComment); // 댓글을 직접 추가
        postRepository.save(post);

        return new CommentResponseDto(savedComment);
    }




    // 댓글 수정
    @Transactional
    public CommentResponseDto updateComment(String tokenValue, Long commentId, CommentRequestDto requestDto) {
        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰 오류");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();
        boolean isAdmin = jwtUtil.isAdmin(token);

        Comment comment = findCommentById(commentId);

        if (!isAdmin && !comment.getUsername().equals(username)) {
            throw new IllegalArgumentException("해당 댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(requestDto.getContent());

        return new CommentResponseDto(comment);
    }

    // 댓글 삭제
    public ResponseEntity<MessageResponseDto> deleteComment(String tokenValue,Long postId, Long commentId) {
        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰 오류");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();
        boolean isAdmin = jwtUtil.isAdmin(token);

        Comment comment = findCommentById(commentId);

        if (!isAdmin && !comment.getUsername().equals(username)) {
            throw new IllegalArgumentException("해당 댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);

        return ResponseEntity.ok(new MessageResponseDto("댓글 삭제 성공", "200"));
    }

    // Helper 메소드: 게시물 조회
    private Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("선택한 게시물이 존재하지 않습니다."));
    }

    // Helper 메소드: 댓글 조회
    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("선택한 댓글이 존재하지 않습니다."));
    }

    // 게시물에 대한 권한 확인
    public boolean hasPermissionForPost(String tokenValue, Long postId) {
        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰 오류");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();

        Optional<User> userOptional = userRepository.findByUsername(username);
        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        boolean isAdmin = user.getRoles().contains(User.Role.ADMIN);

        if (isAdmin) {
            return true;
        }

        Post post = findPost(postId);

        return post.getUsername().equals(username);
    }

    // 댓글에 대한 권한 확인
    public boolean hasPermissionForComment(String tokenValue, Long commentId) {
        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("토큰 오류");
        }

        Claims info = jwtUtil.getUserInfoFromToken(token);
        String username = info.getSubject();
        boolean isAdmin = jwtUtil.isAdmin(token);

        Comment comment = findCommentById(commentId);
        Post post = comment.getPost(); // 댓글이 속한 게시물 가져오기

        return isAdmin || post.getUsername().equals(username);
    }

}
