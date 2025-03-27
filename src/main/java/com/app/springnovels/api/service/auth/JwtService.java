package com.app.springnovels.api.service.auth;

import com.app.springnovels.api.exception.UserNotLoginException;
import com.app.springnovels.config.auth.JwtProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    public Claims validateAndExtractClaims(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new UserNotLoginException();
        }
        return JwtProvider.extractToken(accessToken);
    }

    public String getJwtFromCookies(HttpServletRequest request, String cookieName) {
        return JwtProvider.getJwtFromCookies(request, cookieName);
    }
}
