package com.example.intermediate.controller.response;

import com.example.intermediate.domain.ReComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReCommentResponseDto {
        private Long id;
        private String author;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        public ReCommentResponseDto(ReComment entity) {
                this.id = entity.getId();
                this.author = entity.getMember().getNickname();
                this.content = entity.getContent();
                this.createdAt = entity.getCreatedAt();
                this.modifiedAt = entity.getModifiedAt();
        }

}
