package com.capstone3.GroupAppoint.appointment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @Column(name = "title")
    private String title;

    @Column(name = "appointment_date")
    private LocalDate appointmentDate;

    @Column(name = "appointment_time")
    private LocalTime appointmentTime;

    // 위도
    @Column(name = "latitude")
    private Double latitude;

    // 경도
    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location")
    private String location;

    // 주소
    @Column(name = "address")
    private String address;

    @ColumnDefault("0")
    private int state; // 약속 상태    0 : 약속 한참 전, 1 : 약속 한시간 전(GPS)공유, 2: 약속 끝

}
