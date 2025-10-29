package servlet;

import config.ThymeleafConstants;
import config.UrlConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Estadistica;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.Map;

@WebServlet(UrlConstants.URL_ESTADISTICA)
public class EstadisticaServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, Estadistica> todasEstadisticas = Estadistica.todasEstadisticas();

        TemplateEngine engine = (TemplateEngine) getServletContext()
                .getAttribute(ThymeleafConstants.TEMPLATE_ENGINE_ATTR);
        JakartaServletWebApplication application =
                JakartaServletWebApplication.buildApplication(getServletContext());
        IWebExchange exchange = application.buildExchange(request, response);
        WebContext context = new WebContext(exchange);

        context.setVariable("estadisticas", todasEstadisticas);
        context.setVariable("hayDatos", !todasEstadisticas.isEmpty());

        response.setContentType(ThymeleafConstants.CONTENT_TYPE);
        engine.process(UrlConstants.TEMPLATE_ESTADISTICA, context, response.getWriter());
    }
}