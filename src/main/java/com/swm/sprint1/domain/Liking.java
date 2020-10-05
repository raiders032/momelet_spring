package com.swm.sprint1.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Liking {
    LIKE("좋아요"),
    DISLIKE("싫어요"),
    SOSO("그저그래요");

    private final String sign;
}
