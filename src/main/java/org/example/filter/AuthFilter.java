package org.example.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/dashboard", "/produits", "/produits/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);   // ne crée pas de session
        boolean connecte = (session != null && session.getAttribute("user") != null);

        if (connecte) {
            chain.doFilter(request, response);          // laisse passer vers la servlet
        } else {
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }
}