package com.capstone3.GroupAppoint.appointment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class MyEndAppointmentDto {
    private Long userId;
    private String profile;
    private Boolean isOnTime;
    private LocalTime arrivalTime;     //도착 시간
    private Long lateTime;             //늦은 시간
    private Integer fineMoney;         //벌금
}
