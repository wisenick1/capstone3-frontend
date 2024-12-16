package com.capstone3.GroupAppoint.kakao.auth.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class UserInfoResponseDto {
    private long id;
    private String name;
    private String profile;
}

