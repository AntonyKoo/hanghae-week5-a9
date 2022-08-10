package com.example.intermediate.service;

import com.example.intermediate.controller.request.LikesRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.*;
import com.example.intermediate.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LikesService {
    private final LikesRepository likesRepository;
    private final ReCommentService reCommentService;
    private final PostService postService;  //
    private final CommentService commentService;  //


    // 좋아요 등록
    @Transactional
    public ResponseDto<?> getPostLikes(LikesRequestDto requestDto, UserDetailsImpl userDetails, HttpServletRequest request) {
        // 로그인 체크
        if (null == request.getHeader("Refresh-Token") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        //게시글 좋아요  // ** 아래 조건이 의미하는 것은? => "좋아요 눌렀나 확인"
        if( requestDto.getPostId() != null && requestDto.getCommentId() == null && requestDto.getRecommendId()==null ) {
            Post post = postService.isPresentPost(requestDto.getPostId());
            Optional<Likes> postExists = likesRepository.findByPostAndMember(post, userDetails.getMember());
            if (postExists.isPresent()) {
                return ResponseDto.fail("NOT_FOUND", "이미 좋아요를 누른 게시글입니다.");
            }
            //게시글 존재 확인
            if (null == post) {
                return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
            }
            //게시글 좋아요 저장
            Likes likes = Likes.builder()
                    .post(post)
                    .member(userDetails.getMember())
                    .build();
            likesRepository.save(likes);
            // 게시글에 좋아요 수 보내기
            List<Likes> likestList = likesRepository.findByPostId(requestDto.getPostId());
            post.likesUpdate((long) likestList.size());
            return ResponseDto.success("게시글에 좋아요를 눌렀습니다.");
        }
        //댓글 좋아요
        if(requestDto.getPostId() == null && requestDto.getCommentId() != null && requestDto.getRecommendId()==null){
            Comment comment = commentService.isPresentComment(requestDto.getCommentId());
            Optional<Likes> commentExists = likesRepository.findByCommentAndMember(comment, userDetails.getMember());
            if (commentExists.isPresent()) {
                return ResponseDto.fail("NOT_FOUND", "이미 좋아요를 누른 댓글입니다.");
            }
            //댓글 존재 확인
            if (null == comment) {
                return ResponseDto.fail("NOT_FOUND", "존재하지 않는 댓글 id 입니다.");
            }
            //댓글 좋아요 저장
            Likes likes = Likes.builder()
                    .comment(comment)
                    .member(userDetails.getMember())
                    .build();

            likesRepository.save(likes);
            // 댓글에 좋아요 수 보내기
            List<Likes> commentLikesList = likesRepository.findByCommentId(requestDto.getCommentId());
            System.out.println("comment heart : " + commentLikesList.size());
            comment.commentLikesUpdate((long) commentLikesList.size());
            return ResponseDto.success("댓글에 좋아요를 눌렀습니다.");
        }
        if(requestDto.getPostId() == null && requestDto.getCommentId() == null && requestDto.getRecommendId() != null){
            ReComment recomment = reCommentService.isPresentReComment(requestDto.getRecommendId());
            Optional<Likes> RecommentExists = likesRepository.findByRecommentAndMember(recomment, userDetails.getMember());
            if (RecommentExists.isPresent()) {
                return ResponseDto.fail("NOT_FOUND", "이미 좋아요를 누른 대댓글입니다.");
            }
            //댓글 존재 확인
            if (null == recomment) {
                return ResponseDto.fail("NOT_FOUND", "존재하지 않는 대댓글 id 입니다.");
            }
            //댓글 좋아요 저장
            Likes likes = Likes.builder()
                    .recomment(recomment)
                    .member(userDetails.getMember())
                    .build();

            likesRepository.save(likes);
            // 댓글에 좋아요 수 보내기
            List<Likes> recommentLikesList = likesRepository.findByRecommentId(requestDto.getRecommendId());
            System.out.println("comment heart : " + recommentLikesList.size());
            recomment.likesUpdate((long) recommentLikesList.size());
            return ResponseDto.success("대댓글에 좋아요를 눌렀습니다.");
        }


        return ResponseDto.success("글을 선택해주세요");
    }


    // 좋아요 취소
    @Transactional
    public ResponseDto<?> deletePostLikes(LikesRequestDto requestDto, UserDetailsImpl userDetails, HttpServletRequest request) {
        // 로그인 체크
        if (null == request.getHeader("Refresh-Token") || null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        // 게시글 좋아요 취소
        if(requestDto.getPostId() != null && requestDto.getCommentId() == null && requestDto.getRecommendId() == null) {

            // 좋아요를 눌렀는지 확인
            Post post = postService.isPresentPost(requestDto.getPostId());
            Optional<Likes> postExists = likesRepository.findByPostAndMember(post, userDetails.getMember());
            if (postExists.isEmpty()) {
                return ResponseDto.fail("NOT_FOUND", "좋아요를 하지 않은 게시글입니다.");
            }

            //좋아요 삭제
            likesRepository.deleteByPostAndMember(post, userDetails.getMember());

            // 게시글에 좋아요 수 보내기
            List<Likes> likesList = likesRepository.findByPostId(requestDto.getPostId());
            post.likesUpdate((long) likesList.size());
        }

        // 댓글 좋아요 취소
        if(requestDto.getPostId() == null && requestDto.getCommentId() != null && requestDto.getRecommendId() == null) {
            // 좋아요를 눌렀는지 확인
            Comment comment = commentService.isPresentComment(requestDto.getCommentId());
            Optional<Likes> commentExists = likesRepository.findByCommentAndMember(comment, userDetails.getMember());
            if (commentExists.isEmpty()) {
                return ResponseDto.fail("NOT_FOUND", "좋아요를 하지 않은 게시글입니다.");
            }

            //좋아요 삭제
            likesRepository.deleteByCommentAndMember(comment, userDetails.getMember());

            // 게시글에 좋아요 수 보내기
            List<Likes> likesList = likesRepository.findByCommentId(requestDto.getCommentId());
            comment.commentLikesUpdate((long) likesList.size());
        }
        if(requestDto.getPostId() == null && requestDto.getCommentId() == null && requestDto.getRecommendId() != null ) {
            // 좋아요를 눌렀는지 확인
            ReComment reComment = reCommentService.isPresentReComment(requestDto.getRecommendId());
            Optional<Likes> recommentExists = likesRepository.findByRecommentAndMember(reComment, userDetails.getMember());
            if (recommentExists.isEmpty()) {
                return ResponseDto.fail("NOT_FOUND", "좋아요를 하지 않은 대댓글입니다.");
            }

            //좋아요 삭제
            likesRepository.deleteByRecommentAndMember(reComment, userDetails.getMember());

            // 게시글에 좋아요 수 보내기
            List<Likes> likesList = likesRepository.findByRecommentId(requestDto.getRecommendId());
            reComment.likesUpdate((long) likesList.size());
        }

        return ResponseDto.success("글을 선택해주세요.");
    }
}
