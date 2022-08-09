package com.example.intermediate.controller;


import com.example.intermediate.domain.Comment;
import com.example.intermediate.domain.Post;
import com.example.intermediate.repository.PostRepository;
import com.example.intermediate.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class Scheduler {

    private  final PostRepository postRepository;

    @Scheduled(cron = "0 0/1 * * * *")
    @Transactional
    public void autoDeleteNoUsePost() throws InterruptedException {
        System.out.println("댓글 없는 게시글 삭제 실행");

        List<Post> postList = postRepository.findAll();
        List<Long> deleteList = new ArrayList<>();

        if (postList.size() != 0) {
            for (Post post : postList) {
                // 1초마다 한 게시글 조회
                TimeUnit.SECONDS.sleep(1);
                // i 번째 게시글 꺼냄
                Post p = post;
                // i 번째 게시글을 대상으로 댓글 유무 확인
                List<Comment> commentList = p.getComments();
                if (commentList.size() == 0) {
                    postRepository.delete(p);
                }
                deleteList.add(p.getId());
            }
            Long deletedId = deleteList.get(0);
            System.out.println("게시글"+deletedId+"에 댓글이 존재하지 않아 삭제 처리 되었습니다.");
        }
    }
}
