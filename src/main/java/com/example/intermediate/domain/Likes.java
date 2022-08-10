package com.example.intermediate.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name="member_id")
    @ManyToOne
    private Member member;

    @JoinColumn(name="post_id")
    @ManyToOne
    private Post post;

    @JoinColumn(name="comment_id")
    @ManyToOne
    private Comment comment;

}

