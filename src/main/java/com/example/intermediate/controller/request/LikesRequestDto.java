package com.example.intermediate.controller.request;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LikesRequestDto {
    private Long postId;
    private Long commentId;
}
