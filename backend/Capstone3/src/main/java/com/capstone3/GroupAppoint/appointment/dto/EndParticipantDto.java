package com.capstone3.GroupAppoint.appointment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndParticipantDto {
    private Long userId;
    private String profile;
    private Long lateTime;
    private Boolean isOnTime;
}
