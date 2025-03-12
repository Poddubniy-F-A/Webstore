package com.webstore.security;

import com.webstore.entities.User;
import com.webstore.repositories.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    public static User userFromContext() {
        return ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    private final UsersRepository repository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = repository.findById(login).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String role = "ROLE_";
        switch (user.getStatus()) {
            case cust -> role += "CUSTOMER";
            case mod -> role += "MODERATOR";
            case ww -> role += "WH_WORKER";
        }
        return new MyUserDetails(user, Collections.singletonList(new SimpleGrantedAuthority(role)));
    }
}
