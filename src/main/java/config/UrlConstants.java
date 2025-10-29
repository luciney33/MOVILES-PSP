package config;

/**
 * Constantes relacionadas con URLs y rutas de la aplicación web
 */
public final class UrlConstants {

    // Constructor privado para prevenir instanciación
    private UrlConstants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad");
    }

    // URLs de los servlets
    public static final String URL_LOGIN = "/login";
    public static final String URL_JUEGO = "/juego";
    public static final String URL_FINAL = "/final";
    public static final String URL_ESTADISTICA = "/estadistica";

    public static final String PAGE_LOGIN_HTML = "index.html";
    public static final String PAGE_LOGIN_ERROR = "index.html?error=true";

    // Rutas de plantillas Thymeleaf
    public static final String TEMPLATE_PREFIX = "/WEB-INF/templates/";
    public static final String TEMPLATE_SUFFIX = ".html";

    // Nombres de plantillas
    public static final String TEMPLATE_JUEGO = "juego";
    public static final String TEMPLATE_FINAL = "final";
    public static final String TEMPLATE_ESTADISTICA = "estadistica";

}
