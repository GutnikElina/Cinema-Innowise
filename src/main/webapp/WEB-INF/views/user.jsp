<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Menu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <style>
        html, body {
            height: 100%;
            margin: 0;
            display: flex;
            flex-direction: column;
        }

        .navbar {
            background-color: #343a40;
        }

        .navbar-brand, .nav-link {
            color: #ffffff !important;
        }
        .hero {
            background: linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)) center/cover;
            color: #ffffff;
            text-align: center;
            padding: 50px 20px;
        }
        .card img {
            height: 300px;
            object-fit: cover;
        }
        .card {
            transition: transform 0.3s;
        }
        .card:hover {
            transform: scale(1.05);
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="#">CinemaApp</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/user/tickets/purchase">Buy Tickets</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/user/tickets">My Tickets</a></li>
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/user/edit">Edit Account</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="hero">
        <div class="container">
            <h1 class="display-4">Welcome to CinemaApp</h1>
            <p class="lead">Discover and book tickets for your favorite movies.</p>
            <form method="get" action="${pageContext.request.contextPath}/user" class="mt-4">
                <div class="input-group">
                    <input type="text" name="movieTitle" class="form-control" placeholder="Enter movie title" required>
                    <button type="submit" class="btn btn-primary">Search</button>
                </div>
            </form>
        </div>
    </div>

    <div class="container my-5">
        <c:if test="${not empty message}">
            <div class="alert alert-warning text-center">${message}</div>
        </c:if>

        <div class="row">
            <c:if test="${not empty movies}">
                <c:forEach var="movie" items="${movies}">
                    <div class="col-md-3 mb-4">
                        <div class="card">
                            <img src="${movie.poster}" class="card-img-top" alt="${movie.title}">
                            <div class="card-body">
                                <h5 class="card-title">${movie.title}</h5>
                                <p class="card-text">Genre: ${movie.genre}</p>
                                <p class="card-text"><small>IMDb Rating: ${movie.imdbRating}</small></p>
                                <a href="#" class="btn btn-primary w-100">View Details</a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
        </div>
    </div>

    <footer class="bg-dark text-white py-3">
        <div class="container text-center">
            <p class="mb-0">&copy; 2024 CinemaApp. All Rights Reserved.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
