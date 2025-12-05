package org.example.emailspring.ui.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.emailspring.ui.service.AuthService;
import org.springframework.http.HttpStatus;
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


    // Request is intercepted by this method before reaching the Controller
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequiresAuth requiresAuth = handlerMethod.getMethodAnnotation(RequiresAuth.class);
        // Si tiene anotación @RequiresAuth, usarla
        if (requiresAuth != null) {
            if (!authService.isAuthenticated(request.getSession())) {
                sendError(response, HttpStatus.UNAUTHORIZED, "Debe iniciar sesión");
                return false;
            }

            if (requiresAuth.admin() && !authService.isAdmin(request.getSession())) {
                sendError(response, HttpStatus.FORBIDDEN, "Acceso denegado");
                return false;
            }

            return true;
        }
        return true;

    }

    // Response is intercepted by this method before reaching the client
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

    private void sendError(HttpServletResponse response, HttpStatus status, String message) throws Exception {
        response.sendError(status.value(), message);
    }
}