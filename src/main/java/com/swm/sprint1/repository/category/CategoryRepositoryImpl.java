package com.swm.sprint1.repository.category;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swm.sprint1.domain.Category;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.swm.sprint1.domain.QCategory.*;

@RequiredArgsConstructor
public class CategoryRepositoryImpl implements  CategoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Category> findCategoryByCategoryName(List<String> categories) {
        return queryFactory.select(category)
                .from(category)
                .where(category.name.in(categories)).fetch();
    }
}
