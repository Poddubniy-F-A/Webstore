package com.webstore.security;

import com.webstore.model.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
public class MyUserDetails implements UserDetails {

    @Getter
    private User user;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return "{noop}" + user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLogin();
    }

    public String getNick() {
        return user.getNick();
    }
}
