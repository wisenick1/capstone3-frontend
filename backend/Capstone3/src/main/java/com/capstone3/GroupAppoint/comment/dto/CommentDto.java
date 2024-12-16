package com.capstone3.GroupAppoint.comment.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long commentId;
    private String profile;
    private String content;
    private LocalDateTime updateTime;
}
