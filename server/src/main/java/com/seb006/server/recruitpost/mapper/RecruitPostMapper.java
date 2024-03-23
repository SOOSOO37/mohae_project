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
import org.springframework.beans.BeanUtils;

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
        BeanUtils.copyProperties(recruitCreateDto, recruitPost);

        return recruitPost;
    }
    RecruitPost recruitPostPatchDtoToRecruitPost (RecruitPostUpdateDto recruitPostUpdateDto);


    default RecruitPostResponseDto recruitPostToRecruitPostResponseDto (RecruitPost recruitPost){
        RecruitPostResponseDto recruitPostResponseDto = new RecruitPostResponseDto();

        BeanUtils.copyProperties(recruitPost, recruitPostResponseDto);

        recruitPostResponseDto.setMemberId(recruitPost.getMember().getId());
        recruitPostResponseDto.setNickName(recruitPost.getMember().getNickName());

        return recruitPostResponseDto;
    }

    default RecruitPostResponseDto recruitPostToRecruitPostResponseDto (RecruitPost recruitPost, List<Long> likedRecruitIds){
        RecruitPostResponseDto recruitPostResponseDto = new RecruitPostResponseDto();

        BeanUtils.copyProperties(recruitPost, recruitPostResponseDto);

        recruitPostResponseDto.setMemberId(recruitPost.getMember().getId());
        recruitPostResponseDto.setNickName(recruitPost.getMember().getNickName());

        if(likedRecruitIds.contains(recruitPost.getId())){
            recruitPostResponseDto.setLiked(true);
        }

        return recruitPostResponseDto;
    }

    default RecruitPostDetailResponseDto recruitPostToRecruitDetailResponseDto(RecruitPost recruitPost){
        RecruitPostDetailResponseDto recruitPostDetailResponseDto = new RecruitPostDetailResponseDto();

        BeanUtils.copyProperties(recruitPost,recruitPostDetailResponseDto);
        recruitPostDetailResponseDto.setMemberId(recruitPost.getMember().getId());
        recruitPostDetailResponseDto.setNickName(recruitPost.getMember().getNickName());
        recruitPostDetailResponseDto.setPrfPostId(recruitPost.getPrfPost().getId());

        List<RecruitPostCommentResponseDto> commentResponseDtos = recruitPost.getComments().stream()
                .map(comment -> {
                    RecruitPostCommentResponseDto responseDto = new RecruitPostCommentResponseDto();
                    BeanUtils.copyProperties(comment, responseDto);
                    responseDto.setMemberId(comment.getMember().getId());
                    responseDto.setNickname(comment.getMember().getNickName());
                    return responseDto;
                })
                .collect(Collectors.toList());
        recruitPostDetailResponseDto.setComments(commentResponseDtos);

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
