<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Управление сеансами</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>

<div class="container my-5">

    <c:if test="${not empty message}">
        <div class="alert <c:if test="${message.contains('ошибка')}">error</c:if>
            <c:if test="${!message.contains('ошибка')}">success</c:if>" role="alert">${message}
        </div>
    </c:if>

    <h1 class="text-center">Управление сеансами</h1>

    <c:choose>
        <c:when test="${empty filmSessions}">
            <p class="text-center">Сеансов нет.</p>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th>Фильм</th>
                    <th>Цена(BYN)</th>
                    <th>Дата</th>
                    <th>Время начала</th>
                    <th>Время конца</th>
                    <th>Вместимость(чел.)</th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="filmSession" items="${filmSessions}">
                    <tr>
                        <td>${filmSession.movieTitle}</td>
                        <td>${filmSession.price}</td>
                        <td><fmt:formatDate value="${filmSession.date}" pattern="dd.MM.yyyy" /></td>
                        <td><fmt:formatDate value="${filmSession.startTime}" pattern="HH:mm" /></td>
                        <td><fmt:formatDate value="${filmSession.endTime}" pattern="HH:mm" /></td>
                        <td>${filmSession.capacity}</td>
                        <td>
                            <form method="post" action="${pageContext.request.contextPath}/admin/sessions" class="d-inline">
                                <input type="hidden" name="id" value="${filmSession.id}">
                                <input type="hidden" name="action" value="delete">
                                <button type="submit" class="btn btn-danger btn-sm">Удалить</button>
                            </form>
                            <form method="get" action="${pageContext.request.contextPath}/admin/sessions" class="d-inline">
                                <input type="hidden" name="id" value="${filmSession.id}">
                                <input type="hidden" name="action" value="edit">
                                <button type="submit" class="btn btn-warning btn-sm">Редактировать</button>
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
            <h2 class="text-center">Добавить сеанс</h2>
            <form method="post" action="${pageContext.request.contextPath}/admin/sessions">
                <input type="hidden" name="action" value="add">
                <div class="mb-3">
                    <input type="text" class="form-control form-control-sm" id="#movieTitle" name="movieTitle" placeholder="Название фильма" required>
                </div>
                <div class="mb-3">
                    <input type="number" class="form-control form-control-sm" id="#price" name="price" placeholder="Цена (BYN)" step="0.1" required>
                </div>
                <div class="mb-3">
                    <input type="date" class="form-control form-control-sm" id="#date" name="date" required>
                </div>
                <div class="mb-3">
                    <input type="time" class="form-control form-control-sm" id="#startTime" name="startTime" required>
                </div>
                <div class="mb-3">
                    <input type="time" class="form-control form-control-sm" id="#endTime" name="endTime" required>
                </div>
                <div class="mb-3">
                    <input type="number" class="form-control form-control-sm" id="#capacity" name="capacity" placeholder="Вместимость" required>
                </div>
                <div class="text-center">
                    <button type="submit" class="btn btn-secondary btn-sm">Добавить</button>
                </div>
            </form>
        </div>

        <c:if test="${not empty sessionToEdit}">
            <div class="col-md-6" id="editForm">
                <h2 class="text-center">Редактировать сеанс</h2>
                <form method="post" action="${pageContext.request.contextPath}/admin/sessions">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="${sessionToEdit.id}">
                    <div class="mb-3">
                        <input type="text" class="form-control form-control-sm" id="movieTitle" name="movieTitle" value="${sessionToEdit.movieTitle}" required>
                    </div>
                    <div class="mb-3">
                        <input type="number" class="form-control form-control-sm" id="price" name="price" value="${sessionToEdit.price}" step="0.1" required>
                    </div>
                    <div class="mb-3">
                        <input type="date" class="form-control form-control-sm" id="date" name="date" value="${sessionToEdit.date}" required>
                    </div>
                    <div class="mb-3">
                        <input type="time" class="form-control form-control-sm" id="startTime" name="startTime" value="${sessionToEdit.startTime}" required>
                    </div>
                    <div class="mb-3">
                        <input type="time" class="form-control form-control-sm" id="endTime" name="endTime" value="${sessionToEdit.endTime}" required>
                    </div>
                    <div class="mb-3">
                        <input type="number" class="form-control form-control-sm" id="capacity" name="capacity" value="${sessionToEdit.capacity}" required>
                    </div>
                    <div class="text-center">
                        <button type="submit" class="btn btn-primary btn-sm">Обновить</button>
                        <button type="button" class="btn btn-secondary btn-sm" id="cancelEditBtn">Отмена</button>
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