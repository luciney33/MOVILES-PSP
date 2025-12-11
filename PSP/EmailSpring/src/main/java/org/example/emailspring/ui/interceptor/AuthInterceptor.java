package org.example.emailspring.ui.interceptor;

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
    public final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }


    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler){
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequiresAuth requiresAuth = handlerMethod.getMethodAnnotation(RequiresAuth.class);
        if (requiresAuth != null) {
            if (!authService.isAuthenticated(request.getSession())) {
                throw new UnauthorizedException(Constantes.MSG_USER_NOT_AUTHENTICATED);
            }

            if (requiresAuth.admin() && !authService.isAdmin(request.getSession())) {
                throw new ForbiddenException(Constantes.MSG_LOGIN_INVALID);
            }

            return true;
        }
        return true;

    }
}