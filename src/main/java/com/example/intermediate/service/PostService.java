package com.example.intermediate.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.intermediate.controller.request.ImgRequestDto;
import com.example.intermediate.controller.response.CommentResponseDto;
import com.example.intermediate.controller.response.PostResponseDto;
import com.example.intermediate.controller.response.ReCommentResponseDto;
import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.CommentRepository;
import com.example.intermediate.repository.PostRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import com.example.intermediate.repository.ReCommentRepository;
import com.example.intermediate.shared.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final ReCommentRepository reCommentRepository;
  private final TokenProvider tokenProvider;
  private final AmazonS3Client amazonS3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  // 게시글 작성
  @Transactional
  public ResponseDto<?> createPost(PostRequestDto requestDto, HttpServletRequest request, MultipartFile multipartFile) throws IOException {
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
    String imgurl = null;

    if (!multipartFile.isEmpty()) {
      String fileName = CommonUtils.buildFileName(multipartFile.getOriginalFilename());
      ObjectMetadata objectMetadata = new ObjectMetadata();
      objectMetadata.setContentType(multipartFile.getContentType());
      amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(), objectMetadata)
              .withCannedAcl(CannedAccessControlList.PublicRead));
      imgurl = amazonS3Client.getUrl(bucketName, fileName).toString();
    }


    Post post = Post.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .member(member)
            .image(imgurl)
            .build();
    postRepository.save(post);
    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .image(post.getImage())
                    .author(post.getMember().getNickname())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );
  }

  // 특정 게시글 조회
  @Transactional(readOnly = true)
  public ResponseDto<?> getPost(Long id) {  // 게시글의 id
    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
    List<ReCommentResponseDto> reCommentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {
      commentResponseDtoList.add(
          CommentResponseDto.builder()
              .id(comment.getId())
              .author(comment.getMember().getNickname())
              .content(comment.getContent())
                  .commentLikesCount(comment.getCommentLikesCount())
              .reCommentResponseDtoList(reCommentRepository.findAllByCommentId(comment.getId()).stream().map(ReCommentResponseDto::new).collect(Collectors.toList()))
              .createdAt(comment.getCreatedAt())
              .modifiedAt(comment.getModifiedAt())
              .build()
      );
    }

    return ResponseDto.success( // 댓글수, 대댓글 수, 게시글의 좋아요 수
        PostResponseDto.builder()
            .id(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .image(post.getImage())
            .commentResponseDtoList(commentResponseDtoList)
            .author(post.getMember().getNickname())
            .postLikesCount(post.getPostLikesCount())  // 좋아요 수
            .commentsCount((long) post.getComments().size())  // 댓글수
            .createdAt(post.getCreatedAt())
            .modifiedAt(post.getModifiedAt())
            .build()
    );
  }

  // 게시글 전체 조회
  @Transactional(readOnly = true)
  public ResponseDto<?> getAllPost() {
    return ResponseDto.success(postRepository.findAllByOrderByModifiedAtDesc().stream().map(PostResponseDto::new).collect(Collectors.toList()));
  }

  // 게시글 업데이트
  @Transactional
  public ResponseDto<PostResponseDto> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
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

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }

    post.update(requestDto);
    return ResponseDto.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .image(post.getImage())
                    .author(post.getMember().getNickname())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    );
  }

  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
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

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
    }

    postRepository.delete(post);
    return ResponseDto.success("delete success");
  }

  @Transactional(readOnly = true)
  public Post isPresentPost(Long id) {
    Optional<Post> optionalPost = postRepository.findById(id);
    return optionalPost.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }
  @Transactional
  public ResponseDto<?>  insertimg(Long id, MultipartFile multipartFile,HttpServletRequest request) throws IOException {
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
    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }
    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
    }
    String fileName = CommonUtils.buildFileName(multipartFile.getOriginalFilename());
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(multipartFile.getContentType());
    amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, multipartFile.getInputStream(), objectMetadata)
            .withCannedAcl(CannedAccessControlList.PublicRead));
    String imgurl = amazonS3Client.getUrl(bucketName, fileName).toString();
    ImgRequestDto imgRequestDto = new ImgRequestDto(imgurl);
    post.update(imgRequestDto);
    return ResponseDto.success(post);
  }
}
