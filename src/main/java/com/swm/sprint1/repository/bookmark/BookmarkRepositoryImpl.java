package com.swm.sprint1.repository.bookmark;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swm.sprint1.domain.Bookmark;
import com.swm.sprint1.payload.response.BookmarkResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.List;

import static com.swm.sprint1.domain.QBookmark.bookmark;
import static com.swm.sprint1.domain.QRestaurant.restaurant;
import static com.swm.sprint1.domain.QUserLiking.*;

@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BookmarkResponseDto> findDtosByUserId(Long userId, Pageable pageable) {
        List<BookmarkResponseDto> contents = queryFactory
                .select(Projections.fields(BookmarkResponseDto.class,
                        bookmark.id, restaurant.id.as("restaurantId"), restaurant.name, restaurant.thumUrl, userLiking.id.count().as("like")))
                .from(bookmark)
                .join(bookmark.restaurant, restaurant)
                .leftJoin(restaurant.userLikings, userLiking)
                .where(bookmark.user.id.eq(userId))
                .groupBy(restaurant.id)
                .fetch();

        JPAQuery<Bookmark> countQuery = queryFactory
                .select(bookmark)
                .from(bookmark)
                .join(bookmark.restaurant, restaurant)
                .leftJoin(restaurant.userLikings, userLiking)
                .where(bookmark.user.id.eq(userId))
                .groupBy(restaurant.id);

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchCount);
    }
}
