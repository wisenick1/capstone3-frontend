package com.capstone3.GroupAppoint.appointment.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentListDto extends BaseAppointmentDto{
    private Long diffHours;
    private Long diffMinutes;
    private Integer participantCount;
    private List<AppointmentListParticipantDto> participantList;
}
