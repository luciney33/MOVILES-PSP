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
                .excludePathPatterns(Constantes.EXCLUDE_URL);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(Constantes.SWAGGER_UI)
                .addResourceLocations(Constantes.CLASSPATH_SWAGGER)
                .resourceChain(false);

        registry.addResourceHandler(Constantes.WEBJARS)
                .addResourceLocations(Constantes.CLASSPATH_WEBJARS)
                .resourceChain(false);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController(Constantes.SWAGGER_UI_HTML, Constantes.SWAGGER_UI_INDEX_HTML);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(Constantes.API)
                .allowedOriginPatterns("*")
                .allowedMethods(Constantes.GET, Constantes.POST, Constantes.PUT, Constantes.DELETE, Constantes.OPTIONS)
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}