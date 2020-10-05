package com.swm.sprint1.domain;

import com.swm.sprint1.domain.base.DateEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLiking extends DateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_liking_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(precision = 10, scale = 7, nullable = false)
    private BigDecimal userLongitude;

    @Column(precision = 10, scale = 7, nullable = false)
    private BigDecimal userLatitude;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Liking liking;

    @Column(nullable = false)
    private Integer elapsedTime;

    @Builder
    public UserLiking(User user, BigDecimal userLongitude, BigDecimal userLatitude, Restaurant restaurant, Liking liking, Integer elapsedTime) {
        this.user = user;
        this.userLongitude = userLongitude;
        this.userLatitude = userLatitude;
        this.restaurant = restaurant;
        this.liking = liking;
        this.elapsedTime = elapsedTime;
    }

}

