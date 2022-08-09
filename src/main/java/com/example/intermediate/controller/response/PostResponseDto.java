package com.example.intermediate.controller.response;

import com.example.intermediate.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
  private Long id;
  private String title;
  private String content;
  private String author;
  private List<CommentResponseDto> commentResponseDtoList;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;

  public PostResponseDto(Post entity) {
    this.id = entity.getId();
    this.title = entity.getTitle();
    this.author = entity.getMember().getNickname();
    this.createdAt =entity.getCreatedAt();
    this.modifiedAt = entity.getModifiedAt();
  }
}
