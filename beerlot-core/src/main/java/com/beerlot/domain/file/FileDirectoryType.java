package com.beerlot.domain.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FileDirectoryType {
    BEER("beers"),
    REVIEW("reviews"),
    PROFILE("members");

    private final String directoryName;
}
