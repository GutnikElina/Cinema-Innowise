<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Menu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<div class="container my-5">
    <h1 class="text-center">USER MENU</h1>

    <nav class="mb-5 text-center">
        <a href="${pageContext.request.contextPath}/user/tickets/purchase" class="btn btn-primary">Buy Tickets</a>
        <a href="${pageContext.request.contextPath}/user/tickets" class="btn btn-primary">My Tickets</a>
        <a href="${pageContext.request.contextPath}/user/edit" class="btn btn-primary">Edit Account</a>
    </nav>

    <form method="get" action="${pageContext.request.contextPath}/user" class="mb-5">
        <div class="input-group">
            <input type="text" name="movieTitle" class="form-control" placeholder="Enter movie title" required>
            <button type="submit" class="btn btn-secondary">Search</button>
        </div>
    </form>

    <c:if test="${not empty message}">
        <div class="alert alert-warning text-center">
                ${message}
        </div>
    </c:if>

    <div class="row">
        <c:if test="${not empty movies}">
          <c:forEach var="movie" items="${movies}">
           <div class="col-md-3 mb-4">
             <div class="card">
                 <img src="${movie.poster}" class="card-img-top" alt="${movie.title}">
                 <div class="card-body">
                     <h5 class="card-title">${movie.title}</h5>
                     <p class="card-text">${movie.genre}</p>
                     <p class="card-text"><small>IMDb Rating: ${movie.imdbRating}</small></p>
                 </div>
             </div>
           </div>
          </c:forEach>
        </c:if>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
