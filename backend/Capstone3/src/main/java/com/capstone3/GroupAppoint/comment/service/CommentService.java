package com.capstone3.GroupAppoint.comment.service;

import com.capstone3.GroupAppoint.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {
    // 댓글 생성
    Long createComment(Long userId, Long appointmentId, String content);
    // 댓글 수정
    void updateComment(Long userId, Long commentId, String content);
    // 댓글 권한
    Boolean isMyComment(Long userId, Long commentId);
    // 댓글 삭제
    void deleteComment(Long commentId);
    // 댓글 리스트
    List<CommentDto> getCommentList(Long appointmentId);
}
