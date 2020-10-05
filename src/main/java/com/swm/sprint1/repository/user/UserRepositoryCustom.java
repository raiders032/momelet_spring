package com.swm.sprint1.repository.user;


import com.swm.sprint1.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {
    List<User> findAllCustom();

    Optional<User> findUserWithUserCategory(Long id);
}
