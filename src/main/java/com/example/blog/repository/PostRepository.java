package com.example.blog.repository;

import com.example.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//@Repository
//JpaRepository를 상속받으면 자동으로 빈 등록 가능
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    boolean existsByIdAndUsername(Long commentId, String username);

}