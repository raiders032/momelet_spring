package com.swm.sprint1.payload.response;

import com.swm.sprint1.domain.Menu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {
    private String name;
    private int price;

    public MenuDto(String nameAndPrice){
        String [] split = nameAndPrice.split("∬");
        this.name = split[0];
        this.price = Integer.parseInt(split[1]);
    }

    public MenuDto(Menu menu){
        this.name = menu.getName();
        this.price = menu.getPrice();
    }
}
