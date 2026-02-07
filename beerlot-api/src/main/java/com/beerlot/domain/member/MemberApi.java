package com.beerlot.domain.member;

import com.beerlot.annotation.CurrentUser;
import com.beerlot.domain.auth.security.oauth.entity.OAuthUserPrincipal;
import com.beerlot.domain.beer.BeerSortType;
import com.beerlot.domain.beer.dto.response.BeerSimpleResponse;
import com.beerlot.domain.common.entity.LanguageType;
import com.beerlot.domain.common.page.PageCustom;
import com.beerlot.domain.member.dto.request.CheckUsernameRequest;
import com.beerlot.domain.member.dto.request.MemberProfileRequest;
import com.beerlot.domain.member.dto.request.MemberStatusRequest;
import com.beerlot.domain.member.dto.response.CheckUsernameResponse;
import com.beerlot.domain.member.dto.response.MemberExitResponse;
import com.beerlot.domain.member.dto.response.MemberResponse;
import com.beerlot.domain.member.dto.response.MemberStatusResponse;
import com.beerlot.domain.review.ReviewSortType;
import com.beerlot.domain.review.dto.response.ReviewArchiveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Member API", description = "The Member API.")
@RequestMapping("/api/v1/members")
public interface MemberApi {

    @Operation(description = "Get member profile")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "401", description = "No Authorization was found."),
                    @ApiResponse(responseCode = "403", description = "Member has no proper roles."),
                    @ApiResponse(responseCode = "404", description = "Member does not exist.")
            }
    )
    @GetMapping("/me")
    ResponseEntity<MemberResponse> getMemberProfile (
            @Parameter(hidden = true) @CurrentUser OAuthUserPrincipal userPrincipal
    );

    @Operation(description = "Update member profile")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "400", description = "Member already changed username within last 30 days."),
                    @ApiResponse(responseCode = "401", description = "No Authorization was found."),
                    @ApiResponse(responseCode = "403", description = "Member has no proper roles."),
                    @ApiResponse(responseCode = "404", description = "Member does not exist."),
                    @ApiResponse(responseCode = "409", description = "The username already exists.")
            }
    )
    @PutMapping("/me")
    ResponseEntity<MemberResponse> updateMemberProfile (
            @Parameter(hidden = true) @CurrentUser OAuthUserPrincipal userPrincipal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request form for updating profile")
            @RequestBody @Valid MemberProfileRequest memberProfileRequest
    );

    @Operation(description = "Get member status")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "401", description = "No Authorization was found."),
                    @ApiResponse(responseCode = "403", description = "Member has no proper roles."),
                    @ApiResponse(responseCode = "404", description = "Member does not exist.")
            }
    )
    @PostMapping("/status")
    ResponseEntity<MemberStatusResponse> getMemberStatus (
            @RequestBody MemberStatusRequest memberStatusRequest
    );

    @Operation(description = "Get all reviews written by the member")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "401", description = "No Authorization was found."),
                    @ApiResponse(responseCode = "403", description = "Member has no proper roles."),
                    @ApiResponse(responseCode = "404", description = "Member does not exist.")
            }
    )
    @GetMapping("/reviews")
    ResponseEntity<PageCustom<ReviewArchiveResponse>> getAllReviews (
            @Parameter(hidden = true) @CurrentUser OAuthUserPrincipal userPrincipal,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("sort") ReviewSortType sort,
            @Parameter(description = "Language code") @RequestParam("language") LanguageType language
    );

    @Operation(description = "Get all beers that the member likes")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "401", description = "No Authorization was found."),
                    @ApiResponse(responseCode = "403", description = "Member has no proper roles."),
                    @ApiResponse(responseCode = "404", description = "Member does not exist.")
            }
    )
    @GetMapping("/beers")
    ResponseEntity<PageCustom<BeerSimpleResponse>> getAllLikedBeers(
            @Parameter(hidden = true) @CurrentUser OAuthUserPrincipal userPrincipal,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("sort") BeerSortType sort,
            @Parameter(description = "Language code") @RequestParam("language") LanguageType language
    );

    @Operation(description = "Get all reviews that the member likes")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "204", description = "No review liked by the member."),
                    @ApiResponse(responseCode = "401", description = "No Authorization was found."),
                    @ApiResponse(responseCode = "403", description = "Member has no proper roles."),
                    @ApiResponse(responseCode = "404", description = "Member does not exist.")
            }
    )
    @GetMapping("/reviews/likes")
    ResponseEntity<List<Long>> getAllLikedReviews (
            @Parameter(hidden = true) @CurrentUser OAuthUserPrincipal userPrincipal
    );
  
    @Operation(description = "Check for duplicate usernames")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success.")
            }
    )
    @PostMapping("/check-username")
    ResponseEntity<CheckUsernameResponse> checkDuplicateUsername (
            @RequestBody CheckUsernameRequest checkUsernameRequest
    );

    @Operation(description = "Exit Member")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success.")
            }
    )
    @PostMapping("/exit")
    ResponseEntity<MemberExitResponse> exitUser (
            @Parameter(hidden = true) @CurrentUser OAuthUserPrincipal userPrincipal
    );
}
