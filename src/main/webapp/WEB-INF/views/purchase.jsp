<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <meta charset="UTF-8">
  <title>Purchase Ticket</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<div class="container my-5">
  <h1 class="text-center">Purchase Ticket</h1>

  <c:if test="${not empty message}">
    <div class="alert
        <c:if test="${message.toLowerCase().contains('error')}">alert-danger</c:if>
        <c:if test="${message.toLowerCase().contains('success')}">alert-success</c:if>"
         role="alert">
        ${message}
    </div>
  </c:if>

    <form action="${pageContext.request.contextPath}/user/tickets/purchase" method="get">
      <div class="mb-3">
        <label for="sessionId" class="form-label">Select Film Session:</label>
        <select name="sessionId" id="sessionId" class="form-select" required>
          <c:forEach var="session" items="${filmSessions}">
            <option value="${session.id}">
              ${session.movieTitle}  |  ${session.date} (${session.startTime} - ${session.endTime})  |  ${session.price}
            </option>
          </c:forEach>
        </select>
      </div>
      <button type="submit" class="btn btn-primary">Choose Seat</button>
    </form>

  <c:if test="${not empty selectedSession}">
    <h2 class="text-center">Select your seat for '${selectedSession.movieTitle}'</h2>
    <form action="${pageContext.request.contextPath}/user/tickets/purchase" method="post">
      <input type="hidden" name="sessionId" value="${selectedSession.id}">
      <div class="seat-map">
        <c:forEach var="row" begin="0" end="${(selectedSession.capacity / 10) - 1}">
          <div class="seat-row">
            <c:forEach var="seat" begin="${row * 10 + 1}" end="${row * 10 + 10}">
              <label>
                <input type="radio" name="seatNumber" value="${seat}"
                       ${selectedSession.takenSeats.contains(seat) ? 'disabled' : ''}>
                <span class="seat ${selectedSession.takenSeats.contains(seat) ? 'taken' : 'available'}">${seat}</span>
              </label>
            </c:forEach>
          </div>
        </c:forEach>
      </div>
      <button type="submit" class="btn btn-success mt-3">Purchase</button>
    </form>
  </c:if>
</div>
</body>
</html>
