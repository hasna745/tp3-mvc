<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head><meta charset="UTF-8"><title>Connexion</title></head>
<body>
    <h1>Connexion</h1>

    <c:if test="${not empty erreur}">
        <p style="color:red"><c:out value="${erreur}"/></p>
    </c:if>

    <form method="post" action="${ctx}/login">
        <input type="text" name="username" placeholder="Login"
               value="<c:out value='${username}'/>">
        <input type="password" name="password" placeholder="Mot de passe">
        <button type="submit">Se connecter</button>
    </form>
</body>
</html>