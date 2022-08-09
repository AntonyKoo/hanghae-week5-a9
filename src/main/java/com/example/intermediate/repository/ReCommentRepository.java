package com.example.intermediate.repository;

import com.example.intermediate.controller.response.ReCommentResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.ReComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReCommentRepository extends JpaRepository<ReComment,Long> {
    List<ReComment> findAllByCommentId(Long commentId);
}
