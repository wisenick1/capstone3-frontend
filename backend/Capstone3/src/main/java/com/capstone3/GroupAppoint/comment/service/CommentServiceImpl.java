package com.capstone3.GroupAppoint.comment.service;


import com.capstone3.GroupAppoint.appointment.entity.Appointment;
import com.capstone3.GroupAppoint.appointment.entity.Participant;
import com.capstone3.GroupAppoint.appointment.repository.AppointmentRepository;
import com.capstone3.GroupAppoint.appointment.repository.ParticipantRepository;
import com.capstone3.GroupAppoint.appointment.service.AppointmentService;
import com.capstone3.GroupAppoint.comment.dto.CommentDto;
import com.capstone3.GroupAppoint.comment.entity.Comment;
import com.capstone3.GroupAppoint.comment.repository.CommentRepository;
import com.capstone3.GroupAppoint.kakao.auth.entity.User;
import com.capstone3.GroupAppoint.kakao.auth.service.UserService;
import groovy.util.logging.Slf4j;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);
    private final AppointmentRepository appointmentRepository;
    private final ParticipantRepository participantRepository;
    private final CommentRepository commentRepository;
    private final AppointmentService appointmentService;
    private final UserService userService;

    @Override
    public Long createComment(Long userId, Long appointmentId, String content) {
        User user = userService.findByUserId(userId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("appointment not found"));

        Participant participant = participantRepository.findByAppointmentAndUser(appointment, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 약속에 참가자의 정보가 없습니다."));

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        Comment comment = Comment.builder()
                .participant(participant)
                .appointment(appointment)
                .content(content)
                .registerTime(now)
                .updateTime(now)
                .build();
        commentRepository.save(comment);
        log.info("{} 님이 댓글이 등록되었습니다", user.getUserId());
        return comment.getCommentId();
    }

    @Override
    public void updateComment(Long userId, Long commentId, String content) {
        Comment comment = getCommentById(commentId);

        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        comment.setContent(content);
        comment.setUpdateTime(now);
        commentRepository.save(comment);
        log.info("{} 님이 댓글이 수정되었습니다", userId);
    }

    @Override
    public Boolean isMyComment(Long userId, Long commentId) {
        Comment comment = getCommentById(commentId);
        return comment.getParticipant().getUser().getUserId().equals(userId);
    }

    @Override
    public void deleteComment(Long commentId) {
        Comment comment = getCommentById(commentId);
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> getCommentList(Long appointmentId) {
        // 약속 조회
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);

        // 댓글 조회
        List<Comment> comments = commentRepository.findByAppointment(appointment);

        // Comment -> CommentDto로 변환
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = new CommentDto(
                    comment.getCommentId(),
                    comment.getParticipant().getUser().getProfile(),
                    comment.getContent(),
                    comment.getUpdateTime()
            );
            commentDtos.add(commentDto);
        }

        return commentDtos;
    }


    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 정보가 없음"));
    }
}
