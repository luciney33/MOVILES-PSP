package org.example.emailspring.ui.config;

import org.example.emailspring.common.Constantes;
import org.example.emailspring.ui.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class AuthInterceptorConfig implements WebMvcConfigurer {
    private final AuthInterceptor authInterceptor;

    public AuthInterceptorConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(Constantes.API)
                .excludePathPatterns(Constantes.AUTH_PUBLIC_LOGIN)
                .excludePathPatterns(Constantes.AUTH_PUBLIC_REGISTER)
                .excludePathPatterns(Constantes.AUTH_PUBLIC_ACTIVAR)
                .excludePathPatterns(Constantes.AUTH_PUBLIC_LOGOUT)
                .excludePathPatterns("/api/auth" + Constantes.AUTH_2FA_VERIFY) // Permitir verificación 2FA sin autenticación previa
                .excludePathPatterns("/api/auth/refresh") // Permitir refresh token sin autenticación
                .excludePathPatterns(Constantes.SWAGGER_UI)
                .excludePathPatterns(Constantes.SWAGGER_DOCS)
                .excludePathPatterns(Constantes.SWAGGER_UI_HTML)
                .excludePathPatterns(Constantes.WEBJARS);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(Constantes.API)
                .allowedOriginPatterns(Constantes.ALL)
                .allowedMethods(Constantes.GET, Constantes.POST, Constantes.PUT, Constantes.DELETE, Constantes.OPTIONS)
                .allowedHeaders(Constantes.ALL)
                .allowCredentials(true)
                .maxAge(3600);
    }
}