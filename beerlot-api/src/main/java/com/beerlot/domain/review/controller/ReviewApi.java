package com.beerlot.domain.review.controller;

import com.beerlot.domain.review.ReviewSortType;
import com.beerlot.domain.review.dto.request.ReviewRequest;
import com.beerlot.domain.review.dto.response.ReviewPage;
import com.beerlot.domain.review.dto.response.ReviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1")
public interface ReviewApi {

    @Tag(name = "Review API", description = "The Review API.")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(description = "Create review")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "Success."),
                    @ApiResponse(responseCode = "401", description = "No Authorization was found."),
                    @ApiResponse(responseCode = "403", description = "Member does not have proper rights."),
                    @ApiResponse(responseCode = "404", description = "Beer does not exist.")
            }
    )
    @PostMapping("/beers/{beerId}/reviews")
    ResponseEntity<Void> createReview (
            @Parameter(description = "Beer ID") @PathVariable("beerId") Long beerId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request form for creating review")
            @RequestBody ReviewRequest reviewRequest
    );

    @Tag(name = "Review API", description = "The Review API.")
    @Operation(description = "Get reviews by beer ID")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "204", description = "Empty result."),
                    @ApiResponse(responseCode = "401", description = "No Authorization was found."),
                    @ApiResponse(responseCode = "403", description = "Member does not have proper rights."),
                    @ApiResponse(responseCode = "404", description = "Beer does not exist.")
            }
    )
    @GetMapping("/beers/{beerId}/reviews")
    ResponseEntity<ReviewPage> findReviewsByBeerId (
            @Parameter(description = "Beer ID") @PathVariable("beerId") Long beerId,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("sort") ReviewSortType sort
    );

    @Tag(name = "Review API", description = "The Review API.")
    @Operation(description = "Get all reviews")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success."),
            }
    )
    @GetMapping("/reviews")
    ResponseEntity<ReviewPage> findAllReviews (
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam("sort") ReviewSortType sort
    );

    @Tag(name = "Review API", description = "The Review API.")
    @Operation(description = "Get one review by ID")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "404", description = "Review does not exist."),
            }
    )
    @GetMapping("/reviews/{reviewId}")
    ResponseEntity<ReviewResponse> findReviewById (
            @Parameter(description = "Review ID") @PathVariable("reviewId") Long reviewId
    );

    @Tag(name = "Review API", description = "The Review API.")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(description = "Delete review")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "Success."),
                    @ApiResponse(responseCode = "401", description = "No Authorization was found."),
                    @ApiResponse(responseCode = "403", description = "Member does not have proper rights."),
                    @ApiResponse(responseCode = "404", description = "Review does not exist."),
            }
    )
    @DeleteMapping("/reviews/{reviewId}")
    ResponseEntity<Void> deleteReview (
            @Parameter(description = "Review ID") @PathVariable("reviewId") Long reviewId
    );

    @Tag(name = "Review API", description = "The Review API.")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(description = "Update review")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Success."),
                    @ApiResponse(responseCode = "401", description = "No Authorization was found."),
                    @ApiResponse(responseCode = "403", description = "Member does not have proper rights."),
                    @ApiResponse(responseCode = "404", description = "Review does not exist."),
            }
    )
    @PatchMapping("/reviews/{reviewId}")
    ResponseEntity<ReviewResponse> updateReview (
            @Parameter(description = "Review ID") @PathVariable("reviewId") Long reviewId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Request form for updating review")
            @RequestBody ReviewRequest reviewRequest
    );

}
