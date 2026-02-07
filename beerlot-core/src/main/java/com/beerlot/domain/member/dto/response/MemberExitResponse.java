package com.beerlot.domain.member.dto.response;

import com.beerlot.domain.auth.security.oauth.entity.ProviderType;
import com.beerlot.domain.member.MemberStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MemberExitResponse {

    private String email;

    private ProviderType provider;

    private MemberStatus status;

    private OffsetDateTime exitedAt;

}
