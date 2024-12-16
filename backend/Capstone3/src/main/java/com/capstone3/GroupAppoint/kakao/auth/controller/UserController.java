package com.capstone3.GroupAppoint.kakao.auth.controller;

import com.capstone3.GroupAppoint.kakao.auth.dto.UserInfoResponseDto;
import com.capstone3.GroupAppoint.kakao.auth.entity.User;
import com.capstone3.GroupAppoint.kakao.auth.entity.Friend;
import com.capstone3.GroupAppoint.kakao.auth.service.UserService;
import com.capstone3.GroupAppoint.kakao.auth.service.FriendService;
import com.capstone3.GroupAppoint.kakao.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final FriendService friendService;
    private final UserRepository userRepository;

    // 사용자 정보 조회
    @GetMapping("/id")
    public ResponseEntity<UserInfoResponseDto> getAccountId(HttpServletRequest request) {
        String email = request.getRemoteUser();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            log.info("account.get().getAccountId(): " + user.get().getUserId());
            log.info("account.get().getName(): " + user.get().getName());
            log.info("account.get().getProfile(): " + user.get().getProfile());

            UserInfoResponseDto userInfoResponseDto = new UserInfoResponseDto(user.get().getUserId(), user.get().getName(), user.get().getProfile());
            return ResponseEntity.ok(userInfoResponseDto);
        }
    }

    // 친구 추가
    @PostMapping("/{userId}/friend")
    public ResponseEntity<Friend> addFriend(@PathVariable Long userId, @RequestParam String friendName) {
        try {
            Friend friend = friendService.addFriend(userId, friendName);
            return ResponseEntity.ok(friend);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 특정 사용자의 친구 목록 조회
    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<Friend>> getFriends(@PathVariable Long userId) {
        try {
            List<Friend> friends = friendService.findAllFriends(userId);
            return ResponseEntity.ok(friends);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 친구 삭제
    @DeleteMapping("/friend/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long friendId) {
        try {
            friendService.deleteFriend(friendId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

