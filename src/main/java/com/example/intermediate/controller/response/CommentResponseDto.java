package com.example.intermediate.controller.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.ReComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponseDto {
  private Long id;
  private String author;
  private String content;
  private Long commentLikesCount;
  private List<ReCommentResponseDto> reCommentResponseDtoList;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;


  public CommentResponseDto(Comment comment) {
    this.id = comment.getId();
    this.author = comment.getMember().getNickname();
    this.content = comment.getContent();
    this.commentLikesCount = comment.getCommentLikesCount();
    this.createdAt = comment.getCreatedAt();
    this.modifiedAt = comment.getModifiedAt();
  }
}
