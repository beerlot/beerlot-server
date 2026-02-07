package com.beerlot.domain.member.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberStatusRequest {

    @JsonProperty("email")
    private String email;

    @Builder
    public MemberStatusRequest(String email) {
        this.email = email;
    }
}
