package com.capstone3.GroupAppoint.appointment.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAppointmentDto {

    private String title;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime appointmentTime;
    private Double latitude;
    private Double longitude;
    private String location;
    private String address;
    private String participantIds;

}
