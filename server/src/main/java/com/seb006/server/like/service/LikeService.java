package com.seb006.server.like.service;

import com.seb006.server.global.exception.BusinessLogicException;
import com.seb006.server.global.exception.ExceptionCode;
import com.seb006.server.like.entity.PrfPostLike;
import com.seb006.server.like.entity.RecruitPostLike;
import com.seb006.server.like.repository.PrfPostLikeRepository;
import com.seb006.server.like.repository.RecruitPostLikeRepository;
import com.seb006.server.member.entity.Member;
import com.seb006.server.prfpost.entity.PrfPost;
import com.seb006.server.recruitpost.entity.RecruitPost;
import org.springframework.stereotype.Service;



@Service
public class LikeService {
    private final PrfPostLikeRepository prfPostLikeRepository;
    private final RecruitPostLikeRepository recruitPostLikeRepository;

    public LikeService(PrfPostLikeRepository prfPostLikeRepository, RecruitPostLikeRepository recruitPostLikeRepository) {
        this.prfPostLikeRepository = prfPostLikeRepository;
        this.recruitPostLikeRepository = recruitPostLikeRepository;
    }

    // 게시글 좋아요
    public PrfPostLike addPrfPostLike(Member member, PrfPost prfPost){
        checkExistPrfPostLike(member, prfPost); // 이미 좋아요한 경우

        PrfPostLike prfPostLike = new PrfPostLike(member, prfPost);
        prfPost.likeCountUp();

        return prfPostLikeRepository.save(prfPostLike);
    }

    // 게시글 좋아요 취소
    public void cancelPrfPostLike(Member member, PrfPost prfPost){
        prfPost.likeCountDown();
        PrfPostLike prfPostLike = prfPostLikeRepository.findByMemberAndPrfPost(member, prfPost)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PRFPOSTLIKE_NOT_FOUND));

        prfPostLikeRepository.delete(prfPostLike);
    }

    // 모집글 좋아요
    public RecruitPostLike addRecruitPostLike(Member member, RecruitPost recruitPost){
        checkExistRecruitPostLike(member, recruitPost); // 이미 좋아요한 경우

        recruitPost.likeCountUp();
        RecruitPostLike recruitPostLike = new RecruitPostLike(member, recruitPost);

        return recruitPostLikeRepository.save(recruitPostLike);
    }

    // 모집글 좋아요 취소
    public void cancelRecruitPostLike(Member member, RecruitPost recruitPost){
        recruitPost.likeCountDown();

        RecruitPostLike recruitPostLike = recruitPostLikeRepository.findByMemberAndRecruitPost(member, recruitPost)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.RECRUITPOST_NOT_FOUND));
        recruitPostLikeRepository.delete(recruitPostLike);
    }

    // 이미 존재하는 게시글인지 확인
    public void checkExistPrfPostLike(Member member, PrfPost prfPost){
        if (prfPostLikeRepository.findByMemberAndPrfPost(member, prfPost).isPresent()){
            throw new BusinessLogicException(ExceptionCode.PRFPOSTLIKE_EXISTS);
        }
    }

    // 이미 존재하는 모잡글인지 확인
    public void checkExistRecruitPostLike(Member member, RecruitPost recruitPost){
        if (recruitPostLikeRepository.findByMemberAndRecruitPost(member, recruitPost).isPresent()){
            throw new BusinessLogicException(ExceptionCode.RECRUITPOSTLIKE_EXISTS);
        }
    }
}
