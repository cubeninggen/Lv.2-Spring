package com.example.blog.repository;

import com.example.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
// USER 엔티티의 영속화 -> JPA 를 상속하므로 CRUD 기능을 함
// DB의 조작기능을 한다.
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    // 주어진 사용자 이름(username)에 해당하는 User 객체를 조회
    // Optional 형태로 반환

    boolean existsByUsername(String username);
    //  주어진 사용자 이름(username)이 데이터베이스에 존재하는지 여부를 확인
}