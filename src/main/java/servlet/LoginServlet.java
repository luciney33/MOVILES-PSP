package servlet;

import config.UrlConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(UrlConstants.URL_LOGIN)
public class LoginServlet extends HttpServlet  {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/" + UrlConstants.PAGE_LOGIN_HTML);
            return;
        }

        String usuario = request.getParameter("usuario");
        String password = request.getParameter("password");

        if ("admin".equals(usuario) && "admin".equals(password)) {
            HttpSession session = request.getSession(true);
            session.setAttribute("usuario", usuario);
            session.setAttribute("loginTime", System.currentTimeMillis());
            response.sendRedirect(request.getContextPath() + UrlConstants.URL_JUEGO);
        } else {
            response.sendRedirect(request.getContextPath() + "/" + UrlConstants.PAGE_LOGIN_ERROR);
        }
    }

}