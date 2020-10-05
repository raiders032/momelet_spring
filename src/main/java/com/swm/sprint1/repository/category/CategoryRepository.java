package com.swm.sprint1.repository.category;

import com.swm.sprint1.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long>, CategoryRepositoryCustom {
    Category findByName(String name);
}
