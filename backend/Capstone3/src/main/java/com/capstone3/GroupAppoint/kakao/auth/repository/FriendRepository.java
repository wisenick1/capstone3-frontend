package com.capstone3.GroupAppoint.kakao.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.capstone3.GroupAppoint.kakao.auth.entity.Friend;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}