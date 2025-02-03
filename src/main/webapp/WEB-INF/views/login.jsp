<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
    <h2 class="text-center mb-4 text-warning">
      <spring:message code="welcomeBack.message" />
    </h2>

    <c:if test="${param.error eq 'true'}">
      <div class="alert alert-danger" role="alert">
        <spring:message code="errorLogin.message" />
      </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/login">
      <div class="mb-3">
        <label for="login" class="form-label">
          <spring:message code="username.user" />
        </label>
        <input type="text" id="login" name="username" class="form-control" placeholder="<spring:message code='enterUsername.message' />" required>
      </div>
      <div class="mb-3">
        <label for="password" class="form-label">
          <spring:message code="password.user" />
        </label>
        <input type="password" id="password" name="password" class="form-control" placeholder="<spring:message code='enterPassword.message' />" required>
      </div>
      <div class="text-center">
        <button type="submit" class="btn btn-warning w-100">
          <spring:message code="login.button" />
        </button>
      </div>
    </form>
    <div class="text-center mt-3">
      <a href="${pageContext.request.contextPath}/registration" class="btn btn-link text-white-50 text-decoration-none">
        <spring:message code="register.button" />
      </a>
    </div>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
