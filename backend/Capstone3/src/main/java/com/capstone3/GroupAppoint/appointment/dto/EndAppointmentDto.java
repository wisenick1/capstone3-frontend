package com.capstone3.GroupAppoint.appointment.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndAppointmentDto extends BaseAppointmentDto{
    private MyEndAppointmentDto myDetail;
    private List<EndParticipantDto> EndParticipantDto;
}
