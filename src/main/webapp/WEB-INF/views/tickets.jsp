<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ticket Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="container my-5">

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="text-center">Ticket Management</h1>
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
                    <th>Seat Number</th>
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
                        <td>
                                ${ticket.filmSession.movieTitle} -
                            <c:out value="${ticket.filmSession.date.format(DateTimeFormatter.ofPattern('dd.MM.yyyy'))} ${ticket.filmSession.startTime.format(DateTimeFormatter.ofPattern('HH:mm'))}" />
                        </td>
                        <td>${ticket.seatNumber}</td>
                        <td><c:out value="${ticket.purchaseTime.format(DateTimeFormatter.ofPattern('dd.MM.yyyy HH:mm'))}" /></td>
                        <td>${ticket.status}</td>
                        <td>${ticket.requestType}</td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/admin/tickets" class="d-inline">
                                <input type="hidden" name="id" value="${ticket.id}">
                                <input type="hidden" name="action" value="delete">
                                <button type="submit" class="btn btn-danger btn-sm delete-btn">Delete</button>
                            </form>
                            <form method="get" action="${pageContext.request.contextPath}/admin/tickets" class="d-inline">
                                <input type="hidden" name="id" value="${ticket.id}">
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

    <div class="row justify-content-between">
        <div class="col-md-6">
            <h2 class="text-center">Add Ticket</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/tickets">
                <input type="hidden" name="action" value="add">
                <div class="mb-3">
                    <select class="form-control form-control-sm" name="userId" required>
                        <option value="" disabled selected>-- Select user --</option>
                        <c:forEach var="user" items="${users}">
                            <option value="${user.id}">${user.username}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="mb-3">
                    <select class="form-control form-control-sm" name="sessionId" required>
                        <option value="" disabled selected>-- Select film session --</option>
                        <c:forEach var="filmSession" items="${filmSessions}">
                            <option value="${filmSession.id}">
                                    ${filmSession.movieTitle} -
                                <c:out value="${filmSession.date.format(DateTimeFormatter.ofPattern('dd.MM.yyyy'))} ${filmSession.startTime.format(DateTimeFormatter.ofPattern('HH:mm'))}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="mb-3">
                    <input type="number" class="form-control form-control-sm" name="seatNumber" placeholder="Select seat number" required>
                </div>
                <div class="mb-3">
                    <select class="form-control form-control-sm" name="status" required>
                        <option value="" disabled selected>-- Select status --</option>
                        <option value="PENDING">Pending</option>
                        <option value="CONFIRMED">Confirmed</option>
                        <option value="CANCELLED">Cancelled</option>
                        <option value="RETURNED">Returned</option>
                    </select>
                </div>
                <div class="mb-3">
                    <select class="form-control form-control-sm" name="requestType" required>
                        <option value="" disabled selected>-- Select request type --</option>
                        <option value="PURCHASE">Purchase</option>
                        <option value="RETURN">Return</option>
                    </select>

                </div>
                <div class="text-center">
                    <button type="submit" class="btn btn-secondary btn-sm">Add</button>
                </div>
            </form>
        </div>

        <c:if test="${not empty ticketToEdit}">
            <div class="col-md-6" id="editForm">
                <h2 class="text-center">Edit Ticket</h2>
                <form method="post" action="${pageContext.request.contextPath}/admin/tickets">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="${ticketToEdit.id}">
                    <div class="mb-3">
                        <select class="form-control form-control-sm" placeholder="Select user" name="userId" required>
                            <c:forEach var="user" items="${users}">
                                <option value="${user.id}" <c:if test="${user.id == ticketToEdit.user.id}">selected</c:if>>${user.username}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <select class="form-control form-control-sm" placeholder="Select film session" name="sessionId" required>
                            <c:forEach var="filmSession" items="${filmSessions}">
                                <option value="${filmSession.id}" <c:if test="${filmSession.id == ticketToEdit.filmSession.id}">selected</c:if>>
                                        ${filmSession.movieTitle} -
                                    <c:out value="${filmSession.date.format(DateTimeFormatter.ofPattern('dd.MM.yyyy'))} ${filmSession.startTime.format(DateTimeFormatter.ofPattern('HH:mm'))}" />
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <input type="number" class="form-control form-control-sm"  placeholder="Select seat number" name="seatNumber" value="${ticketToEdit.seatNumber}" required>
                    </div>
                    <div class="mb-3">
                        <select class="form-control form-control-sm" placeholder="Select status" name="status" required>
                            <option value="PENDING" <c:if test="${ticketToEdit.status == 'PENDING'}">selected</c:if>>Pending</option>
                            <option value="CONFIRMED" <c:if test="${ticketToEdit.status == 'CONFIRMED'}">selected</c:if>>Confirmed</option>
                            <option value="CANCELLED" <c:if test="${ticketToEdit.status == 'CANCELLED'}">selected</c:if>>Cancelled</option>
                            <option value="RETURNED"  <c:if test="${ticketToEdit.status == 'RETURNED'}">selected</c:if>>Returned</option>
                        </select>
                    </div>
                    <div class="mb-3">
                        <select class="form-control form-control-sm" placeholder="Select request type" name="requestType" required>
                            <option value="PURCHASE" <c:if test="${ticketToEdit.requestType == 'PURCHASE'}">selected</c:if>>Purchase</option>
                            <option value="RETURN" <c:if test="${ticketToEdit.requestType == 'RETURN'}">selected</c:if>>Return</option>
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
<script>
    document.querySelectorAll('.delete-btn').forEach(button => {
    button.addEventListener('click', function() {
      const form = this.closest('.delete-form');
      const confirmDelete = confirm('Are you sure you want to delete ticket?');
      if (confirmDelete) {
        form.submit();
      }
    });
  });
</script>

</body>
</html>