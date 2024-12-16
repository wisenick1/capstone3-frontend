package com.capstone3.GroupAppoint.kakao.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "friend")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // 외래 키 설정
    private User user;

    @Column(nullable = false)
    private String friendName;

    public Friend(User user, String friendName) {
        this.user = user;
        this.friendName = friendName;
    }
}
