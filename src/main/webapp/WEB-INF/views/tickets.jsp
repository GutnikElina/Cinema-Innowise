<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ticket Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>

<div class="container my-5">

    <c:if test="${not empty message}">
        <div class="alert <c:if test="${message.contains('error')}">error</c:if>
            <c:if test="${!message.contains('error')}">success</c:if>" role="alert">${message}
        </div>
    </c:if>

    <h1 class="text-center">Ticket Management</h1>

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
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="ticket" items="${tickets}">
                    <tr>
                        <td>${ticket.id}</td>
                        <td>${ticket.user.name}</td>
                        <td>${ticket.filmSession.movieTitle}</td>
                        <td>${ticket.seatNumber}</td>
                        <td><c:out value="${ticket.purchaseTime.format(DateTimeFormatter.ofPattern('dd.MM.yyyy HH:mm'))}" /></td>
                        <td>${ticket.status}</td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/admin/tickets" class="d-inline">
                                <input type="hidden" name="id" value="${ticket.id}">
                                <input type="hidden" name="action" value="delete">
                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
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

    <div class="form-row mb-4">
        <div class="col-md-6">
            <h2 class="text-center">Add Ticket</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/tickets">
                <input type="hidden" name="action" value="add">
                <div class="mb-3">
                    <select class="form-control form-control-sm" name="userId" required>
                        <c:forEach var="user" items="${users}">
                            <option value="${user.id}">${user.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="mb-3">
                    <select class="form-control form-control-sm" name="sessionId" required>
                        <c:forEach var="filmSession" items="${filmSessions}">
                            <option value="${filmSession.id}">${filmSession.movieTitle} - ${filmSession.date}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="mb-3">
                    <input type="text" class="form-control form-control-sm" name="seatNumber" placeholder="Seat Number" required>
                </div>
                <div class="mb-3">
                    <input type="datetime-local" class="form-control form-control-sm" name="purchaseTime" required>
                </div>
                <div class="mb-3">
                    <select class="form-control form-control-sm" name="status" required>
                        <option value="Purchased">Purchased</option>
                        <option value="Refunded">Refunded</option>
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
                        <select class="form-control form-control-sm" name="userId" required>
                            <c:forEach var="user" items="${users}">
                                <option value="${user.id}" <c:if test="${user.id == ticketToEdit.user.id}">selected</c:if>>${user.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <select class="form-control form-control-sm" name="sessionId" required>
                            <c:forEach var="filmSession" items="${filmSessions}">
                                <option value="${filmSession.id}" <c:if test="${filmSession.id == ticketToEdit.filmSession.id}">selected</c:if>>${filmSession.movieTitle} - ${filmSession.date}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <input type="text" class="form-control form-control-sm" name="seatNumber" value="${ticketToEdit.seatNumber}" required>
                    </div>
                    <div class="mb-3">
                        <input type="datetime-local" class="form-control form-control-sm" name="purchaseTime" value="${ticketToEdit.purchaseTime}" required>
                    </div>
                    <div class="mb-3">
                        <select class="form-control form-control-sm" name="status" required>
                            <option value="Purchased" <c:if test="${ticketToEdit.status == 'Purchased'}">selected</c:if>>Purchased</option>
                            <option value="Refunded" <c:if test="${ticketToEdit.status == 'Refunded'}">selected</c:if>>Refunded</option>
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

</body>
</html>