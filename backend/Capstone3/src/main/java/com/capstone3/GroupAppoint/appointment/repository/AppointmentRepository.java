package com.capstone3.GroupAppoint.appointment.repository;

import com.capstone3.GroupAppoint.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findById(Long appointmentId);
    List<Appointment> findByAppointmentDateBetween(LocalDate startDate, LocalDate endDate);
    List<Appointment> findByAppointmentTimeBetween(LocalTime startTime, LocalTime endTime);
}
