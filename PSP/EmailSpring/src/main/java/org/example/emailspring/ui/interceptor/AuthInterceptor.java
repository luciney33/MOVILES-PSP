package org.example.emailspring.ui.interceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.error.ForbiddenException;
import org.example.emailspring.domain.error.UnauthorizedException;
import org.example.emailspring.ui.service.AuthService;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    @SuppressWarnings("java:S3516")
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler){
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequiresAuth requiresAuth = handlerMethod.getMethodAnnotation(RequiresAuth.class);
        if (requiresAuth == null) {
            return true;
        }

        String authHeader = request.getHeader(Constantes.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(Constantes.BEARER)) {
            throw new UnauthorizedException(Constantes.MSG_USER_NOT_AUTHENTICATED);
        }

        String token = authHeader.substring(Constantes.BEARER_PREFIX_LENGTH);

        try {
            Claims claims = authService.validateAccessToken(token);

            request.setAttribute(Constantes.AUTHENTICATED, true);
            request.setAttribute(Constantes.USERNAME, claims.getSubject());
            request.setAttribute(Constantes.ROL, claims.get(Constantes.ROL, String.class));

            if (requiresAuth.admin()) {
                String rol = claims.get(Constantes.ROL, String.class);
                if (!Constantes.ADMIN.equals(rol)) {
                    throw new ForbiddenException(Constantes.MSG_LOGIN_INVALID);
                }
            }

            return true;

        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(Constantes.MSG_TOKEN_EXPIRADO);
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {
            throw new UnauthorizedException(Constantes.MSG_TOKEN_INVALIDO);
        } catch (Exception e) {
            throw new UnauthorizedException(Constantes.MSG_ERROR_PROCESAR_TOKEN + e.getMessage());
        }
    }
}