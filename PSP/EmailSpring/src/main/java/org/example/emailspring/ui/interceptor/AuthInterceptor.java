package org.example.emailspring.ui.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.error.ForbiddenException;
import org.example.emailspring.domain.error.UnauthorizedException;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

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

        // Verificar autenticación JWT desde los atributos del request
        Boolean authenticated = (Boolean) request.getAttribute("authenticated");
        if (authenticated == null || !authenticated) {
            throw new UnauthorizedException(Constantes.MSG_USER_NOT_AUTHENTICATED);
        }

        // Verificar rol de admin si es requerido
        if (requiresAuth.admin()) {
            String rol = (String) request.getAttribute("rol");
            if (!"ADMIN".equals(rol)) {
                throw new ForbiddenException(Constantes.MSG_LOGIN_INVALID);
            }
        }

        return true;

    }
}