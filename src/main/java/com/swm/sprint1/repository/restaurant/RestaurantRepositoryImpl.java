package com.swm.sprint1.repository.restaurant;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.swm.sprint1.domain.Category;
import com.swm.sprint1.domain.Restaurant;
import com.swm.sprint1.payload.response.RestaurantDtoResponse;
import com.swm.sprint1.payload.response.RetrieveRestaurantResponseV1;
import lombok.RequiredArgsConstructor;
import org.qlrm.mapper.JpaResultMapper;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

import static com.swm.sprint1.domain.QCategory.category;
import static com.swm.sprint1.domain.QRestaurant.*;
import static com.swm.sprint1.domain.QRestaurantCategory.*;

@RequiredArgsConstructor
public class RestaurantRepositoryImpl implements RestaurantRepositoryCustom{

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;
    private final JpaResultMapper jpaResultMapper;

    @Override
    public List<RetrieveRestaurantResponseV1> findRetrieveRestaurantByLatitudeAndLongitudeAndUserCategory(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, List<Category> categoryList) {
       return queryFactory.select(Projections.fields(RetrieveRestaurantResponseV1.class,
                restaurant.id, restaurant.name, restaurant.thumUrl ,restaurant.address, restaurant.roadAddress
                , restaurant.googleId, restaurant.naverId, restaurant.googleRating, restaurant.openingHours
                , restaurant.priceLevel, restaurant.longitude, restaurant.latitude
                , ExpressionUtils.as( Expressions.stringTemplate("function('group_concat', {0})", category.name),"categories")))
                .from(restaurantCategory)
                .join(restaurantCategory.category, category)
                .join(restaurantCategory.restaurant, restaurant)
                .where(latitudeBetween(latitude, radius), longitudeBetween(longitude, radius), restaurantInUserCategory(categoryList))
                .groupBy(restaurant.id)
                .fetch();
    }

    @Override
    public List<RestaurantDtoResponse> findRestaurantDtoResponseByLatitudeAndLongitudeAndUserCategory(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, Long id) {
        String sql =
                "   SELECT  " +
                "       r.restaurant_id, r.name, r.thum_url, " +
                "       group_concat(DISTINCT concat(m.name,'∬', m.price)  order by m.menu_id SEPARATOR '`') as menu, " +
                "       group_concat(DISTINCT c.name order by c.category_id) as categories, " +
                "       r.google_rating, r.google_review_count, r.opening_hours, r.price_level, r.address, r.road_address, " +
                "       r.longitude, r.latitude, r.naver_id, r.google_id, r.phone_number " +
                "   FROM( " +
                "       SELECT restaurant.* " +
                "       FROM restaurant  " +
                "       WHERE (restaurant.latitude between ? and ?) and (restaurant.longitude between ? and ?)) r " +
                "   JOIN restaurant_category rc on r.restaurant_id = rc.restaurant_id " +
                "   JOIN category c on rc.category_id = c.category_id " +
                "   LEFT JOIN menu m on r.restaurant_id = m.restaurant_id " +
                "   WHERE r.restaurant_id in ( " +
                "       SELECT rc.restaurant_id " +
                "       FROM restaurant_category rc " +
                "       WHERE rc.category_id in ( " +
                "           SELECT user_category.category_id " +
                "           FROM user_category " +
                "           WHERE user_category.user_id = ? ) " +
                "       GROUP by rc.restaurant_id) " +
                "   GROUP by r.restaurant_id " +
                "   ORDER by rand() " +
                "   LIMIT 100 ";

        Query query = em.createNativeQuery(sql)
                .setParameter(1, latitude.subtract(radius))
                .setParameter(2, latitude.add(radius))
                .setParameter(3, longitude.subtract(radius))
                .setParameter(4, longitude.add(radius))
                .setParameter(5, id);

        List<RestaurantDtoResponse> list = jpaResultMapper.list(query, RestaurantDtoResponse.class);
        return list;
    }

    @Override
    public List<Restaurant> findByLatitudeAndLongitudeAndCategories(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, List<Category> categoryList) {
        return queryFactory.select(restaurant)
                .from(restaurantCategory)
                .join(restaurantCategory.category, category)
                .join(restaurantCategory.restaurant, restaurant)
                .where(latitudeBetween(latitude, radius), longitudeBetween(longitude, radius), restaurantInUserCategory(categoryList))
                .groupBy(restaurant.id)
                .orderBy(restaurant.googleRating.desc())
                .limit(100)
                .fetch();
    }

    @Override
    public List<Restaurant> findByLatitudeAndLongitudeAndCategory(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, Long category_id, Long limit) {
        return queryFactory.select(restaurant)
                .from(restaurant)
                .join(restaurant.restaurantCategories, restaurantCategory).fetchJoin()
                .join(restaurantCategory.category, category)
                .where(restaurantCategory.category.id.eq(category_id)
                        , longitudeBetween(longitude,radius)
                        , latitudeBetween(latitude,radius)
                        ,  restaurant.id.lt(30))
                .limit(limit)
                .orderBy(restaurant.googleRating.desc())
                .fetch();
    }

    @Override
    public List<RestaurantDtoResponse> findRestaurant7(BigDecimal latitude, BigDecimal longitude, BigDecimal radius, List<Long> ids) {
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

        return jpaResultMapper.list(query, RestaurantDtoResponse.class);
    }

    @Override
    public List<Restaurant> findAllByIdOrderByIdAsc(List<Long> restaurantId) {
        return queryFactory
                .select(restaurant)
                .from(restaurant)
                .where(restaurant.id.in(restaurantId))
                .orderBy(restaurant.id.asc())
                .fetch();
    }

    private BooleanExpression latitudeBetween(BigDecimal latitude, BigDecimal length){
        return latitude != null ? restaurant.latitude.between(latitude.subtract(length), latitude.add(length)) : null;
    }

    private BooleanExpression longitudeBetween(BigDecimal longitude, BigDecimal length){
        return longitude != null ? restaurant.longitude.between(longitude.subtract(length), longitude.add(length)) : null;
    }

    private BooleanExpression restaurantInUserCategory(List<Category> categoryList){
        return !categoryList.isEmpty() ? restaurantCategory.category.in(categoryList) : null;
    }
}
