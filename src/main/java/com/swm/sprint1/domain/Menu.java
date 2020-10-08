package com.swm.sprint1.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.swm.sprint1.payload.response.MenuDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Menu{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="restaurant_id")
    private Restaurant restaurant;

    private String name;

    private int price;

    private boolean isPopular = false;

    public Menu(Restaurant restaurant, MenuDto menuDto) {
        this.restaurant = restaurant;
        this.name = menuDto.getName();
        this.price = menuDto.getPrice();
    }
}