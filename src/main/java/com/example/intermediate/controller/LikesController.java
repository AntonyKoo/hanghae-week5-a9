package com.example.intermediate.controller;

import com.example.intermediate.controller.request.LikesRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    // 좋아요 저장
    @PostMapping("/api/auth/likes")
    public ResponseDto<?> saveLikes(@RequestBody LikesRequestDto requestDto,// includes postId & commentId
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    HttpServletRequest request){

        return ResponseDto.success(likesService.getPostLikes(requestDto, userDetails,request));
    }

    //좋아요 삭제
    @DeleteMapping("/api/auth/likes")
    public ResponseDto<?> deleteLikes(@RequestBody LikesRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                      HttpServletRequest request){

        return ResponseDto.success(likesService.deletePostLikes(requestDto, userDetails, request));
    }
}