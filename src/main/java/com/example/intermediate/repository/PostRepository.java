package com.example.intermediate.repository;

import com.example.intermediate.domain.Post;
import com.example.intermediate.domain.Member;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByModifiedAtDesc();
  List<Post> findAllByMemberId(Long id);
}
