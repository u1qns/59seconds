package com.ssafy.fiftyninesec.solution.dto;

import com.ssafy.fiftyninesec.solution.entity.Member;
import lombok.Getter;
import lombok.Builder;

@Getter
@Builder
public class MemberResponseDto {
    private Long id;
    private String participateName;
    private String creatorName;
    private String address;
    private String phone;
    private String profileImage;
    private String creatorIntroduce;

    public static MemberResponseDto of(Member member) {
        return MemberResponseDto.builder()
                .id(member.getId())
                .participateName(member.getParticipateName())
                .creatorName(member.getCreatorName())
                .address(member.getAddress())
                .phone(member.getPhone())
                .profileImage(member.getProfileImage())
                .creatorIntroduce(member.getCreatorIntroduce())
                .build();
    }
}