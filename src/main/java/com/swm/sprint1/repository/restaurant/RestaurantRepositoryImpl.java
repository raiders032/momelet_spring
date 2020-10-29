package com.swm.sprint1.repository.restaurant;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swm.sprint1.domain.*;
import com.swm.sprint1.payload.request.RestaurantSearchCondition;
import com.swm.sprint1.payload.response.RestaurantResponseDto;
import com.swm.sprint1.payload.response.RetrieveRestaurantResponseV1;
import lombok.RequiredArgsConstructor;
import org.qlrm.mapper.JpaResultMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.swm.sprint1.domain.QCategory.category;
import static com.swm.sprint1.domain.QRestaurant.*;
import static com.swm.sprint1.domain.QRestaurantCategory.*;
import static com.swm.sprint1.domain.QUserCategory.userCategory;
import static com.swm.sprint1.domain.QUserLiking.userLiking;

@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom{

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    private final JpaResultMapper jpaResultMapper;

    @Override
    public List<Restaurant> findAllByIdOrderByIdAsc(List<Long> restaurantId) {
        return queryFactory
                .select(restaurant)
                .from(restaurant)
                .where(restaurant.id.in(restaurantId))
                .orderBy(restaurant.id.asc())
                .fetch();
    }

    @Override
    public Page<RestaurantResponseDto> searchRestaurants(Pageable pageable, RestaurantSearchCondition condition) {
        List<RestaurantResponseDto> content = queryFactory
                .select(Projections.constructor(RestaurantResponseDto.class,
                        restaurant.id, restaurant.name, restaurant.thumUrl,
                        restaurant.address, restaurant.roadAddress,
                        restaurant.longitude, restaurant.latitude, restaurant.phoneNumber))
                .from(restaurant)
                .where(nameLike(condition.getName()),
                        latitudeBetween(condition.getLatitude(), condition.getRadius()),
                        longitudeBetween(condition.getLongitude(), condition.getRadius()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Restaurant> countQuery = queryFactory
                .select(restaurant)
                .from(restaurant)
                .where(nameLike(condition.getName()),
                        latitudeBetween(condition.getLatitude(), condition.getRadius()),
                        longitudeBetween(condition.getLongitude(), condition.getRadius()));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }

    @Override
    public List<RestaurantResponseDto> findRestaurant7(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, List<Long> ids) {
        String sql =
                "   SELECT " +
                "       f.restaurant_id, f.name, f.thum_url, " +
                "       group_concat(DISTINCT concat(m.name,'∬', m.price)  order by m.menu_id SEPARATOR '`') as menu, " +
                "       f.categories,    " +
                "       f.google_rating, f.google_review_count, f.opening_hours, f.price_level, f.address, f.road_address, " +
                "       f.longitude, f.latitude, f.naver_id, f.google_id, f.phone_number " +
                "   FROM(   " +
                "       SELECT   " +
                "           c.*,    " +
                "           group_concat(DISTINCT c.category_name order by c.category_id) as categories " +
                "       FROM (  " +
                "           SELECT " +
                "               a.*, " +
                "               (CASE @vcar WHEN a.category_id THEN @rownum \\:=@rownum+1 ELSE @rownum \\:=1 END) rnum,  " +
                "               (@vcar \\:=a.category_id) vcar  " +
                "           from(  " +
                "               select " +
                "                   r.*, category.name as category_name, " +
                "                   category.category_id  " +
                "               from (  " +
                "                   select restaurant.*   " +
                "                   from restaurant    " +
                "                   where (restaurant.latitude between :lat1 and :lat2) and (restaurant.longitude between :lon1 and :lon2)  " +
                "                   ) r  " +
                "               JOIN restaurant_category rc on r.restaurant_id = rc.restaurant_id  " +
                "               JOIN category on rc.category_id =category.category_id  " +
                "               where rc.category_id in(  " +
                "                   select distinct uc.category_id  " +
                "                   from user_category uc  " +
                "                   where uc.user_id in (:ids)  " +
                "                   )   " +
                "                ) a, (SELECT @vcar \\:=0, @rownum \\:=0 FROM DUAL) b  " +
                "       ORDER BY a.category_id, a.google_rating desc  " +
                "       ) c  " +
                "       where c.rnum <=(select count(uc.user_category_id)  " +
                "           from user_category uc  " +
                "           where uc.category_id = c.category_id   " +
                "           and uc.user_id in (:ids)  " +
                "           group by uc.category_id) * 7  " +
                "       GROUP by c.restaurant_id   " +
                "       ORDER by rand()  " +
                "       limit 7  " +
                "   ) f  " +
                "   left join menu m on m.restaurant_id = f.restaurant_id  " +
                "   group by f.restaurant_id ";

        Query query = em.createNativeQuery(sql)
                .setParameter("lat1", latitude.subtract(radius))
                .setParameter("lat2", latitude.add(radius))
                .setParameter("lon1", longitude.subtract(radius))
                .setParameter("lon2", longitude.add(radius))
                .setParameter("ids", ids);

        return jpaResultMapper.list(query, RestaurantResponseDto.class);
    }

    @Override
    public List<RestaurantResponseDto> findDtosByUserCategory(Long userId, BigDecimal longitude, BigDecimal latitude, BigDecimal radius) {
        QCategory c = new QCategory("c");
        List<Tuple> tuples = queryFactory
                .select(restaurant, userLiking.id.count().as("like"))
                .from(restaurant)
                .join(restaurant.restaurantCategories, restaurantCategory)
                .leftJoin(restaurant.userLikings, userLiking)
                .where( longitudeBetween(longitude, radius),
                        latitudeBetween(latitude, radius),
                        restaurantCategory.category.id.in(JPAExpressions
                                .select(userCategory.category.id)
                                .from(userCategory)
                                .where(userCategory.user.id.eq(userId))))
                .groupBy(restaurant.id)
                .orderBy(Expressions.numberTemplate(Double.class, "rand()").asc())
                .limit(100)
                .fetch();

        List<RestaurantResponseDto> restaurantResponseDtos = tuples.stream()
                .map(tuple -> new RestaurantResponseDto(tuple.get(0, Restaurant.class), tuple.get(1, Long.class)))
                .collect(Collectors.toList());

        return restaurantResponseDtos;
    }

    @Override
    public List<RestaurantResponseDto> findDtos(List<Long> userIds, BigDecimal latitude, BigDecimal longitude, BigDecimal radius, Integer limit) {
        String sql =
                "  SELECT  " +
                "      r5.restaurant_id, r5.name, r5.thum_url, r5.menu,    " +
                "      r5.opening_hours,r5.address, r5.road_address,   " +
                "      r5.longitude, r5.latitude, r5.phone_number,    " +
                "      count(ul.user_liking_id) as likecount " +
                "  FROM( " +
                "      SELECT r4.*, group_concat(DISTINCT concat(m.name, '∬', m.price)  order by m.menu_id SEPARATOR '`') as menu    " +
                "      FROM  " +
                "      (   " +
                "          SELECT r3.*   " +
                "          FROM(    " +
                "              SELECT  r2.*,   " +
                "                      (CASE @vcar WHEN r2.category_id THEN @rownum \\:=@rownum+1 ELSE @rownum \\:=1 END) rnum, " +
                "                      (@vcar \\:=r2.category_id) vcar   " +
                "              FROM(   " +
                "                      (   " +
                "                      SELECT r1.*, rc.category_id " +
                "                      FROM(   " +
                "                          SELECT restaurant.*     " +
                "                          FROM restaurant " +
                "                          WHERE restaurant.latitude BETWEEN :lat1 AND :lat2   " +
                "                          AND restaurant.longitude BETWEEN :lon1 AND :lon2    " +
                "                          ) r1    " +
                "                      JOIN restaurant_category rc ON r1.restaurant_id = rc.restaurant_id  " +
                "                      WHERE rc.category_id in(    " +
                "                          SELECT distinct uc.category_id     " +
                "                          FROM user_category uc                     " +
                "                          WHERE uc.user_id in ( :ids )) " +
                "                      ORDER BY rc.category_id asc, RAND() " +
                "                      ) r2    " +
                "                      ,   " +
                "                      (   " +
                "                          SELECT @vcar \\:=0, @rownum \\:=0   " +
                "                          FROM DUAL   " +
                "                      ) b " +
                "                  )   " +
                "              ) r3    " +
                "          WHERE r3.rnum <=(   " +
                "          select  " +
                "          count(uc.user_category_id)             " +
                "          from    " +
                "          user_category uc             " +
                "          where   " +
                "          uc.category_id = r3.category_id            " +
                "          and uc.user_id in (:ids)             " +
                "          group by    " +
                "          uc.category_id  " +
                "          )* 7     " +
                "          ORDER BY r3.category_id     " +
                "          ) r4    " +
                "              LEFT JOIN menu m on r4.restaurant_id = m.restaurant_id  " +
                "              GROUP BY r4.restaurant_id   " +
                "      ) r5    " +
                "      LEFT JOIN user_liking ul on r5.restaurant_id = ul.restaurant_id " +
                "      GROUP BY r5.restaurant_id   " +
                "      ORDER By rand() " +
                "      LIMIT :limit ";

        Query query = em.createNativeQuery(sql)
                .setParameter("lat1", latitude.subtract(radius))
                .setParameter("lat2", latitude.add(radius))
                .setParameter("lon1", longitude.subtract(radius))
                .setParameter("lon2", longitude.add(radius))
                .setParameter("ids", userIds)
                .setParameter("limit", limit);

        return jpaResultMapper.list(query, RestaurantResponseDto.class);
    }

    @Override
    public List<RestaurantResponseDto> findDtosById(List<Long> restaurantIds) {
        List<Tuple> tuples = queryFactory
                .select(restaurant, userLiking.id.count().as("like"))
                .from(restaurant)
                .leftJoin(restaurant.userLikings, userLiking)
                .where(restaurant.id.in(restaurantIds))
                .groupBy(restaurant.id)
                .fetch();

        return tuples.stream()
                .map(tuple -> new RestaurantResponseDto(tuple.get(0, Restaurant.class), tuple.get(1, Long.class)))
                .collect(Collectors.toList());
    }

    private BooleanExpression nameLike(String name) {
        return name != null ? restaurant.name.like("%"+name+"%") : null;
    }

    private BooleanExpression latitudeBetween(BigDecimal latitude, BigDecimal radius){
        return latitude != null ? restaurant.latitude.between(latitude.subtract(radius), latitude.add(radius)) : null;
    }

    private BooleanExpression longitudeBetween(BigDecimal longitude, BigDecimal radius){
        return longitude != null ? restaurant.longitude.between(longitude.subtract(radius), longitude.add(radius)) : null;
    }

}
