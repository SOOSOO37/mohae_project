package com.seb006.server.recruitpost.service;

import com.seb006.server.global.Sorting;
import com.seb006.server.global.exception.BusinessLogicException;
import com.seb006.server.global.exception.ExceptionCode;
import com.seb006.server.member.entity.Member;
import com.seb006.server.participation.entity.Participation;
import com.seb006.server.participation.repository.ParticipationRepository;
import com.seb006.server.participation.service.ParticipationService;
import com.seb006.server.recruitpost.entity.RecruitPost;
import com.seb006.server.recruitpost.repository.RecruitPostRepository;
import com.seb006.server.recruitpostcomment.entity.RecruitPostComment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class RecruitPostService {

    private final RecruitPostRepository recruitPostRepository;
    private final Sorting sort;

    private final ParticipationRepository participationRepository;


    public RecruitPost createRecruitPost(Member member, RecruitPost recruitPost){

        recruitPost.setMember(member);

        return recruitPostRepository.save(recruitPost);
    }

    public RecruitPost updateRecruitPost(RecruitPost recruitPost){
        RecruitPost findRecruitPost = findVerifiedRecruitPost(recruitPost.getId());
        BeanUtils.copyProperties(recruitPost,findRecruitPost, "id","member","prfPost","createdAt");

        return recruitPostRepository.save(findRecruitPost);

    }

    public RecruitPost findRecruitPost(long id){
        RecruitPost recruitPost = recruitPostRepository.findById(id)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.RECRUITPOST_NOT_FOUND));

        checkRecruitPostStatus(recruitPost);

        return recruitPost;
    }
    // 모집글 리스트 보기 최신순
    public Page<RecruitPost> findRecruitPosts(int page, int size, int sorting) {
        List<Sort.Order> orders = sort.getOrders(sorting);

        return recruitPostRepository.findAll(PageRequest.of(page,size, Sort.by(orders)));
    }

    //태그,카테고리 검색
    public Page<RecruitPost> searchRecruitPosts(int page, int size, int sorting, String category, String keyword){
        List<Sort.Order> orders = sort.getOrders(sorting);
        Pageable pageable = PageRequest.of(page,size,Sort.by(orders));

        return getSearchResult(pageable,category,keyword);
    }

    private Page<RecruitPost> getSearchResult(Pageable pageable, String category, String keyword){
        if(category.isBlank() && keyword.isBlank()){
            return recruitPostRepository.findAll(pageable);
        } else if (category.isBlank()) {
            return recruitPostRepository.findByTagsContainingOrTitleContaining(pageable,keyword,keyword);
        } else {
            return recruitPostRepository.findByCategoryAndKeyword(pageable, category, keyword);
        }
    }


    public void deleteRecruitPost(long id){
        RecruitPost findRecruitPost = findVerifiedRecruitPost(id);

        recruitPostRepository.deleteById(findRecruitPost.getId());
    }
    public RecruitPost findVerifiedRecruitPost(long id){
        Optional<RecruitPost> optionalRecruitPost =
                recruitPostRepository.findById(id);
        RecruitPost findRecruitPost =
                optionalRecruitPost.orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.RECRUITPOST_NOT_FOUND));
        return findRecruitPost;
    }

    //모집글 닫기
    public void closeRecruitPost (long id) {
        RecruitPost findRecruitPost = findVerifiedRecruitPost(id);

        findRecruitPost.setRecruitStatus(RecruitPost.RecruitStatus.CLOSE);

        recruitPostRepository.save(findRecruitPost);

    }

    public void expiredRecruitPost(long id) {
        RecruitPost findRecruitPost = findVerifiedRecruitPost(id);
        findRecruitPost.getDueDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate dateTime = LocalDate.parse(findRecruitPost.getDueDate(),formatter);

        if (isRecruitExpired(findRecruitPost,dateTime)) {
            findRecruitPost.setRecruitStatus(RecruitPost.RecruitStatus.EXPIRED);

        }
    }

    private boolean isRecruitExpired(RecruitPost recruitPost, LocalDate dueDate) {
        return recruitPost.getCurrentNumber() < recruitPost.getRecruitNumber() && LocalDate.now().isAfter(dueDate);
    }


    public void  dbExpiredRecruitPost(long id) {
        RecruitPost findRecruitPost = findVerifiedRecruitPost(id);

        int status = findRecruitPost.getRecruitStatus().getStatusNumber();
        if(status >= 3){
            recruitPostRepository.deleteById(id);
        }
    }
    private void checkRecruitPostStatus(RecruitPost recruitPost) {
        if (recruitPost.getRecruitStatus() == RecruitPost.RecruitStatus.CLOSE) {
            throw new BusinessLogicException(ExceptionCode.RECRUITPOST_CLOSED);
        }if(recruitPost.getRecruitStatus() == RecruitPost.RecruitStatus.EXPIRED){
            throw new BusinessLogicException(ExceptionCode.RECRUITPOST_EXPIRED);
        }
    }
}
