package com.capstone3.GroupAppoint.kakao.auth.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.capstone3.GroupAppoint.kakao.auth.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @SuppressWarnings("null")
    @EntityGraph(attributePaths = "friends")
    Optional<User> findById(Long userId); // 사용자 + 친구 조회
}