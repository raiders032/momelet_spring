package com.swm.sprint1.repository.menu;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swm.sprint1.payload.response.MenuResponseDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.swm.sprint1.domain.QMenu.menu;

@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MenuResponseDto> findMenuResponseDto(Long restaurantId) {
        return queryFactory
                .select(Projections.fields(MenuResponseDto.class, menu.id, menu.name, menu.price ))
                .from(menu)
                .where(menu.restaurant.id.eq(restaurantId))
                .fetch();
    }
}
