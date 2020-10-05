package com.swm.sprint1.repository.user;


import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swm.sprint1.domain.*;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.swm.sprint1.domain.QCategory.*;
import static com.swm.sprint1.domain.QUserCategory.*;


@RequiredArgsConstructor
public class UserCategoryRepositoryImpl implements UserCategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Category> findCategoryByUserId(Long userId) {
        return  queryFactory.select(category)
                .from(userCategory)
                .where(userCategory.user.id.eq(userId))
                .fetch();
    }

    @Override
    public List<String> findCategoryNameByUserId(Long userId) {
        return  queryFactory.select(category.name)
                .from(userCategory)
                .join(userCategory.category, category)
                .where(userCategory.user.id.eq(userId))
                .fetch();
    }

    @Override
    public Map<String, Integer> findAllCategoryNameByUserId(Long userId) {
        List<Tuple> tuples = queryFactory.select(category.name, new CaseBuilder()
                .when(category.id.in(
                        JPAExpressions.select(userCategory.category.id)
                                .from(userCategory)
                                .where(userCategory.user.id.eq(userId))
                )).then(1)
                .otherwise(0))
                .from(category).fetch();
        Map<String,Integer> result = new HashMap<>();
        tuples.forEach(tuple -> result.put((String)tuple.get(0, Object.class), tuple.get(1, Integer.class)));
        return result;
    }

    @Override
    public List<CategoryNumber> findCategoryAndCountByUserId(List<Long> ids) {
        NumberPath<Long> aliasCount = Expressions.numberPath(Long.class, "number");
        return queryFactory.select(Projections.constructor(CategoryNumber.class, category, category.count().as(aliasCount)))
                .from(userCategory)
                .join(userCategory.category, category)
                .where(userCategory.user.id.in(ids))
                .groupBy(category.id)
                .orderBy(aliasCount.desc())
                .fetch();
    }

    @Override
    public List<Category> findByUserIds(List<Long> ids) {
        return queryFactory
                .select(category).distinct()
                .from(userCategory)
                .join(userCategory.category, category)
                .where(userCategory.user.id.in(ids))
                .fetch();
    }


}
