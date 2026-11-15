package org.example.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.example.model.ProduitDAO;

@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        ProduitDAO dao = new ProduitDAO();
        ctx.setAttribute("produitDAO", dao);
        ctx.log("AppInitListener : ProduitDAO cree avec " + dao.findAll().size() + " produits");
    }
}