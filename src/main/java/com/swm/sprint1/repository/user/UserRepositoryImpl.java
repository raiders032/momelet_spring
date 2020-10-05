package com.swm.sprint1.repository.user;


import com.querydsl.jpa.impl.JPAQueryFactory;

import com.swm.sprint1.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.swm.sprint1.domain.QUser.*;
import static com.swm.sprint1.domain.QUserCategory.userCategory;


@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final  JPAQueryFactory queryFactory;

    @Override
    public List<User> findAllCustom() {
        return queryFactory.select(user)
                .from(user)
                .fetch();
    }

    @Override
    public Optional<User> findUserWithUserCategory(Long id) {
        return Optional.ofNullable(
                queryFactory
                .selectFrom(user)
                .join(user.userCategories, userCategory).fetchJoin()
                .where(user.id.eq(id))
                .fetchOne());
    }

}
