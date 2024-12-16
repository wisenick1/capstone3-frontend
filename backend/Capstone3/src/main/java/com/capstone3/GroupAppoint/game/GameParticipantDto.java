package com.capstone3.GroupAppoint.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameParticipantDto {
    private Long userId;       // 사용자 ID
    private Long lateTime;     // 지각 시간 (분)
}
