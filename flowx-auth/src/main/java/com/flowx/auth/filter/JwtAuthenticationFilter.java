package com.flowx.auth.filter;

import com.flowx.auth.util.JwtUtil;
import com.flowx.common.security.SecurityUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT authentication filter
 * Extracts and validates JWT token from Authorization header
 *
 * @author FlowX Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Public endpoints to skip JWT validation
     */
    private static final List<String> SKIP_URLS = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/captcha",
            "/auth/refresh-token",
            "/auth/reset-password",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/doc.html",
            "/webjars/**",
            "/actuator/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        // Skip JWT validation for public endpoints
        if (shouldSkip(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                Claims claims = jwtUtil.parseToken(jwt);

                // Check if it's an access token
                String tokenType = claims.get("tokenType", String.class);
                if (!"access".equals(tokenType)) {
                    log.warn("Invalid token type: {}", tokenType);
                    filterChain.doFilter(request, response);
                    return;
                }

                Long userId = claims.get("userId", Long.class);
                String username = claims.getSubject();
                Long tenantId = claims.get("tenantId", Long.class);
                @SuppressWarnings("unchecked")
                List<String> roles = claims.get("roles", List.class);
                @SuppressWarnings("unchecked")
                List<String> permissions = claims.get("permissions", List.class);

                // Build SecurityUser from claims
                SecurityUser securityUser = SecurityUser.builder()
                        .userId(userId)
                        .username(username)
                        .tenantId(tenantId)
                        .roles(roles)
                        .permissions(permissions)
                        .build();

                // Create authentication token
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                securityUser,
                                null,
                                securityUser.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Check if the request URI should skip JWT validation
     */
    private boolean shouldSkip(String requestUri) {
        return SKIP_URLS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }
}
