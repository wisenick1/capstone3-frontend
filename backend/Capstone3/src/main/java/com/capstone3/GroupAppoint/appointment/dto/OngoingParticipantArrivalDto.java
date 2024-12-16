package com.capstone3.GroupAppoint.appointment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OngoingParticipantArrivalDto {
    private Long userId;      //참가자Id
    private String profile;         //참가자프로필
    private Boolean isHost;         //약속주최자인지
    private Boolean isArrived;      //도착했는지
    private Boolean isOnTime;       //제때 도착 여부 판단 0 : 지각, 1 : 도착 - 지각 도착인지, 그냥 도착인지를 판단하기 위함
    private String estimatedArrivalTime;  //도착 예정 시간
}
