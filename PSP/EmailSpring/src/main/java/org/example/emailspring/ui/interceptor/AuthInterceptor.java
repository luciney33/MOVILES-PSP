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

        // Extraer token JWT del header Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException(Constantes.MSG_USER_NOT_AUTHENTICATED);
        }

        String token = authHeader.substring(7); // Remover "Bearer "

        try {
            // Validar y extraer información del token
            Claims claims = authService.validateAccessToken(token);

            // Establecer información en el request para uso posterior
            request.setAttribute("authenticated", true);
            request.setAttribute("username", claims.getSubject());
            request.setAttribute("rol", claims.get("rol", String.class));

            // Verificar rol de admin si es requerido
            if (requiresAuth.admin()) {
                String rol = claims.get("rol", String.class);
                if (!"ADMIN".equals(rol)) {
                    throw new ForbiddenException(Constantes.MSG_LOGIN_INVALID);
                }
            }

            return true;

        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token expirado");
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException e) {
            throw new UnauthorizedException("Token inválido");
        } catch (Exception e) {
            throw new UnauthorizedException("Error al procesar token: " + e.getMessage());
        }
    }
}