package com.seb006.server.recruitpost.controller;

import com.seb006.server.global.response.MultiResponseDto;
import com.seb006.server.like.service.LikeService;
import com.seb006.server.member.entity.Member;
import com.seb006.server.member.service.MemberService;
import com.seb006.server.recruitpost.dto.RecruitPostDetailResponseDto;
import com.seb006.server.recruitpost.dto.RecruitCreateDto;
import com.seb006.server.recruitpost.dto.RecruitPostUpdateDto;
import com.seb006.server.recruitpost.dto.RecruitPostResponseDto;
import com.seb006.server.recruitpost.entity.RecruitPost;
import com.seb006.server.recruitpost.mapper.RecruitPostMapper;
import com.seb006.server.recruitpost.service.RecruitPostService;
import com.seb006.server.utils.UriCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.net.URI;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/recruit-posts")
public class RecruitPostController {

    private final static String RECRUITPOST_DEFAULT_URL = "/recruit-posts";

    private final RecruitPostService service;

    private final MemberService memberService;

    private final RecruitPostMapper mapper;
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity postRecruitPost(@AuthenticationPrincipal Member member,
                                          @RequestBody RecruitCreateDto recruitCreateDto){

        log.info("Creating RecruitPost");
        RecruitPost recruitPost = service.createRecruitPost(member,mapper.recruitPostDtoToRecruitPost(recruitCreateDto));

        URI location = UriCreator.createUri(RECRUITPOST_DEFAULT_URL,recruitPost.getId());

        log.info("RecruitPost ID: {}", recruitPost.getId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{recruit-post-id}")
    public ResponseEntity patchRecruitPost(@PathVariable("recruit-post-id")long id,
                                           @RequestBody RecruitPostUpdateDto recruitPostUpdateDto){
        log.info("Updating RecruitPost : {}",id);
        recruitPostUpdateDto.setId(id);
        RecruitPost recruitPost = service.updateRecruitPost(mapper.recruitPostPatchDtoToRecruitPost(recruitPostUpdateDto));
        log.info("Updated RecruitPost : {}",id);

        return new ResponseEntity<>(mapper.recruitPostToRecruitPostResponseDto(recruitPost),HttpStatus.OK);
    }

    @GetMapping("/{recruit-post-id}")
    public ResponseEntity getRecruitPost(@AuthenticationPrincipal Member member,
                                         @PathVariable("recruit-post-id") long id){
        log.info("Inquiring RecruitPost : {}",id);
        RecruitPost recruitPost = service.findRecruitPost(id);

        RecruitPostDetailResponseDto result = mapper.recruitPostToRecruitDetailResponseDto(recruitPost);

        if(likeService.isRecruitPostLiked(member, recruitPost)){
            result.setLiked(true);
        }
        log.info("Found RecruitPost : {}",id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity getRecruitPosts(@Nullable @AuthenticationPrincipal Member member,
                                          @Positive @RequestParam(defaultValue = "1") int page,
                                          @Positive @RequestParam(defaultValue = "10") int size,
                                          @Positive @RequestParam(defaultValue = "1") int sorting){
        log.info("Inquiring All RecruitPost");

        Page<RecruitPost> recruitPostPage = service.findRecruitPosts(page-1,size,sorting);
        List<RecruitPost> recruitPosts = recruitPostPage.getContent();

        if(member == null){ // 비로그인 시에
            List<RecruitPostResponseDto> result = mapper.recruitPostsToRecruitPostResponseDtos(recruitPosts);
            return new ResponseEntity<>(new MultiResponseDto<RecruitPostResponseDto>(result,recruitPostPage), HttpStatus.OK);
        }
        List<Long> likedRecruitIds = likeService.recruitPostLiked(member, recruitPosts);
        List<RecruitPostResponseDto> result = mapper.recruitPostsToRecruitPostResponseDtos(recruitPosts,likedRecruitIds);

        log.info("All RecruitPost : {}",recruitPostPage.getTotalElements());
        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.recruitPostsToRecruitPostResponseDtos(recruitPosts, likedRecruitIds),recruitPostPage),
                HttpStatus.OK);
    }

    //모집글 삭제
    @DeleteMapping("/{recruit-post-id}")
    public ResponseEntity deleteRecruitPost(@PathVariable("recruit-post-id")long id){
        log.info("Deleting RecruitPost",id);
        service.deleteRecruitPost(id);
        log.info("Deleted RecruitPost",id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //모집글 리스트 보기(태그,카테고리)
    @GetMapping
    public ResponseEntity searchRecruitPosts( @Nullable@AuthenticationPrincipal Member member,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              @RequestParam(defaultValue = "1") int sorting,
                                              @RequestParam(required = false, defaultValue = "") String category,
                                              @RequestParam(required = false, defaultValue = "") String keyword){
        log.info("Inquiring Category & Keyword");
        Page<RecruitPost> recruitPostPage = service.searchRecruitPosts(page-1,size,sorting,category,keyword);
        List<RecruitPost> recruitPosts = recruitPostPage.getContent();

        if(member == null){ // 비로그인 시에
            List<RecruitPostResponseDto> result = mapper.recruitPostsToRecruitPostResponseDtos(recruitPosts);
            return new ResponseEntity<>(new MultiResponseDto<RecruitPostResponseDto>(result,recruitPostPage), HttpStatus.OK);
        }
        List<Long> likedRecruitIds = likeService.recruitPostLiked(member, recruitPosts);
        List<RecruitPostResponseDto> result = mapper.recruitPostsToRecruitPostResponseDtos(recruitPosts,likedRecruitIds);
        log.info("Found Category & Keyword");

        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.recruitPostsToRecruitPostResponseDtos(recruitPosts, likedRecruitIds),recruitPostPage),
                HttpStatus.OK);
    }


    //모집글 닫기
    @PatchMapping("/{recruit-post-id}/close")
    public ResponseEntity closeRecruitPost (@PathVariable("recruit-post-id") long id) {
        log.info("Closing RecruitPost :{}",id);
        service.closeRecruitPost(id);
        log.info("Closed RecruitPost :{}",id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //모집 실패
    @PatchMapping("/{recruit-post-id}/expired")
    public ResponseEntity expiredRecruitPost (@PathVariable("recruit-post-id")long id){
        log.info("RecruitPost : {}",id);
        service.expiredRecruitPost(id);
        log.info("Expired RecruitPost :{}",id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
