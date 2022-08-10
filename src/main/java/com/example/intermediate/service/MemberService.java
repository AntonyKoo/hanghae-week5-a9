package com.example.intermediate.service;

import com.example.intermediate.controller.response.*;
import com.example.intermediate.domain.*;
import com.example.intermediate.controller.request.LoginRequestDto;
import com.example.intermediate.controller.request.MemberRequestDto;
import com.example.intermediate.controller.request.TokenDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final ReCommentRepository reCommentRepository;
  private final LikesRepository likesRepository;

  private final MemberRepository memberRepository;

  private final PasswordEncoder passwordEncoder;
//  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final TokenProvider tokenProvider;

  @Transactional
  public ResponseDto<?> createMember(MemberRequestDto requestDto) {
    if (null != isPresentMember(requestDto.getNickname())) {
      return ResponseDto.fail("DUPLICATED_NICKNAME",
          "중복된 닉네임 입니다.");
    }

    if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
      return ResponseDto.fail("PASSWORDS_NOT_MATCHED",
          "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
    }

    Member member = Member.builder()
            .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                    .build();
    memberRepository.save(member);
    return ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()
    );
  }

  @Transactional
  public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
    Member member = isPresentMember(requestDto.getNickname());
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "사용자를 찾을 수 없습니다.");
    }

    if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
      return ResponseDto.fail("INVALID_MEMBER", "사용자를 찾을 수 없습니다.");
    }

//    UsernamePasswordAuthenticationToken authenticationToken =
//        new UsernamePasswordAuthenticationToken(requestDto.getNickname(), requestDto.getPassword());
//    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
    tokenToHeaders(tokenDto, response);

    return ResponseDto.success(
        MemberResponseDto.builder()
            .id(member.getId())
            .nickname(member.getNickname())
            .createdAt(member.getCreatedAt())
            .modifiedAt(member.getModifiedAt())
            .build()
    );
  }

//  @Transactional
//  public ResponseDto<?> reissue(HttpServletRequest request, HttpServletResponse response) {
//    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
//      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
//    }
//    Member member = tokenProvider.getMemberFromAuthentication();
//    if (null == member) {
//      return ResponseDto.fail("MEMBER_NOT_FOUND",
//          "사용자를 찾을 수 없습니다.");
//    }
//
//    Authentication authentication = tokenProvider.getAuthentication(request.getHeader("Access-Token"));
//    RefreshToken refreshToken = tokenProvider.isPresentRefreshToken(member);
//
//    if (!refreshToken.getValue().equals(request.getHeader("Refresh-Token"))) {
//      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
//    }
//
//    TokenDto tokenDto = tokenProvider.generateTokenDto(member);
//    refreshToken.updateValue(tokenDto.getRefreshToken());
//    tokenToHeaders(tokenDto, response);
//    return ResponseDto.success("success");
//  }

  public ResponseDto<?> logout(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }
    Member member = tokenProvider.getMemberFromAuthentication();
    if (null == member) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
          "사용자를 찾을 수 없습니다.");
    }

    return tokenProvider.deleteRefreshToken(member);
  }

  @Transactional(readOnly = true)
  public Member isPresentMember(String nickname) {
    Optional<Member> optionalMember = memberRepository.findByNickname(nickname);
    return optionalMember.orElse(null);
  }

  public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
    response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
    response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
    response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
  }

  @Transactional(readOnly = true)
  public ResponseDto<?> mypage(HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    // 헤더에 Auth 토큰 있늬?
    // Refresh와 Authorization 한 번에 할 수 없나?
    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    // 위에서 토큰 확인 후, 이용자 검증 로직
    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    List<PostDetailResponseDto> formypostlist = new ArrayList<>();
    List<Post> postlist = postRepository.findAllByMemberId(member.getId());
    for (Post post :postlist){
      formypostlist.add(new PostDetailResponseDto(post));
    }

    List<CommentResponseDto> formycommentlist = new ArrayList<>();
    List<Comment> commentList = commentRepository.findAllByMemberId(member.getId());
    for (Comment comment :commentList){
      formycommentlist.add(new CommentResponseDto(comment));
    }

    List<ReCommentResponseDto> formyRecommentlist = new ArrayList<>();
    List<ReComment> Recommentlist = reCommentRepository.findAllByMemberId(member.getId());
    for (ReComment reComment :Recommentlist){
      formyRecommentlist.add(new ReCommentResponseDto(reComment));
    }

    List<PostDetailResponseDto> forPostlike = new ArrayList<>();
    List<CommentResponseDto> forcommentlike = new ArrayList<>();
    List<Likes> likesList = likesRepository.findAllByMemberId(member.getId());
    for (Likes likess :likesList) {
      if(likess.getComment()!=null){
        Comment init = commentRepository.getReferenceById(likess.getComment().getId());
        forcommentlike.add(new CommentResponseDto(init));
      }
      if(likess.getPost()!=null){
        Post init = postRepository.getReferenceById(likess.getPost().getId());
        forPostlike.add(new PostDetailResponseDto(init));
      }
    }

    return ResponseDto.success(
            MypageResponseDto.builder()
                    .postList(formypostlist)
                    .commentList(formycommentlist)
                    .reCommentList(formyRecommentlist)
                    .LikeList(forPostlike)
                    .LikeComment(forcommentlike)
                    .build()
    );
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }
}
