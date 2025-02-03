<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Session Management</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<div class="container my-5">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h1 class="text-center">Session Management</h1>
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
        <c:when test="${empty filmSessions}">
            <p class="text-center">No sessions available.</p>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered">
                <thead class="table-dark">
                <tr>
                    <th>Movie</th>
                    <th>Price (BYN)</th>
                    <th>Date</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Capacity</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="filmSession" items="${filmSessions}">
                    <tr>
                        <td>${filmSession.movieTitle}</td>
                        <td>${filmSession.price}</td>
                        <td>${filmSession.date}</td>
                        <td>${filmSession.startTime}</td>
                        <td>${filmSession.endTime}</td>
                        <td>${filmSession.capacity}</td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/admin/sessions/delete" class="d-inline delete-form">
                                <input type="hidden" name="id" value="${filmSession.id}">
                                <button type="button" class="btn btn-danger btn-sm delete-btn">Delete</button>
                            </form>
                            <form method="get" action="${pageContext.request.contextPath}/admin/sessions/edit" class="d-inline">
                                <input type="hidden" name="id" value="${filmSession.id}">
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
            <h2 class="text-center">Add Session</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/sessions/add">
                <div class="mb-3">
                    <select class="form-control form-control-sm" name="movieId" required>
                        <option value="" disabled selected>-- Select movie --</option>
                        <c:forEach var="movie" items="${movies}">
                            <option value="${movie.id}">${movie.title}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="mb-3">
                    <input type="number" class="form-control form-control-sm" name="price" placeholder="Price (BYN)" step="0.1" required>
                </div>
                <div class="mb-3">
                    <input type="date" class="form-control form-control-sm" name="date" required>
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
                <form method="post" action="${pageContext.request.contextPath}/admin/sessions/edit">
                    <input type="hidden" name="id" value="${sessionToEdit.id}">
                    <div class="mb-3">
                        <select class="form-control form-control-sm" name="movieId">
                            <c:forEach var="movie" items="${movies}">
                                <option value="${movie.id}" <c:if test="${movie.id == sessionToEdit.movieId}">selected</c:if>>${movie.title}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <input type="number" class="form-control form-control-sm" name="price" value="${sessionToEdit.price}" step="0.1" required>
                    </div>
                    <div class="mb-3">
                        <input type="date" class="form-control form-control-sm" name="date" value="${sessionToEdit.date}" required>
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

<script>
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            const form = this.closest('.delete-form');
            if (confirm('Are you sure you want to delete this session?')) {
                form.submit();
            }
        });
    });

    const cancelEditBtn = document.getElementById('cancelEditBtn');
    if (cancelEditBtn) {
        cancelEditBtn.addEventListener('click', function() {
            document.getElementById('editForm').style.display = 'none';
        });
    }
</script>
</body>
</html>