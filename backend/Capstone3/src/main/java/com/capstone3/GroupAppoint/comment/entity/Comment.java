package com.capstone3.GroupAppoint.comment.entity;

import com.capstone3.GroupAppoint.appointment.entity.Appointment;
import com.capstone3.GroupAppoint.appointment.entity.Participant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    // 약속
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="appointment_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="participant_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Participant participant;

    private String content;

    @Column(name = "register_dt")
    private LocalDateTime registerTime;

    @Column(name = "update_dt")
    private LocalDateTime updateTime;
}
