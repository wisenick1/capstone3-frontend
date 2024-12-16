package com.capstone3.GroupAppoint.comment.repository;

import com.capstone3.GroupAppoint.appointment.entity.Appointment;
import com.capstone3.GroupAppoint.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findById(Long commentId);
    List<Comment> findByAppointment(Appointment appointment);
}
