package com.capstone3.GroupAppoint.appointment.service;

import com.capstone3.GroupAppoint.appointment.dto.AppointmentListDto;
import com.capstone3.GroupAppoint.appointment.dto.EndAppointmentDto;
import com.capstone3.GroupAppoint.appointment.dto.OngoingAppointmentDto;
import com.capstone3.GroupAppoint.appointment.entity.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    Long createAppointment(Long userId, String title, LocalDate appointmentDate, LocalTime appointmentTime, Double latitude, Double longitude, String location, String address, String participantIds);

    void updateAppointmentAndParticipant(Appointment appointment, String title, LocalDate appointmentDate, LocalTime appointmentTime, Double latitude, Double longitude, String location, String address,String newParticipantIds);

    void deleteAppointment(Appointment appointment);

    OngoingAppointmentDto getAppointmentDetails(Long AppointmentId);

    EndAppointmentDto getEndAppointmentDetails(Long AppointmentId, Long userId);
    //날짜에 따른 약속 조회
    List<AppointmentListDto> getAppointmentList(Long userId, LocalDate appointmentDate);
    //약속 있음없음?
    Appointment getAppointmentById(Long appointmentId);
    //약속 시간 차이(시간)
    long getTimeHoursDifference(LocalDate appointmentDate, LocalTime planTime);
    //약속 시간 차이(분)
    long getTimeMinutesDifference(LocalDate appointmentDate, LocalTime planTime);
    //약속 state 수정 - entity 설명 참조
    void updateAppointmentStatus(Appointment appointment);
    //state에 따른 약속 조회
    List<AppointmentListDto> getAllAppointmentsByState(Long userId, int state);
}
