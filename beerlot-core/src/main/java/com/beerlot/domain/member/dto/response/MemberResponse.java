package com.beerlot.domain.member.dto.response;

import com.beerlot.domain.member.Member;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@NoArgsConstructor
@Getter
public class MemberResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("email")
    private String email;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("status_message")
    private String statusMessage;

    @JsonProperty("username_updated_at")
    private OffsetDateTime usernameUpdatedAt;

    @Builder
    public MemberResponse (Long id, String username, String email, String imageUrl, String statusMessage, OffsetDateTime usernameUpdatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
        this.statusMessage = statusMessage;
        this.usernameUpdatedAt = usernameUpdatedAt;
    }

    public static MemberResponse of (Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .imageUrl(member.getImageUrl())
                .statusMessage(member.getStatusMessage())
                .usernameUpdatedAt(member.getUsernameUpdatedAt())
                .build();
    }
}
