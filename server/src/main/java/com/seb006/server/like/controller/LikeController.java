package com.seb006.server.like.controller;

import com.seb006.server.like.dto.PrfPostLikeDto;
import com.seb006.server.like.entity.PrfPostLike;
import com.seb006.server.like.entity.RecruitPostLike;
import com.seb006.server.like.service.LikeService;
import com.seb006.server.member.entity.Member;
import com.seb006.server.member.service.MemberService;
import com.seb006.server.prfpost.entity.PrfPost;
import com.seb006.server.prfpost.service.PrfPostService;
import com.seb006.server.recruitpost.entity.RecruitPost;
import com.seb006.server.recruitpost.service.RecruitPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/like")
public class LikeController {
    private final MemberService memberService;
    private final LikeService likeService;
    private final PrfPostService prfPostService;
    private final RecruitPostService recruitPostService;

    public LikeController(MemberService memberService, LikeService likeService, PrfPostService prfPostService, RecruitPostService recruitPostService) {
        this.memberService = memberService;
        this.likeService = likeService;
        this.prfPostService = prfPostService;
        this.recruitPostService = recruitPostService;
    }

    @PostMapping("/prf-posts/{prf-post-id}")
    public ResponseEntity postPrfPostLike(Principal principal, @PathVariable("prf-post-id") long prfPostId){
        Member member = memberService.findVerifiedMember(principal.getName());
        PrfPost prfPost = prfPostService.findverifiedPrfPost(prfPostId);

        PrfPostLike prfPostLike = likeService.addPrfPostLike(member, prfPost);

        return new ResponseEntity(new PrfPostLikeDto.Response(prfPost.getLikeCount()), HttpStatus.CREATED);
    }

    @PostMapping("/recruit-posts/{recruit-post-id}")
    public ResponseEntity postRecruitPostLike(Principal principal, @PathVariable("recruit-post-id") long recruitPostId){
        Member member = memberService.findVerifiedMember(principal.getName());
        RecruitPost recruitPost = recruitPostService.findVerifiedRecruitPost(recruitPostId);

        RecruitPostLike recruitPostLike = likeService.addRecruitPostLike(member, recruitPost);

        return new ResponseEntity(HttpStatus.CREATED);
    }


    @DeleteMapping("/prf-posts/{prf-post-id}")
    public ResponseEntity deletePrfPostLike(Principal principal, @PathVariable("prf-post-id") long prfPostId){
        Member member = memberService.findVerifiedMember(principal.getName());
        PrfPost prfPost = prfPostService.findverifiedPrfPost(prfPostId);

        likeService.cancelPrfPostLike(member, prfPost);
        return new ResponseEntity(new PrfPostLikeDto.Response(prfPost.getLikeCount()), HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/recruit-posts/{recruit-post-id}")
    public ResponseEntity deleteRecruitPostLike(Principal principal, @PathVariable("recruit-post-id") long recruitPostId){
        Member member = memberService.findVerifiedMember(principal.getName());
        RecruitPost recruitPost = recruitPostService.findVerifiedRecruitPost(recruitPostId);

        likeService.cancelRecruitPostLike(member, recruitPost);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
