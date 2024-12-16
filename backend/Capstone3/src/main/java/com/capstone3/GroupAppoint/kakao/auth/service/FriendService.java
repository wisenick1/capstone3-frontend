package com.capstone3.GroupAppoint.kakao.auth.service;

import com.capstone3.GroupAppoint.kakao.auth.entity.Friend;
import com.capstone3.GroupAppoint.kakao.auth.entity.User;
import com.capstone3.GroupAppoint.kakao.auth.repository.FriendRepository;
import com.capstone3.GroupAppoint.kakao.auth.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    // 생성자 주입
    public FriendService(FriendRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    // 특정 User에 Friend 추가
    public Friend addFriend(Long userId, String friendName) {
        // User 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));

        // Friend 생성 및 저장
        Friend friend = new Friend(user, friendName);
        return friendRepository.save(friend);
    }

    // 특정 User의 모든 Friend 조회
    public List<Friend> findAllFriends(Long userId) {
        // User 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다."));
        return user.getFriends();
    }

    // Friend 삭제
    public void deleteFriend(Long friendId) {
        if (!friendRepository.existsById(friendId)) {
            throw new RuntimeException("존재하지 않는 친구입니다.");
        }
        friendRepository.deleteById(friendId);
    }
}
