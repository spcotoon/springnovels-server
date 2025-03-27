package com.app.springnovels.config.filter;

import com.app.springnovels.api.exception.InvalidJwtTokenException;
import com.app.springnovels.api.exception.UserNotLoginException;
import com.app.springnovels.api.service.auth.JwtService;
import com.app.springnovels.api.service.auth.SecurityContextService;
import com.app.springnovels.config.auth.CustomUser;
import com.app.springnovels.config.auth.JwtProvider;
import com.app.springnovels.config.util.ResponseUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final SecurityContextService securityContextService;

    private static final List<String> EXCLUDE_URLS = List.of(
            "/author/join",
            "/author/login",
            "/author/logout",
            "/member/login",
            "/member/join",
            "/member/logout",
            "/api/v1/novels/all",
            "/health",
            "/h2-console"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (EXCLUDE_URLS.stream().anyMatch(requestURI::contains)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = jwtService.getJwtFromCookies(request, "accessToken");

            Claims claims = jwtService.validateAndExtractClaims(accessToken);

            securityContextService.setAuthentication(claims, request);

            filterChain.doFilter(request, response);

        } catch (UserNotLoginException | InvalidJwtTokenException e) {
            ResponseUtil.sendErrorResponse(response, e.getStatusCode(), e.getMessage());
        }


    }
}
