<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.util.Locale" %>
<%
  String lang = (String) session.getAttribute("lang");
  if (lang == null || lang.isEmpty()) {
    lang = "en";
  }
  ResourceBundle messages = ResourceBundle.getBundle("messages", new Locale(lang));
%>
<html>
<head>
  <title>Login</title>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-dark text-white">
<div class="container d-flex justify-content-center align-items-center min-vh-100">
  <div class="form-container bg-secondary mx-auto p-5 shadow-lg rounded">
    <h2 class="text-center mb-4 text-warning"><%= messages.getString("welcomeBack.message") %></h2>

    <c:if test="${not empty message}">
      <div class="alert
        <c:if test="${message.toLowerCase().contains('error')}">alert-danger</c:if>
        <c:if test="${message.toLowerCase().contains('success')}">alert-success</c:if>"
           role="alert">
          ${message}
      </div>
      ${pageContext.session.removeAttribute("message")}
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
      <div class="mb-3">
        <label for="login" class="form-label"><%= messages.getString("username.user") %></label>
        <input type="text" id="login" name="login" class="form-control" placeholder="<%= messages.getString("enterUsername.message") %>" required>
      </div>
      <div class="mb-3">
        <label for="password" class="form-label"><%= messages.getString("password.user") %></label>
        <input type="password" id="password" name="password" class="form-control" placeholder="<%= messages.getString("enterPassword.message") %>" required>
      </div>
      <div class="text-center">
        <button type="submit" class="btn btn-warning w-100"><%= messages.getString("login.button") %></button>
      </div>
    </form>
    <div class="text-center mt-3">
      <a href="${pageContext.request.contextPath}/registration" class="btn btn-link text-white-50 text-decoration-none"><%= messages.getString("register.button") %></a>
    </div>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
