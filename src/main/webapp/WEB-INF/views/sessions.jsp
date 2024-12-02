<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Session Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>

<div class="container my-5">

    <c:if test="${not empty message}">
        <div class="alert
        <c:if test="${message.toLowerCase().contains('error')}">error</c:if>
        <c:if test="${message.toLowerCase().contains('success')}">success</c:if>"
             role="alert">
                ${message}
        </div>
    </c:if>

    <h1 class="text-center">Session Management</h1>

    <c:choose>
        <c:when test="${empty filmSessions}">
            <p class="text-center">No sessions available.</p>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th>Movie</th>
                    <th>Price (BYN)</th>
                    <th>Date</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Capacity (people)</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="filmSession" items="${filmSessions}">
                    <tr>
                        <td>${filmSession.movieTitle}</td>
                        <td>${filmSession.price}</td>
                        <td><c:out value="${filmSession.date.format(DateTimeFormatter.ofPattern('dd.MM.yyyy'))}" /></td>
                        <td><c:out value="${filmSession.startTime.format(DateTimeFormatter.ofPattern('HH:mm'))}" /></td>
                        <td><c:out value="${filmSession.endTime.format(DateTimeFormatter.ofPattern('HH:mm'))}" /></td>
                        <td>${filmSession.capacity}</td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/admin/sessions" class="d-inline">
                                <input type="hidden" name="id" value="${filmSession.id}">
                                <input type="hidden" name="action" value="delete">
                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                            </form>
                            <form method="get" action="${pageContext.request.contextPath}/admin/sessions" class="d-inline">
                                <input type="hidden" name="id" value="${filmSession.id}">
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
            <h2 class="text-center">Add Session</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/sessions">
                <input type="hidden" name="action" value="add">
                <div class="mb-3">
                    <input type="text" class="form-control form-control-sm" name="movieTitle" placeholder="Movie Title" required>
                </div>
                <div class="mb-3">
                    <input type="number" class="form-control form-control-sm" name="price" placeholder="Price (BYN)" step="0.1" required>
                </div>
                <div class="mb-3">
                    <input type="date" class="form-control form-control-sm" name="date" placeholder="dd.MM.yyyy" required>
                </div>
                <div class="mb-3">
                    <input type="time" class="form-control form-control-sm" name="startTime" required>
                </div>
                <div class="mb-3">
                    <input type="time" class="form-control form-control-sm" name="endTime" required>
                </div>
                <div class="mb-3">
                    <input type="number" class="form-control form-control-sm" name="capacity" placeholder="Capacity" required>
                </div>
                <div class="text-center">
                    <button type="submit" class="btn btn-secondary btn-sm">Add</button>
                </div>
            </form>
        </div>

        <c:if test="${not empty sessionToEdit}">
            <div class="col-md-6" id="editForm">
                <h2 class="text-center">Edit Session</h2>
                <form method="post" action="${pageContext.request.contextPath}/admin/sessions">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="${sessionToEdit.id}">
                    <div class="mb-3">
                        <input type="text" class="form-control form-control-sm" name="movieTitle" value="${sessionToEdit.movieTitle}" required>
                    </div>
                    <div class="mb-3">
                        <input type="number" class="form-control form-control-sm" name="price" value="${sessionToEdit.price}" step="0.1" required>
                    </div>
                    <div class="mb-3">
                        <input type="date" class="form-control form-control-sm" placeholder="dd.MM.yyyy" name="date" value="${sessionToEdit.date}" required>
                    </div>
                    <div class="mb-3">
                        <input type="time" class="form-control form-control-sm" name="startTime" value="${sessionToEdit.startTime}" required>
                    </div>
                    <div class="mb-3">
                        <input type="time" class="form-control form-control-sm" name="endTime" value="${sessionToEdit.endTime}" required>
                    </div>
                    <div class="mb-3">
                        <input type="number" class="form-control form-control-sm" name="capacity" value="${sessionToEdit.capacity}" required>
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
