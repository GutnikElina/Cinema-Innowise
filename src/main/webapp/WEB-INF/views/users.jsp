<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>User Management</title>

  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">

</head>
<body>

<div class="container my-5">
  <c:if test="${not empty message}">
    <div class="alert <c:if test="${message.contains('error')}">error</c:if><c:if test="${!message.contains('error')}">success</c:if>" role="alert">
        ${message}
    </div>
  </c:if>

  <h1 class="text-center">User Management</h1>

  <c:choose>
    <c:when test="${empty users}">
      <p class="text-center">No users available.</p>
    </c:when>
    <c:otherwise>
      <table class="table table-bordered">
        <thead>
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>Role</th>
          <th>Created At</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="user" items="${users}">
          <tr>
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>${user.role}</td>
            <td><c:out value="${user.createdAt.format(DateTimeFormatter.ofPattern('dd.MM.yyyy HH:mm'))}" /></td>
            <td>
              <form method="post" action="${pageContext.request.contextPath}/admin/users" class="d-inline">
                <input type="hidden" name="id" value="${user.id}">
                <input type="hidden" name="action" value="delete">
                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
              </form>
              <form method="get" action="${pageContext.request.contextPath}/admin/users" class="d-inline">
                <input type="hidden" name="id" value="${user.id}">
                <input type="hidden" name="action" value="edit">
                <button type="submit" class="btn btn-warning btn-sm">Edit</button>
              </form>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>

  <div class="form-row">
    <div class="form-container">
      <h2 class="text-center">Add User</h2>
      <form method="post" action="${pageContext.request.contextPath}/admin/users">
        <div class="mb-3">
          <input type="text" class="form-control" name="username" placeholder="Name" required>
        </div>
        <div class="mb-3">
          <input type="password" class="form-control" name="password" placeholder="Password" required>
        </div>
        <div class="mb-3">
          <select class="form-select" name="role">
            <option value="USER">User</option>
            <option value="ADMIN">Administrator</option>
          </select>
        </div>
        <input type="hidden" name="action" value="add">
        <div class="text-center">
          <button type="submit" class="btn btn-secondary btn-sm">Add</button>
        </div>
      </form>
    </div>

    <c:if test="${not empty user}">
      <div class="form-container" id="editForm">
        <h2>Edit User</h2>
        <form method="post" action="${pageContext.request.contextPath}/admin/users">
          <input type="hidden" name="action" value="update">
          <input type="hidden" name="id" value="${user.id}">
          <div class="mb-3">
            <input type="text" class="form-control" name="username" value="${user.username}" placeholder="Name" required>
          </div>
          <div class="mb-3">
            <input type="password" class="form-control"  name="password" placeholder="Password" required>
          </div>
          <div class="mb-3">
            <select class="form-select" name="role">
              <option value="USER" <c:if test="${user.role == 'USER'}">selected</c:if>>User</option>
              <option value="ADMIN" <c:if test="${user.role == 'ADMIN'}">selected</c:if>>Administrator</option>
            </select>
          </div>
          <div class="text-center">
            <button type="submit" class="btn btn-primary btn-sm">Update</button>
            <button type="button" class="btn btn-secondary btn-sm" id="cancelEditBtn">Cancel</button>
          </div>
        </form>
      </div>
    </c:if>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script>
  document.getElementById('cancelEditBtn').addEventListener('click', function() {
    document.getElementById('editForm').style.display = 'none';
  });
</script>

</body>
</html>
