package com.example.intermediate.repository;

import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Likes;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {
    // 게시글에 달린 좋아요
    Optional<Likes> findByPostAndMember(Post post, Member member);
    List<Likes> findByPostId(Long postId);
    void deleteByPostAndMember(Post post, Member member);

    // 댓글에 달린 좋아요
    Optional<Likes> findByCommentAndMember(Comment comment, Member member);
    List<Likes> findByCommentId(Long commentId);
    void deleteByCommentAndMember(Comment comment, Member member);
}
