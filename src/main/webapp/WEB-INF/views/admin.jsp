<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Панель администратора</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<div class="container my-5">
  <h1 class="text-center">Панель администратора</h1>

  <nav class="mb-5 text-center">
    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">Управление пользователями</a>
    <a href="${pageContext.request.contextPath}/admin/sessions" class="btn btn-primary">Управление сеансами</a>
    <a href="${pageContext.request.contextPath}/admin/tickets" class="btn btn-primary">Управление билетами</a>
    <a href="${pageContext.request.contextPath}/admin/tickets/confirm" class="btn btn-primary">Подтверждение заказов</a>
  </nav>

  <form method="get" action="${pageContext.request.contextPath}/admin" class="mb-5">
    <div class="input-group">
      <input type="text" name="movieTitle" class="form-control" placeholder="Введите название фильма" required>
      <button type="submit" class="btn btn-secondary">Поиск</button>
    </div>
  </form>

  <div class="row">
    <c:choose>
      <c:when test="${not empty movies}">
        <c:forEach var="movie" items="${movies}">
          <div class="col-md-3 mb-4">
            <div class="card">
              <img src="${movie.poster}" class="card-img-top" alt="${movie.title}">
              <div class="card-body">
                <h5 class="card-title">${movie.title}</h5>
                <p class="card-text">${movie.genre}</p>
                <p class="card-text"><small>Рейтинг IMDb: ${movie.imdbRating}</small></p>
              </div>
            </div>
          </div>
        </c:forEach>
      </c:when>
      <c:otherwise>
        <div class="col-12">
          <div class="alert alert-warning text-center" role="alert">
            Фильмы с таким названием не найдены.
          </div>
        </div>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
