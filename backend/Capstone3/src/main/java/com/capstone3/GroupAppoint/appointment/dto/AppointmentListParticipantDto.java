package com.capstone3.GroupAppoint.appointment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentListParticipantDto {
    private Long userId;
    private String profile;
}
