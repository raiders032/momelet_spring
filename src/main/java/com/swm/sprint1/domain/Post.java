package com.swm.sprint1.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String claim;

    private Boolean isDone = false;

    public Post(User user, Restaurant restaurant, String imageUrl, String claim) {
        this.user = user;
        this.restaurant = restaurant;
        this.imageUrl = imageUrl;
        this.claim = claim;
    }
}
