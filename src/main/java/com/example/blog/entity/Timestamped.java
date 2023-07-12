package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


// POST 및 Comment 에 상속되어 기능작용 ( 추상 클래스 )
@Getter
@MappedSuperclass // DB와 매핑되지 않음
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamped {

    @CreatedDate
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column()
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedAt;

}