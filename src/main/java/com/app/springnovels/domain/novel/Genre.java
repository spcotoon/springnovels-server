package com.app.springnovels.domain.novel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Genre {

    FANTASY("판타지"),
    MYSTERY("미스터리"),
    ACTION("액션"),
    DRAMA("드라마"),
    COMEDY("코미디");

    private final String text;
}
