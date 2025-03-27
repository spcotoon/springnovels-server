package com.app.springnovels.api.service.auth;

import com.app.springnovels.config.auth.CustomUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityContextService {
    public void setAuthentication(Claims claims, HttpServletRequest request) {
        Long id = claims.get("sub", Long.class);
        String email = claims.get("email", String.class);
        String displayName = claims.get("displayName", String.class);
        String authorities = claims.get("authority", String.class);

        CustomUser customUser = new CustomUser(id, email, "", List.of(new SimpleGrantedAuthority(authorities)), displayName);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUser, null, customUser.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
