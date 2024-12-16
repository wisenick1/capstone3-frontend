package com.capstone3.GroupAppoint.kakao.auth.service;

import com.capstone3.GroupAppoint.kakao.auth.entity.User;
import com.capstone3.GroupAppoint.kakao.auth.entity.Friend;
import com.capstone3.GroupAppoint.kakao.auth.repository.UserRepository;
import com.capstone3.GroupAppoint.kakao.auth.repository.FriendRepository;
import com.capstone3.GroupAppoint.kakao.auth.service.FriendService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final FriendService friendService;

    // 생성자 주입
    public UserService(UserRepository userRepository, FriendService friendService) {
        this.userRepository = userRepository;
        this.friendService = friendService;
    }

    // User 조회
    public User findByUserId(Long userId) {
        // EntityGraph를 활용한 사용자 조회
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원"));
    }

    // User 생성
    public User createUser(String name, String email) {
        User user = new User(name, email);
        return userRepository.save(user);
    }

    public void addFriend(Long userId, String friendName) {
        friendService.addFriend(userId, friendName);
    }
}

