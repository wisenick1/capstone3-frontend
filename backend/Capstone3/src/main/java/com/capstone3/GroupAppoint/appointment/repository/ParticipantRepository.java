package com.capstone3.GroupAppoint.appointment.repository;

import com.capstone3.GroupAppoint.appointment.entity.Appointment;
import com.capstone3.GroupAppoint.appointment.entity.Participant;
import com.capstone3.GroupAppoint.kakao.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByAppointment(Appointment appointment);
    List<Participant> findByAppointment_AppointmentId(Long appointmentId);
    List<Participant> findByUser(User user);
    Optional<Participant> findByAppointmentAndUser(Appointment appointment, User user);
}
