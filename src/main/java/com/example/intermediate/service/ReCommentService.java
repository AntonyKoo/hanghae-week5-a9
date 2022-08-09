package com.example.intermediate.service;

import com.example.intermediate.controller.request.ReCommentRequestDto;
import com.example.intermediate.controller.response.ReCommentResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.ReComment;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.ReCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ReCommentService {
    private final ReCommentRepository  reCommentRepository;  // Service Layer의 Repo

    private final TokenProvider tokenProvider;

    private final CommentService commentService;  // 대댓글고 상호작용하는 댓글의 Repo

    @Transactional  // 공통으로 들어가는 Response에 대한 Dto
    public ResponseDto<?> createRecomment(ReCommentRequestDto requestDto, HttpServletRequest request) {
        // 요청의 헤더에 토큰 있늬?
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

        // 대댓글이 바라보고 있는 댓글 존재 검증
        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        ReComment reComment = ReComment.builder()
                .member(member)
                .comment(comment)
                .content(requestDto.getContent())
                .build();
        reCommentRepository.save(reComment);
        return ResponseDto.success(
                ReCommentResponseDto.builder()
                        .id(reComment.getId())
                        .author(reComment.getMember().getNickname())
                        .content(reComment.getContent())
                        .createdAt(reComment.getCreatedAt())
                        .modifiedAt(reComment.getModifiedAt())
                        .build()
        );
    }

    // 특정 댓글을 바라보는 모든 대댓글 조회
    @Transactional(readOnly = true)
    public ResponseDto<?> getAllReCommentsByCommentId(Long commentId) {
        Comment comment = commentService.isPresentComment(commentId);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        List<ReComment> reCommentList = reCommentRepository.findAllByCommentId(commentId);
        List<ReCommentResponseDto> reCommentResponseDtoList = new ArrayList<>();

        for (ReComment reComment : reCommentList) {
            reCommentResponseDtoList.add(
                    ReCommentResponseDto.builder()
                            .id(reComment.getId())
                            .author(reComment.getMember().getNickname())
                            .content(reComment.getContent())
                            .createdAt(reComment.getCreatedAt())
                            .modifiedAt(reComment.getModifiedAt())
                            .build()
            );
        }
        return ResponseDto.success(reCommentResponseDtoList);
    }

    @Transactional
    public ResponseDto<?> updateComment(Long id, ReCommentRequestDto requestDto, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Comment comment = commentService.isPresentComment(requestDto.getCommentId());
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        ReComment reComment = isPresentReComment(id);
        if (null == reComment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (reComment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        reComment.update(requestDto);
        return ResponseDto.success(
                ReCommentResponseDto.builder()
                        .id(reComment.getId())
                        .author(reComment.getMember().getNickname())
                        .content(reComment.getContent())
                        .createdAt(reComment.getCreatedAt())
                        .modifiedAt(reComment.getModifiedAt())
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> deleteReComment(Long id, HttpServletRequest request) {
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        ReComment reComment = isPresentReComment(id);
        if (null == reComment) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
        }

        if (reComment.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        reCommentRepository.delete(reComment);
        return ResponseDto.success("success");
    }

    @Transactional(readOnly = true)
    public ReComment isPresentReComment(Long id) {
        Optional<ReComment> optionalReComment = reCommentRepository.findById(id);
        return optionalReComment.orElse(null);
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
