<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Your Tickets</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>

<div class="container my-5">
    <h1 class="text-center mb-4">Your Tickets</h1>

    <c:if test="${not empty message}">
        <div class="alert alert-info" role="alert">${message}</div>
    </c:if>

    <c:if test="${empty tickets}">
        <p class="text-center">You have no tickets.</p>
    </c:if>

    <c:choose>
        <c:when test="${not empty tickets}">
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Ticket ID</th>
                    <th>Film Title</th>
                    <th>Seat Number</th>
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
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/user/tickets" style="display:inline;">
                                <input type="hidden" name="action" value="returnMyTicket">
                                <input type="hidden" name="id" value="${ticket.id}">
                                <button
                                        type="submit"
                                        class="btn btn-warning btn-sm <c:if test='${!((ticket.status == "PENDING" || ticket.status == "CONFIRMED") && ticket.requestType != "RETURN")}'>btn-secondary</c:if>'"
                                        <c:if test="${!((ticket.status == 'PENDING' || ticket.status == 'CONFIRMED') && ticket.requestType != 'RETURN')}">disabled</c:if> >
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
</body>
</html>
