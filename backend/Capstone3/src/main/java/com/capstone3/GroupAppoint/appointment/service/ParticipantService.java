package com.capstone3.GroupAppoint.appointment.service;

import com.capstone3.GroupAppoint.appointment.dto.OngoingParticipantArrivalDto;
import com.capstone3.GroupAppoint.appointment.entity.Appointment;
import com.capstone3.GroupAppoint.game.GameParticipantDto;
import com.capstone3.GroupAppoint.kakao.auth.entity.User;

import java.time.LocalTime;
import java.util.List;

public interface ParticipantService {
    void addParticipant(User user, Appointment appointment, boolean host);
    void updateParticipant(Appointment appointment, String newParticipantIds);
    boolean findIsHostofAppointment(Appointment appointment, User user);
    //도착 예정 시간과 참여자
    List<OngoingParticipantArrivalDto> getParticipantsWithEstimatedArrival(Long appointmentId);
    String getEstimatedArrivalTime(Long userId, Appointment appointment);
    List<GameParticipantDto> calculateLateTimesAndGetLateParticipants(LocalTime appointmentTime);
}
