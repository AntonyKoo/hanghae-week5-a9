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
  private String title;  // 글 제목
  private String author;  // 작성자

  private String content;  // 게시글 내용
  private String image;  // 게시글에 등록된 이미지 url
  private List<CommentResponseDto> commentResponseDtoList;  // 게시글에 등록된 댓글 리스트
  private Long commentsCount;  // 댓글 수
  private Long postLikesCount;  // 좋아요 수
  private LocalDateTime createdAt;  // 생성일
  private LocalDateTime modifiedAt;  // 수정일

  public PostResponseDto(Post entity) {
    this.id = entity.getId();
    this.title = entity.getTitle();
    this.author = entity.getMember().getNickname();
    this.postLikesCount = entity.getPostLikesCount();
    this.commentsCount = (long) (entity.getComments().size());
    this.createdAt =entity.getCreatedAt();
    this.modifiedAt = entity.getModifiedAt();
  }
}
