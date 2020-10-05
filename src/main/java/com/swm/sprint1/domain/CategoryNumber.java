package com.swm.sprint1.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CategoryNumber {
    Category category;
    Long number = 0L;

    public CategoryNumber(Category category, Long number) {
        this.category = category;
        this.number = number;
    }
}