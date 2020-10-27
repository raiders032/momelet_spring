package com.swm.sprint1.security;

import com.swm.sprint1.domain.Admin;
import com.swm.sprint1.domain.AdminContext;
import com.swm.sprint1.domain.User;
import com.swm.sprint1.exception.ResourceNotFoundException;
import com.swm.sprint1.repository.AdminRepository;
import com.swm.sprint1.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rajeevkumarsingh on 02/08/17.
 */


@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final AdminRepository adminRepository;

    private final TokenProvider tokenProvider;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with name : " + username));

        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(admin.getRole()));
        return  new AdminContext(admin, roles);
    }

    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("User", "id", id, "200")
        );
        return UserPrincipal.create(user);
    }

    public UserDetails loadUserByJwt(String jwt) {
        String role = tokenProvider.getRoleFromToken(jwt);
        if(role.equals("ROLE_USER")){
            return loadUserById(tokenProvider.getUserIdFromToken(jwt));
        }
        else if(role.equals("ROLE_ADMIN")){
            return loadUserByUsername(tokenProvider.getUsernameFromToken(jwt));
        }
        return null;
    }
}