package com.capstone3.GroupAppoint.appointment.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OngoingAppointmentDto extends BaseAppointmentDto{
    private Long diffDay;
    private Integer participantCount;
    private List<OngoingParticipantDto> participantList;
}
