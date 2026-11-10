package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // TODO 1 : lire "user" dans la session SANS en créer une (false)
        HttpSession session = req.getSession(false);
        String user = (session == null) ? null : (String) session.getAttribute("user");

        // Provisoire : sera supprimé quand on aura le filtre (Partie 5)
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // TODO 2 : le placer dans la requête pour la JSP
        req.setAttribute("user", user);

        // TODO 3 : forward vers la vue
        req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
    }
}