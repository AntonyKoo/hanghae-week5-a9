package com.example.intermediate.controller;

import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.PostService;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class PostController {

  private final PostService postService;

  // 게시글 작성
  @RequestMapping(value = "/api/auth/post", method = RequestMethod.POST)
  public ResponseDto<?> createPost(@RequestPart(required = false,value = "file") MultipartFile multipartFile,
                                   @RequestPart PostRequestDto postRequestDto,
                                   HttpServletRequest request) throws IOException {
    return postService.createPost(postRequestDto, request,multipartFile);
  }

  // 특정 게시글 조회  ** 댓글 & 대댓글 & count(좋아요) 같이 response
  @RequestMapping(value = "/api/post/{id}", method = RequestMethod.GET)
  public ResponseDto<?> getPost(@PathVariable Long id) {
    return postService.getPost(id);
  }

  // 게시글 전체 조회
  @RequestMapping(value = "/api/post", method = RequestMethod.GET)
  public ResponseDto<?> getAllPosts() {
    return postService.getAllPost();
  }

  // 게시글 수정
  @RequestMapping(value = "/api/auth/post/{id}", method = RequestMethod.PUT)
  public ResponseDto<?> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto,
      HttpServletRequest request) {
    return postService.updatePost(id, postRequestDto, request);
  }

  // 게시글 삭제
  @RequestMapping(value = "/api/auth/post/{id}", method = RequestMethod.DELETE)
  public ResponseDto<?> deletePost(@PathVariable Long id,
      HttpServletRequest request) {
    return postService.deletePost(id, request);
  }

  // 게시글 내 파일 수정
  @RequestMapping(value = "/api/post/{id}", method = RequestMethod.PUT)
  public ResponseDto<?> insertimg(@PathVariable Long id,@RequestPart(value = "file") MultipartFile multipartFile, HttpServletRequest request) throws IOException {
    return postService.insertimg(id,multipartFile,request);
  }

}
