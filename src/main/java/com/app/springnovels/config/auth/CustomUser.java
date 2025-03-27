package com.app.springnovels.config.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUser extends User {

    private final Long id;
    private final String displayName;

    public CustomUser(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities, String displayName) {
        super(username, password, authorities);
        this.id = id;
        this.displayName = displayName;
    }
}
