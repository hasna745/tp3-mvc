package org.example.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.model.Produit;
import org.example.model.ProduitDAO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/produits")
public class ProduitServlet extends HttpServlet {

    private static final String VUE_LISTE = "/WEB-INF/views/produits/list.jsp";
    private static final String VUE_FORM  = "/WEB-INF/views/produits/form.jsp";

    private ProduitDAO dao;

    @Override
    public void init() {
        dao = (ProduitDAO) getServletContext().getAttribute("produitDAO");
    }

    // ---------- GET : lister / formulaire vide / formulaire pré-rempli ----------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");

        if ("new".equals(action)) {
            req.setAttribute("produit", new Produit());
            req.getRequestDispatcher(VUE_FORM).forward(req, resp);

        } else if ("edit".equals(action)) {
            Produit p = dao.findById(parseId(req.getParameter("id")));
            if (p == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);   // id invalide ou inconnu
                return;
            }
            req.setAttribute("produit", p);
            req.getRequestDispatcher(VUE_FORM).forward(req, resp);

        } else {
            req.setAttribute("produits", dao.findAll());
            req.getRequestDispatcher(VUE_LISTE).forward(req, resp);
        }
    }

    // ---------- POST : supprimer / créer / modifier ----------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        String action = req.getParameter("action");
        HttpSession session = req.getSession();

        // --- suppression ---
        if ("delete".equals(action)) {
            Produit p = dao.findById(parseId(req.getParameter("id")));
            if (p != null) {
                dao.delete(p.getId());
                session.setAttribute("flash", "Produit « " + p.getNom() + " » supprimé.");
            }
            resp.sendRedirect(req.getContextPath() + "/produits");
            return;
        }

        // --- lecture des champs ---
        String idStr    = req.getParameter("id");
        String nom      = trim(req.getParameter("nom"));
        String desc     = trim(req.getParameter("description"));
        String prixStr  = trim(req.getParameter("prix"));
        String stockStr = trim(req.getParameter("stock"));

        // modification : l'id doit exister
        Long id = null;
        if (idStr != null && !idStr.isBlank()) {
            id = parseId(idStr);
            if (dao.findById(id) == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        // --- validation ---
        Map<String, String> erreurs = new HashMap<>();

        if (nom.isEmpty()) {
            erreurs.put("nom", "Le nom est obligatoire.");
        } else if (nom.length() < 2 || nom.length() > 50) {
            erreurs.put("nom", "Le nom doit contenir entre 2 et 50 caractères.");
        }

        BigDecimal prix = null;
        if (prixStr.isEmpty()) {
            erreurs.put("prix", "Le prix est obligatoire.");
        } else {
            try {
                prix = new BigDecimal(prixStr.replace(',', '.'));
                if (prix.signum() <= 0) {
                    erreurs.put("prix", "Le prix doit être strictement positif.");
                }
            } catch (NumberFormatException e) {
                erreurs.put("prix", "Le prix doit être un nombre décimal.");
            }
        }

        int stock = 0;
        if (!stockStr.isEmpty()) {
            try {
                stock = Integer.parseInt(stockStr);
                if (stock < 0) {
                    erreurs.put("stock", "Le stock doit être supérieur ou égal à 0.");
                }
            } catch (NumberFormatException e) {
                erreurs.put("stock", "Le stock doit être un nombre entier.");
            }
        }

        if (desc.length() > 255) {
            erreurs.put("description", "La description ne doit pas dépasser 255 caractères.");
        }

        // objet "courant" (nouvel objet : on ne modifie pas celui du DAO avant validation)
        Produit produit = new Produit(id, nom, desc, prix, stock);

        // --- échec : FORWARD, on ré-affiche le formulaire ---
        if (!erreurs.isEmpty()) {
            req.setAttribute("erreurs", erreurs);
            req.setAttribute("produit", produit);
            req.setAttribute("prixSaisi", prixStr);     // pour ré-afficher un prix invalide tel quel
            req.setAttribute("stockSaisi", stockStr);
            req.getRequestDispatcher(VUE_FORM).forward(req, resp);
            return;
        }

        // --- succès : sauvegarde + flash + REDIRECT (pattern PRG) ---
        boolean creation = (id == null);
        dao.save(produit);
        session.setAttribute("flash",
                "Produit « " + produit.getNom() + " » " + (creation ? "créé." : "modifié."));
        resp.sendRedirect(req.getContextPath() + "/produits");
    }

    // ---------- utilitaires ----------
    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    /** Convertit en Long, ou renvoie null si absent / invalide. */
    private static Long parseId(String s) {
        if (s == null) return null;
        try {
            return Long.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}