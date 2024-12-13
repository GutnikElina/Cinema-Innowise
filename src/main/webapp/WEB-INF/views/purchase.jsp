<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Purchase Ticket</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="bg-dark text-white">
<div class="container my-5">
  <div class="form-container mx-auto col-lg-8">
    <div class="d-flex justify-content-center align-items-center mb-4 position-relative">
      <a href="${pageContext.request.contextPath}/user" class="text-secondary text-decoration-none position-absolute start-0">
        &larr; Back
      </a>
      <h1 class="mb-0 text-white text-center">Purchase Ticket</h1>
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

    <form action="${pageContext.request.contextPath}/user/tickets/purchase" method="get" class="mb-4 text-center">
      <div class="mb-3">
        <label for="date" class="form-label">Select Date:</label>
        <input type="date" name="date" id="date" class="form-control" value="${selectedDate}">
      </div>
      <button type="submit" class="btn btn-primary mx-auto d-block">View Sessions</button>
    </form>

    <c:if test="${not empty filmSessions}">
      <form action="${pageContext.request.contextPath}/user/tickets/purchase" method="get" class="mb-4 text-center">
        <div class="mb-3">
          <label for="sessionId" class="form-label">Select Film Session:</label>
          <select name="sessionId" id="sessionId" class="form-select" required>
            <c:forEach var="session" items="${filmSessions}">
              <option value="${session.id}" ${selectedSession != null && selectedSession.id == session.id ? 'selected' : ''}>
                  ${session.movieTitle} |  ${session.date} ( ${session.startTime} - ${session.endTime} ) | ${session.price} BYN
              </option>
            </c:forEach>
          </select>
        </div>
        <button type="submit" class="btn btn-primary mx-auto d-block">Choose Seat</button>
      </form>
    </c:if>

    <c:if test="${not empty selectedSession}">
      <h3 class="text-center">Select your seat for '${selectedSession.movieTitle}'</h3>
      <h2 class="text-center">+--------------------------------+</h2>
      <h2 class="text-center">|----------- SCREEN -----------|</h2>
      <h2 class="text-center">+--------------------------------+</h2>
      <form action="${pageContext.request.contextPath}/user/tickets/purchase" method="post" id="seatForm" class="text-center">
        <input type="hidden" name="sessionId" value="${selectedSession.id}">
        <input type="hidden" name="seatNumber" id="selectedSeat" value="">
        <div class="seat-map">
          <c:forEach var="row" begin="0" end="${(selectedSession.capacity / 10) - 1}">
            <div class="seat-row">
              <c:forEach var="seat" begin="${row * 10 + 1}" end="${row * 10 + 10}">
                <button type="button" class="seat-btn ${selectedSession.takenSeats.contains(seat) ? 'taken' : ''}"
                        data-seat-number="${seat}" ${selectedSession.takenSeats.contains(seat) ? 'disabled' : ''}>
                    ${seat}
                </button>
              </c:forEach>
            </div>
          </c:forEach>
        </div>
        <button type="submit" class="btn btn-success mt-3 mx-auto d-block">Purchase</button>
      </form>
    </c:if>
  </div>
</div>

<script>
  document.querySelectorAll('.seat-btn').forEach(button => {
    button.addEventListener('click', () => {
      const selectedSeatInput = document.getElementById('selectedSeat');
      const previouslySelected = document.querySelector('.seat-btn.selected');

      if (previouslySelected) {
        previouslySelected.classList.remove('selected');
      }

      button.classList.add('selected');
      selectedSeatInput.value = button.getAttribute('data-seat-number');
    });
  });
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
