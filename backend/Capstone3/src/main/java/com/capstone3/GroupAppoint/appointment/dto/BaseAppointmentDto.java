package com.capstone3.GroupAppoint.appointment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter

public abstract class BaseAppointmentDto {
    private Long appointmentId;  //약속 ID
    private String title;           //약속명
    private LocalDate appointmentDate;     //약속일
    private LocalTime appointmentTime;     //약속시간
    private Double latitude;        //위도
    private Double longitude;       //경도
    private String location;        //위치
    private String address;         //주소
    private Integer state;          //약속 상태 0 : 약속 한참 전, 1 : 약속 한시간 전(GPS)공유, 2: 약속 끝
}
