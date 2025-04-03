package com.app.springnovels.mock;

import com.app.springnovels.config.auth.CustomUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.ArrayList;
import java.util.Collection;

public class MockAuthorSecurityContextFactory implements WithSecurityContextFactory<MockAuthor> {
    @Override
    public SecurityContext createSecurityContext(MockAuthor customUser) {

        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Collection<? extends GrantedAuthority > authorities = new ArrayList<>();
        CustomUser principal = new CustomUser(customUser.id(), customUser.username(), "", authorities, customUser.displayName());

        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(principal, "", principal.getAuthorities());

        context.setAuthentication(auth);

        return context;
    }
}
