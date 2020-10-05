package com.swm.sprint1.domain;

import com.swm.sprint1.domain.base.DateEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends DateEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Email
    private String email;

    private String password;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @NotNull
    private String providerId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserCategory> userCategories = new HashSet<>();

    public User(String name, String email, String imageUrl, AuthProvider provider, String providerId, List<Category> categories) {
        this.name=name;
        this.email=email;
        this.imageUrl=imageUrl;
        this.provider = provider;
        this.providerId= providerId;
        categories.forEach(category->userCategories.add(new UserCategory(this,category)));
    }

    public void updateUserInfo(String name, String imageUrl, List<Category> categories) {
        this.name = name;
        this.imageUrl= imageUrl;
        this.userCategories.clear();
        categories.forEach(category ->userCategories.add(new UserCategory(this,category)));
    }

    public void updateUserInfo(List<Category> categories) {
        this.userCategories.clear();
        categories.forEach(category ->userCategories.add(new UserCategory(this,category)));
    }
}
