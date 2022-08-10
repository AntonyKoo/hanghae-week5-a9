package com.example.intermediate.controller;


import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MypageController {

    private final MemberService memberService;

    // 마이 페이지 조회
    @RequestMapping(value = "/api/auth/mypage", method = RequestMethod.GET)
    public ResponseDto<?> showMypage(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseDto.success(memberService.getAllMemberHist(userDetails));
    }
    // 사용자 작성한 list = post, comment, reComment
    // 사용자가 좋아요 누른 list = post, comment
}
