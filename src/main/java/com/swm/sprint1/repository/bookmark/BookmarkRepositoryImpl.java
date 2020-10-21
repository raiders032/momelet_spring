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

@RequiredArgsConstructor
public class BookmarkRepositoryImpl implements BookmarkRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BookmarkResponseDto> findAllByUserId(Long userId, Pageable pageable) {
        List<BookmarkResponseDto> content = queryFactory
                .select(Projections.fields(BookmarkResponseDto.class,
                        bookmark.id, restaurant.id.as("restaurantId"), restaurant.name, restaurant.thumUrl))
                .from(bookmark)
                .join(bookmark.restaurant, restaurant)
                .where(bookmark.user.id.eq(userId))
                .orderBy(bookmark.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Bookmark> countQuery = queryFactory
                .select(bookmark)
                .from(bookmark)
                .where(bookmark.user.id.eq(userId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }
}
