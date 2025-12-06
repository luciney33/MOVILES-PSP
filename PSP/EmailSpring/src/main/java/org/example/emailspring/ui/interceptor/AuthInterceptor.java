package org.example.emailspring.ui.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.emailspring.common.Constantes;
import org.example.emailspring.domain.error.ForbiddenException;
import org.example.emailspring.domain.error.UnauthorizedException;
import org.example.emailspring.ui.service.AuthService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    public final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //* Business logic just before the response reaches the client and the request is served
        try {
            System.out.println("2 - postHandle() : After the Controller serves the request (before returning back response to the client)");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is called after request & response HTTP communication is done.
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //* Business logic after request and response is Completed
        try {
            System.out.println("3 - afterCompletion() : After the request and Response is completed");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}