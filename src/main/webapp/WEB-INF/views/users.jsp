<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
  <title>Управление пользователями</title>
  <link rel="stylesheet" href="/css/admin.css">
</head>
<body>
<h1>Пользователи</h1>
<c:if test="${not empty message}">
  <div class="alert">${message}</div>
</c:if>
<c:choose>
  <c:when test="${empty users}">
    <p>Пользователи отсутствуют.</p>
  </c:when>
  <c:otherwise>
    <table border="1">
      <thead>
      <tr>
        <th>ID</th>
        <th>Имя</th>
        <th>Роль</th>
        <th>Действия</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="user" items="${users}">
        <tr>
          <td>${user.id}</td>
          <td>${user.username}</td>
          <td>${user.role}</td>
          <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/users">
              <input type="hidden" name="id" value="${user.id}">
              <input type="hidden" name="action" value="delete">
              <button type="submit">Удалить</button>
            </form>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </c:otherwise>
</c:choose>

<h2>Добавить пользователя</h2>
<form method="post" action="${pageContext.request.contextPath}/admin/users">
  <input type="text" name="username" placeholder="Имя" required>
  <input type="password" name="password" placeholder="Пароль" required>
  <select name="role">
    <option value="USER">Пользователь</option>
    <option value="ADMIN">Администратор</option>
  </select>
  <input type="hidden" name="action" value="add">
  <button type="submit">Добавить</button>
</form>
</body>
</html>
