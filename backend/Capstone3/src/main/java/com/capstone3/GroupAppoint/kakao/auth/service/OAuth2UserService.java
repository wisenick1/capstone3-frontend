package com.capstone3.GroupAppoint.kakao.auth.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

import com.capstone3.GroupAppoint.kakao.auth.entity.User;
import com.capstone3.GroupAppoint.kakao.auth.repository.UserRepository;


@Slf4j
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public OAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 카카오 응답 데이터 확인
        log.info("카카오 로그인 응답: {}", oAuth2User.getAttributes());

        // 응답 데이터 파싱
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Long kakaoId = Long.valueOf(attributes.get("id").toString());

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;

        String email = kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
        String name = profile != null ? (String) profile.get("nickname") : null;
        String profileImage = profile != null ? (String) profile.get("profile_image_url") : null;


        log.info("Parsed User Info: id={}, email={}, name={}, profileImage={}", kakaoId, email, name, profileImage);

        // 사용자 DB 저장/업데이트
        User user = userRepository.findById(kakaoId).orElse(null);
        if (user == null) {
            user = User.builder()
                    .userId(kakaoId)
                    .name(name)
                    .email(email != null ? email : "unknown@domain.com") // 이메일 기본값 설정
                    .profile(profileImage)
                    .accumulatedTime(0) // 기본 값 설정
                    .build();
            userRepository.save(user);
            log.info("새 사용자 저장: {}", user);
        } else {
            log.info("기존 사용자 조회: {}", user);
        }

        // 반환 객체 생성
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "id");
    }
}
