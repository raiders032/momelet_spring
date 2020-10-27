package com.swm.sprint1.domain;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class AdminContext extends User {

    private final Admin admin;

    public AdminContext(Admin admin, Collection<? extends GrantedAuthority> authorities) {
        super(admin.getUsername(), admin.getPassword(), authorities);
        this.admin = admin;
    }
}
