package com.beerlot.domain.member.dto.response;

import com.beerlot.domain.auth.security.oauth.entity.ProviderType;
import com.beerlot.domain.member.MemberStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberStatusResponse {

    @JsonProperty("status")
    private MemberStatus status;

    @JsonProperty("provider")
    private ProviderType provider;


}
