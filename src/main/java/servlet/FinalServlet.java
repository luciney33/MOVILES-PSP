package servlet;

import config.UrlConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@WebServlet(UrlConstants.URL_FINAL)
public class FinalServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/" + UrlConstants.PAGE_LOGIN_HTML);
            return;
        }

        String usuario = (String) session.getAttribute("usuario");
        Integer intentos = (Integer) session.getAttribute("intentosFinales");

        if (intentos == null) {
            response.sendRedirect(request.getContextPath() + UrlConstants.URL_JUEGO);
            return;
        }

        int puntuacion = 50 - (intentos * 5);

        session.removeAttribute("inicioPartida");
        session.removeAttribute("intentosFinales");

        TemplateEngine engine = (TemplateEngine) getServletContext().getAttribute("templateEngine");
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(getServletContext());
        IWebExchange exchange = application.buildExchange(request, response);
        WebContext context = new WebContext(exchange);

        context.setVariable("intentos", intentos);
        context.setVariable("usuario", usuario);
        context.setVariable("puntuacion", puntuacion);

        engine.process(UrlConstants.TEMPLATE_FINAL, context, response.getWriter());
    }
}