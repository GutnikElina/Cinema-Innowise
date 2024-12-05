<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <title>Registration</title>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="bg-light">
<div class="container mt-5">
  <div class="form-container mx-auto">
    <h2 class="text-center mb-4">Registration</h2>
    <c:if test="${not empty message}">
      <div class="alert alert-danger">${message}</div>
    </c:if>
    <form method="post" action="${pageContext.request.contextPath}/registration">
      <div class="mb-3">
        <label for="newLogin" class="form-label">Username:</label>
        <input type="text" id="newLogin" name="newLogin" class="form-control" required>
      </div>
      <div class="mb-3">
        <label for="newPassword" class="form-label">Password:</label>
        <input type="password" id="newPassword" name="newPassword" class="form-control" required>
      </div>
      <button type="submit" class="btn btn-primary w-100">Register</button>
    </form>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
