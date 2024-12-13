<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Your Tickets</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .table-actions button {
            margin-right: 5px;
        }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark">
    <div class="container">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/user">CinemaApp</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/user/tickets/purchase">Buy Tickets</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/user/tickets">My Tickets</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/user/edit">Edit Account</a></li>
                <li class="nav-item">
                    <form method="post" action="${pageContext.request.contextPath}/logout" class="d-inline" id="logoutForm">
                        <button type="button" class="btn btn-outline-light btn-sm" id="logoutButton">Logout</button>
                    </form>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="hero">
    <div class="container">
        <h1 class="display-4">Your Tickets</h1>
        <p class="lead">Manage your tickets and enjoy the show!</p>
    </div>
</div>

<div class="container my-5">
    <c:if test="${not empty message}">
        <div class="alert
        <c:if test="${message.toLowerCase().contains('error')}">alert-danger</c:if>
        <c:if test="${message.toLowerCase().contains('success')}">alert-success</c:if>"
             role="alert">
                ${message}
        </div>
        ${pageContext.session.removeAttribute("message")}
    </c:if>

    <c:if test="${empty tickets}">
        <p class="text-center">You have no tickets yet. Start by booking your first movie!</p>
    </c:if>

    <c:choose>
        <c:when test="${not empty tickets}">
            <table class="table table-hover">
                <thead class="table-dark">
                <tr>
                    <th>Ticket ID</th>
                    <th>Film Title</th>
                    <th>Seat</th>
                    <th>Status</th>
                    <th>Request Type</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="ticket" items="${tickets}">
                    <tr>
                        <td>${ticket.id}</td>
                        <td>${ticket.filmSession.movieTitle}</td>
                        <td>${ticket.seatNumber}</td>
                        <td>${ticket.status}</td>
                        <td>${ticket.requestType}</td>
                        <td class="table-actions">
                            <form method="post" action="${pageContext.request.contextPath}/user/tickets" style="display:inline;">
                                <input type="hidden" name="action" value="returnMyTicket">
                                <input type="hidden" name="id" value="${ticket.id}">
                                <button
                                        type="submit"
                                        class="btn btn-warning btn-sm <c:if test='${!((ticket.status == "PENDING" || ticket.status == "CONFIRMED") && ticket.requestType != "RETURN")}'>btn-secondary</c:if>'"
                                        <c:if test="${!((ticket.status == 'PENDING' || ticket.status == 'CONFIRMED') && ticket.requestType != 'RETURN')}">disabled</c:if>>
                                    Return
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:when>
    </c:choose>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.getElementById('logoutButton').addEventListener('click', function () {
        const confirmLogout = confirm('Are you sure you want to log out?');
        if (confirmLogout) {
            document.getElementById('logoutForm').submit();
        }
    });
</script>
</body>
</html>
