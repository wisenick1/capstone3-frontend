package com.capstone3.GroupAppoint.comment.controller;

import com.capstone3.GroupAppoint.comment.dto.CommentDto;
import com.capstone3.GroupAppoint.comment.dto.CreateCommentDto;
import com.capstone3.GroupAppoint.comment.dto.UpdateCommentDto;
import com.capstone3.GroupAppoint.comment.service.CommentService;
import groovy.util.logging.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@lombok.extern.slf4j.Slf4j
@Slf4j
@RestController
@RequestMapping("/appoint/{appointment_Id}/comment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createComment(
            HttpServletRequest request,
            @PathVariable("appointment_Id") Long appointmentId,
            @RequestBody CreateCommentDto createCommentDto
    ) {
        Map<String, String> response = new HashMap<>();

        try {
            // 사용자 인증
            //String userIdString = request.getRemoteUser();
            //Long userId = Long.parseLong(userIdString);
            Long userId = 1L;

            // 댓글 생성
            Long commentId = commentService.createComment(userId, appointmentId, createCommentDto.getContent());

            String successMessage = String.format("commentId: %d, 댓글이 생성되었습니다!", commentId);
            log.info(successMessage);
            response.put("message", successMessage);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("댓글 생성 실패: appointmentId {}에 대한 유효하지 않은 요청입니다.", appointmentId, e);
            response.put("message", "유효하지 않은 요청입니다. appointmentId를 확인해주세요.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("댓글 생성 실패: appointmentId {}", appointmentId, e);
            response.put("message", "서버 오류가 발생했습니다. 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{comment_id}")
    public ResponseEntity<Map<String, String>> updateComment(
            HttpServletRequest request,
            @PathVariable("appointment_Id") Long appointmentId,
            @PathVariable("comment_id") Long commentId,
            @RequestBody UpdateCommentDto updateCommentDto
    ) {
        Map<String, String> response = new HashMap<>();

        try {
            // 사용자 인증
            //String userIdString = request.getRemoteUser();
            //Long userId = Long.parseLong(userIdString);

            Long userId = 1L;

            // 내 댓글 여부 확인
            if (!commentService.isMyComment(userId, commentId)) {
                log.warn("내 댓글이 아닙니다.");
                response.put("message", "자신의 댓글만 수정할 수 있습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 댓글 수정
            commentService.updateComment(userId, commentId, updateCommentDto.getContent());

            String successMessage = String.format("commentId: %d, 댓글이 수정되었습니다!", commentId);
            log.info(successMessage);
            response.put("message", successMessage);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("수정 실패: commentId {}는 존재하지 않는 commentId 입니다.", commentId, e);
            response.put("message", "존재하지 않는 commentId입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("댓글 수정 실패: commentId {}", commentId, e);
            response.put("message", "서버 오류가 발생했습니다. 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<Map<String, String>> deleteComment(
            HttpServletRequest request,
            @PathVariable("appointment_Id") Long appointmentId,
            @PathVariable("comment_id") Long commentId
    ) {
        Map<String, String> response = new HashMap<>();

        try {
            // 사용자 인증 (테스트용 userId 설정)
            //String userIdString = request.getRemoteUser();
            //Long userId = Long.parseLong(userIdString);

            Long userId = 1L;

            // 내 댓글 여부 확인
            if (!commentService.isMyComment(userId, commentId)) {
                log.warn("내 댓글이 아닙니다.");
                response.put("message", "자신의 댓글만 삭제할 수 있습니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 댓글 삭제
            commentService.deleteComment(commentId);
            String successMessage = String.format("commentId: %d 삭제되었습니다.", commentId);
            log.info(successMessage);
            response.put("message", successMessage);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("삭제 실패: commentId {}는 존재하지 않는 commentId 입니다.", commentId, e);
            response.put("message", "존재하지 않는 commentId입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            log.error("댓글 삭제 실패: commentId {}", commentId, e);
            response.put("message", "서버 오류가 발생했습니다. 다시 시도해주세요.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCommentsForAppointment(
            @PathVariable("appointment_Id") Long appointmentId
    ) {
        try {
            List<CommentDto> comments = commentService.getCommentList(appointmentId);

            // 응답 메시지 생성
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("message", "댓글 목록 조회 성공");
            response.put("data", comments);

            if (comments.isEmpty()) {
                response.put("message", "댓글이 없습니다.");
            }

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // 예외 발생 시 오류 메시지 반환
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("message", "잘못된 요청입니다. 유효하지 않은 appointmentId입니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            // 기타 예외 발생 시 오류 메시지 반환
            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("message", "댓글 조회 중 문제가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
