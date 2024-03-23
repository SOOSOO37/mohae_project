package com.seb006.server.recruitpost.mapper;

import com.seb006.server.member.entity.Member;
import com.seb006.server.prfpost.entity.PrfPost;
import com.seb006.server.recruitpost.dto.RecruitPostDetailResponseDto;
import com.seb006.server.recruitpost.dto.RecruitCreateDto;
import com.seb006.server.recruitpost.dto.RecruitPostUpdateDto;
import com.seb006.server.recruitpost.dto.RecruitPostResponseDto;
import com.seb006.server.recruitpost.entity.RecruitPost;
import com.seb006.server.recruitpostcomment.dto.RecruitPostCommentResponseDto;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RecruitPostMapper {

    default RecruitPost recruitPostDtoToRecruitPost (RecruitCreateDto recruitCreateDto){
        RecruitPost recruitPost = new RecruitPost();

        PrfPost prfPost = new PrfPost();
        prfPost.setId(recruitCreateDto.getPrfPostId());

        Member member = new Member();

        recruitPost.setPrfPost(prfPost);
        recruitPost.setMember(member);
        recruitPost.setTitle(recruitCreateDto.getTitle());
        recruitPost.setCategory(recruitCreateDto.getCategory());
        recruitPost.setContent(recruitCreateDto.getContent());
        recruitPost.setRecruitNumber(recruitCreateDto.getRecruitNumber());
        recruitPost.setDueDate(recruitCreateDto.getDueDate());
        recruitPost.setAge(recruitCreateDto.getAge());
        recruitPost.setTags(recruitCreateDto.getTags());

        return recruitPost;
    }
    RecruitPost recruitPostPatchDtoToRecruitPost (RecruitPostUpdateDto recruitPostUpdateDto);


    default RecruitPostResponseDto recruitPostToRecruitPostResponseDto (RecruitPost recruitPost){
        RecruitPostResponseDto recruitPostResponseDto = new RecruitPostResponseDto();

        recruitPostResponseDto.setId(recruitPost.getId());
        recruitPostResponseDto.setTitle(recruitPost.getTitle());
        recruitPostResponseDto.setCategory(recruitPost.getCategory());
        recruitPostResponseDto.setContent(recruitPost.getContent());
        recruitPostResponseDto.setRecruitNumber(recruitPost.getRecruitNumber());
        recruitPostResponseDto.setCurrentNumber(recruitPost.getCurrentNumber());
        recruitPostResponseDto.setRecruitStatus(recruitPost.getRecruitStatus());
        recruitPostResponseDto.setDueDate(recruitPost.getDueDate());
        recruitPostResponseDto.setCreatedAt(recruitPost.getCreatedAt());
        recruitPostResponseDto.setModifiedAt(recruitPost.getModifiedAt());
        recruitPostResponseDto.setMemberId(recruitPost.getMember().getId());
        recruitPostResponseDto.setNickName(recruitPost.getMember().getNickName());
        recruitPostResponseDto.setAge(recruitPost.getAge());
        recruitPostResponseDto.setTags(recruitPost.getTags());
        recruitPostResponseDto.setLikeCount(recruitPost.getLikeCount());

        return recruitPostResponseDto;
    }

    default RecruitPostResponseDto recruitPostToRecruitPostResponseDto (RecruitPost recruitPost, List<Long> likedRecruitIds){
        RecruitPostResponseDto recruitPostResponseDto = new RecruitPostResponseDto();

        recruitPostResponseDto.setId(recruitPost.getId());
        recruitPostResponseDto.setTitle(recruitPost.getTitle());
        recruitPostResponseDto.setCategory(recruitPost.getCategory());
        recruitPostResponseDto.setContent(recruitPost.getContent());
        recruitPostResponseDto.setRecruitNumber(recruitPost.getRecruitNumber());
        recruitPostResponseDto.setCurrentNumber(recruitPost.getCurrentNumber());
        recruitPostResponseDto.setRecruitStatus(recruitPost.getRecruitStatus());
        recruitPostResponseDto.setDueDate(recruitPost.getDueDate());
        recruitPostResponseDto.setCreatedAt(recruitPost.getCreatedAt());
        recruitPostResponseDto.setModifiedAt(recruitPost.getModifiedAt());
        recruitPostResponseDto.setMemberId(recruitPost.getMember().getId());
        recruitPostResponseDto.setNickName(recruitPost.getMember().getNickName());
        recruitPostResponseDto.setAge(recruitPost.getAge());
        recruitPostResponseDto.setTags(recruitPost.getTags());
        recruitPostResponseDto.setLikeCount(recruitPost.getLikeCount());

        if(likedRecruitIds.contains(recruitPost.getId())){
            recruitPostResponseDto.setLiked(true);
        }

        return recruitPostResponseDto;
    }

    default RecruitPostDetailResponseDto recruitPostToRecruitDetailResponseDto(RecruitPost recruitPost){

        RecruitPostDetailResponseDto recruitPostDetailResponseDto = new RecruitPostDetailResponseDto();
        recruitPostDetailResponseDto.setId(recruitPost.getId());
        recruitPostDetailResponseDto.setMemberId(recruitPost.getMember().getId());
        recruitPostDetailResponseDto.setNickName(recruitPost.getMember().getNickName());
        recruitPostDetailResponseDto.setTitle(recruitPost.getTitle());
        recruitPostDetailResponseDto.setCategory(recruitPost.getCategory());
        recruitPostDetailResponseDto.setContent(recruitPost.getContent());
        recruitPostDetailResponseDto.setRecruitNumber(recruitPost.getRecruitNumber());
        recruitPostDetailResponseDto.setCurrentNumber(recruitPost.getCurrentNumber());
        recruitPostDetailResponseDto.setRecruitStatus(recruitPost.getRecruitStatus());
        recruitPostDetailResponseDto.setDueDate(recruitPost.getDueDate());
        recruitPostDetailResponseDto.setAge(recruitPost.getAge());
        recruitPostDetailResponseDto.setTags(recruitPost.getTags());
        recruitPostDetailResponseDto.setLikeCount(recruitPost.getLikeCount());
        recruitPostDetailResponseDto.setPrfPostId(recruitPost.getPrfPost().getId());

        recruitPostDetailResponseDto.setComments(recruitPost.getComments().stream()
                .map(comment-> {
                    RecruitPostCommentResponseDto.RecruitPostCommentResponseDtoBuilder recruitPostCommentResponseDto = RecruitPostCommentResponseDto.builder();
                    recruitPostCommentResponseDto.id(comment.getId());
                    recruitPostCommentResponseDto.content(comment.getContent());
                    recruitPostCommentResponseDto.createdAt(comment.getCreatedAt());
                    recruitPostCommentResponseDto.modifiedAt(comment.getModifiedAt());
                    recruitPostCommentResponseDto.memberId(comment.getMember().getId());
                    recruitPostCommentResponseDto.nickname(comment.getMember().getNickName());

                    return recruitPostCommentResponseDto.build();
                })
                .collect(Collectors.toList()));
        recruitPostDetailResponseDto.setCreatedAt(recruitPost.getCreatedAt());
        recruitPostDetailResponseDto.setModifiedAt(recruitPost.getModifiedAt());

        return recruitPostDetailResponseDto;
    }

    default List<RecruitPostResponseDto> recruitPostsToRecruitPostResponseDtos(List<RecruitPost> recruitPosts, List<Long> likedRecruitIds){
        List<RecruitPostResponseDto> responses = recruitPosts.stream()
                .map(e -> recruitPostToRecruitPostResponseDto(e, likedRecruitIds))
                .collect(Collectors.toList());
        return responses;
    }

    default List<RecruitPostResponseDto> recruitPostsToRecruitPostResponseDtos(List<RecruitPost> recruitPosts){
        List<RecruitPostResponseDto> responses = recruitPosts.stream()
                .map(this::recruitPostToRecruitPostResponseDto)
                .collect(Collectors.toList());
        return responses;
    }


}
