package com.capstone3.GroupAppoint.appointment.entity;

import com.capstone3.GroupAppoint.kakao.auth.entity.User;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="appointment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Appointment appointment;

    @Column(name = "is_host")
    private Boolean isHost;

    @Column(name = "is_arrived")
    @ColumnDefault("false")
    private Boolean isArrived;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", referencedColumnName = "userId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "arrival_time")
    private LocalTime arrivalTime;

    @Column(name = "late_time")
    @ColumnDefault("0")
    private Long lateTime;

}
