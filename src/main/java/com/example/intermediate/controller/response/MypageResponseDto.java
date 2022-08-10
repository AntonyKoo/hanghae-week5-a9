package com.example.intermediate.controller.response;

import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Post;
import com.example.intermediate.domain.ReComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MypageResponseDto {
    List<PostDetailResponseDto> postList;
    List<CommentResponseDto> commentList;
    List<ReCommentResponseDto> reCommentList;
    List<PostDetailResponseDto> LikeList;
    List<CommentResponseDto> LikeComment;
    List<ReCommentResponseDto> LikeRecomment;
}
