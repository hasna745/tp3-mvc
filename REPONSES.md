# Réponses TP3

## Question 1 : forward ou sendRedirect pour le login

Quand le login échoue, j'utilise un forward parce que je dois envoyer à login.jsp le message d'erreur et le login que l'utilisateur a tapé. Je les mets avec req.setAttribute, donc ils sont dans la requête. Avec un forward la requête reste la même, donc la JSP peut les lire. Avec un sendRedirect le navigateur ferait une nouvelle requête et les attributs seraient perdus.

Quand le login réussit, j'utilise sendRedirect vers /dashboard. Là je n'ai plus rien à transmettre, et le redirect change l'URL du navigateur en /dashboard. Ça évite aussi que le F5 renvoie le formulaire de connexion.

## Question 2 : F5 après avoir ajouté un produit

(a) Si doPost fait un forward vers la liste, l'URL dans le navigateur reste celle du POST. Quand on appuie sur F5, le navigateur renvoie le même POST (il demande d'abord si on veut renvoyer le formulaire), et le produit est ajouté une deuxième fois. On obtient un doublon.

(b) Si doPost fait un redirect, le navigateur fait ensuite un GET sur /produits. F5 refait seulement ce GET, donc la liste est juste réaffichée et rien n'est ajouté en double.

Ce pattern s'appelle PRG : Post, Redirect, Get. C'est aussi pour ça que le message flash est mis en session et pas dans la requête, parce qu'il doit survivre au redirect.

## Question 3 : XSS avec le nom `<script>alert('XSS')</script>`

Avec `${produit.nom}` directement, le texte est mis tel quel dans la page HTML. Le navigateur croit que c'est une vraie balise script et il exécute le code, donc la boîte d'alerte XSS s'affiche. J'ai testé et ça marche. C'est une faille XSS : quelqu'un pourrait mettre un script qui vole le cookie de session des autres utilisateurs.

Avec `<c:out value="${produit.nom}"/>`, les caractères spéciaux sont remplacés (< devient &lt; et > devient &gt;). Le navigateur affiche donc simplement le texte `<script>alert('XSS')</script>` et ne l'exécute pas.

Conclusion : il ne faut jamais afficher directement ce que l'utilisateur a saisi. Il faut toujours utiliser c:out (ou fn:escapeXml) dans les JSP.

## Question 4 : le filtre AuthFilter

L'avantage du filtre, c'est qu'on écrit la vérification de connexion une seule fois, au lieu de la recopier dans chaque servlet. Si on ajoute une nouvelle page protégée, il suffit de rajouter son URL dans le filtre, alors qu'avant on pouvait oublier le test dans une servlet et laisser la page ouverte à tout le monde. Et si la règle change, on modifie un seul fichier. Les servlets sont aussi plus simples, elles font seulement leur travail de contrôleur.

Deux autres usages classiques des filtres : écrire des logs (garder une trace de chaque requête) et fixer l'encodage UTF-8 de toutes les requêtes. On peut aussi s'en servir pour compresser les réponses.
