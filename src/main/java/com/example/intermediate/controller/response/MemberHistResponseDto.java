package com.example.intermediate.controller.response;

import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.domain.ReComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberHistResponseDto {
    // 사용자 작성한 list = post, comment, reComment
    // 사용자가 좋아요 누른 list = post, comment
    private Long id; // 사용자 키값
    private String author; // 사용자 이름

    // ----- 아래는 사용자가 작성한 리스트 -----
    private List<PostResponseDto> postList;  // 작성한 게시글 리스트
    private List<CommentResponseDto> commentList;  // 작성한 댓글 리스트
    private List<ReCommentResponseDto> reCommentList;  // 작성한 대댓글 리스트
    // ----- 아래는 좋아요 누른 리스트 -----
    private List<Post> likedPostList;  // 좋아요 누른 게시글 리스트
    private List<Comment> likedCommentList;  // 좋아요 누른 댓글 리스트

//    public MemberHistResponseDto(Member entity) {
//        this.id = entity.getId();
//        this.author = entity.getNickname();
//        this.postList = entity.getPostList().
//        this.commentList = entity.getCommentList();
//        this.reCommentList = entity.getReCommentList();
//        this.likedPostList = getLikedPostList();
//        this.likedCommentList = getLikedCommentList();
//    }
}
