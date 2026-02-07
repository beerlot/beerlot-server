package com.beerlot.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {
    // ===== Beer ===== //
    BEER__NOT_EXIST("Beer does not exist."),
    BEER_INTERNATIONAL__NOT_EXIST("Beer international does not exist."),

    // ===== Brewery ===== //
    BREWERY__NOT_EXIST("Brewery does not exist."),
    BREWERY_INTERNATIONAL__NOT_EXIST("Brewery international does not exist."),

    // ===== Beer Like ===== //
    BEER_LIKE__NOT_FOUND("Member has not liked the beer yet."),
    BEER_LIKE__CONFLICT("Member already liked the beer."),

    // ===== Review ===== //
    REVIEW__NOT_FOUND("Review does not exist."),
    REVIEW__ALREADY_EXIST("Member already wrote the review."),

    // ===== Review Like ===== //
    REVIEW_LIKE__NOT_FOUND("Member has not liked the review yet."),
    REVIEW_LIKE__CONFLICT("Member already liked the review."),

    // ===== Category ===== //
    CATEGORY__NOT_EXIST("Category does not exist."),
    CATEGORY_INTERNATIONAL__NOT_EXIST("Category international does not exist."),

    // ===== Tag ===== //
    TAG_NOT_FOUND("Tag does not exist."),

    // ===== Member ===== //
    MEMBER__ALREADY_SIGNED_UP("Member already signed up."),
    MEMBER__NOT_EXIST("Member does not exist."),
    MEMBER__UNPROPER_ROLE("Member has no proper role."),
    MEMBER__ACCESS_DENIED("Member has no proper rights."),
    MEMBER__USERNAME_30DAYS("Member already changed username within last 30 days."),
    MEMBER__USERNAME_ALREADY_EXIST("The username already exists."),
    MEMBER__FAILED_TO_REVOKE_OAUTH("Failed to revoke user from OAuth Provider"),

    // ===== Token ===== //
    MEMBER_REFRESH_TOKEN__NOT_EXIST("Member refresh token does not exist."),
    TOKEN__INVALID("Token is invalid."),
    TOKEN__NOT_EXPIRED_YET("Token is not expired yet."),
    TOKEN__EXPIRED("Token is expired."),

    // ===== Auth ===== //
    UNAUTHORIZED_REDIRECT_URI("Redirect URI is not authorized."),
    NO_AUTHORIZATION("No Authorization was found."),

    // ===== File ===== //
    FILE__FILENAME_NULL("Filename cannot be null."),
    FILE__CONVERT_PROCESSING_ERROR("An error has occured while converting the file."),
    FILE__READ_PROCESSING_ERROR("An error has occured while reading the file."),

    // ===== Etc ===== //
    LANGUAGE__INVALID("Language code is invalid or not supported."),
    POLICY__REQUIRED_NOT_AGREED("Required policies have not agreed yet.")
            ;

    private final String message;
}
