<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Confirm Tickets</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container my-5">

  <div class="d-flex justify-content-between align-items-center mb-3">
    <h1 class="text-center">Confirm Tickets</h1>
    <a href="${pageContext.request.contextPath}/admin" class="btn btn-danger">Back</a>
  </div>

  <c:if test="${not empty message}">
    <div class="alert
        <c:if test="${message.toLowerCase().contains('error')}">alert-danger</c:if>
        <c:if test="${message.toLowerCase().contains('success')}">alert-success</c:if>"
         role="alert">
        ${message}
    </div>
    ${pageContext.session.removeAttribute("message")}
  </c:if>

  <c:choose>
    <c:when test="${empty tickets}">
      <p class="text-center">No tickets available.</p>
    </c:when>
    <c:otherwise>
      <table class="table table-bordered">
        <thead>
        <tr>
          <th>Ticket ID</th>
          <th>User</th>
          <th>Session</th>
          <th>Seat</th>
          <th>Purchase Time</th>
          <th>Status</th>
          <th>Request Type</th>
          <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="ticket" items="${tickets}">
          <tr>
            <td>${ticket.id}</td>
            <td>${ticket.user.username}</td>
            <td>${ticket.filmSession.movieTitle}</td>
            <td>${ticket.seatNumber}</td>
            <td><c:out value="${ticket.purchaseTime.format(DateTimeFormatter.ofPattern('dd.MM.yyyy HH:mm'))}" /></td>
            <td>${ticket.status}</td>
            <td>${ticket.requestType}</td>
            <td>
              <c:if test="${ticket.status == 'PENDING'}">
                <c:choose>
                  <c:when test="${ticket.requestType == 'PURCHASE'}">
                    <form method="post" action="${pageContext.request.contextPath}/admin/tickets/confirm" class="d-inline">
                      <input type="hidden" name="id" value="${ticket.id}">
                      <input type="hidden" name="action" value="confirm">
                      <button type="submit" class="btn btn-success btn-sm">Confirm</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/admin/tickets/confirm" class="d-inline">
                      <input type="hidden" name="id" value="${ticket.id}">
                      <input type="hidden" name="action" value="cancel">
                      <button type="submit" class="btn btn-danger btn-sm">Cancel</button>
                    </form>
                  </c:when>
                  <c:when test="${ticket.requestType == 'RETURN'}">
                    <form method="post" action="${pageContext.request.contextPath}/admin/tickets/confirm" class="d-inline">
                      <input type="hidden" name="id" value="${ticket.id}">
                      <input type="hidden" name="action" value="return">
                      <button type="submit" class="btn btn-primary btn-sm">Return</button>
                    </form>
                    <form method="post" action="${pageContext.request.contextPath}/admin/tickets/confirm" class="d-inline">
                      <input type="hidden" name="id" value="${ticket.id}">
                      <input type="hidden" name="action" value="cancel">
                      <button type="submit" class="btn btn-danger btn-sm">Cancel</button>
                    </form>
                  </c:when>
                </c:choose>
              </c:if>

              <c:if test="${ticket.status == 'CONFIRMED'}">
                <c:if test="${ticket.requestType == 'RETURN'}">
                  <form method="post" action="${pageContext.request.contextPath}/admin/tickets/confirm" class="d-inline">
                    <input type="hidden" name="id" value="${ticket.id}">
                    <input type="hidden" name="action" value="return">
                    <button type="submit" class="btn btn-primary btn-sm">Return</button>
                  </form>
                  <form method="post" action="${pageContext.request.contextPath}/admin/tickets/confirm" class="d-inline">
                    <input type="hidden" name="id" value="${ticket.id}">
                    <input type="hidden" name="action" value="cancel">
                    <button type="submit" class="btn btn-danger btn-sm">Cancel</button>
                  </form>
                </c:if>
              </c:if>

            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:otherwise>
  </c:choose>
</div>
</body>
</html>
