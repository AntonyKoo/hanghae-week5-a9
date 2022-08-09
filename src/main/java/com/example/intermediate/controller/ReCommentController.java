package com.example.intermediate.controller;

import com.example.intermediate.controller.request.ReCommentRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.ReCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Validated
@RequiredArgsConstructor
@RestController
public class ReCommentController {
    private final ReCommentService reCommentService;

    //  대댓글 작성 기능
    @RequestMapping(value = "/api/auth/reply", method = RequestMethod.POST)
    public ResponseDto<?> createRecomment(@RequestBody ReCommentRequestDto requestDto,  // dto 안에 commentId, content
                                          HttpServletRequest request) {
        return reCommentService.createRecomment(requestDto, request);
    }

    // 게시글 댓글을 바라보는 **모든 대댓글 조회
    @RequestMapping(value = "/api/reply/{id}", method = RequestMethod.GET)
    public ResponseDto<?> getAllReComments(@PathVariable Long id) {
        return reCommentService.getAllReCommentsByCommentId(id);
    }

    // 대댓글 수정 기능
    @RequestMapping(value = "/api/auth/reply/{id}", method = RequestMethod.PUT)
    public ResponseDto<?> updateReComment(@PathVariable Long id, @RequestBody ReCommentRequestDto requestDto, HttpServletRequest request) {
        return reCommentService.updateComment(id, requestDto, request);
    }

    // 대댓글 삭제 기능
    @RequestMapping(value = "/api/auth/reply/{id}", method = RequestMethod.DELETE)
    public ResponseDto<?> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        return reCommentService.deleteReComment(id, request);
    }
}
