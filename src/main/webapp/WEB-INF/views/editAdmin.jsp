<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Admin</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/editAdmin.css">
    <script src="${pageContext.request.contextPath}/js/adminFormValidation.js"></script>    
</head>
<body>
    <div class="center-container">
        <h1>Edit Admin</h1>
        <h4>ADMIN ID: <span class="edit-data">${admin.adminId}</span></h4>
        <div class="row">
            <!-- Previous Admin Details -->
            <div class="col-md-5 previous-data-container">
                <div class="admin-details-container">
                    <h2>Previous Admin Details</h2>
                    <table class="table table-bordered">
                        <tbody>
                            <tr>
                                <th scope="row">Admin ID</th>
                                <td>${admin.adminId}</td>
                            </tr>
                            <tr>
                                <th scope="row">Name</th>
                                <td>${admin.name}</td>
                            </tr>
                            <tr>
                                <th scope="row">Username</th>
                                <td>${admin.username}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <!-- Arrow and Text -->
            <div class="col-md-2 arrow-container">
                <div class="arrow">
                    <div class="arrow-text">Update</div>
                </div>
            </div>
            <!-- Form for Editing Admin Details -->
            <div class="col-md-5 new-data-container">
                <div class="admin-details-container">
                    <h2>Edit Admin Details</h2>
                    <c:if test="${not empty errorMessage}">
                        <div class="error-message alert alert-danger" id="error-message"><b>${errorMessage}</b></div>
                    </c:if>
                    <form action="<c:url value='/api/v1/admins/updateAdmin/${admin.adminId}' />" method="POST" onsubmit="return validateForm()">
                        <!-- Hidden input field to specify the HTTP method as PUT -->
                        <input type="hidden" name="_method" value="PUT">
                        <!-- Input fields for admin details -->
                        <div class="form-group">
                            <label for="name"><b>Name:</b></label>
                            <input type="text" class="form-control" id="name" name="name" value="${admin.name}" required>
                            <c:if test="${not empty nameError}">
                                <div class="fieldsError" id="name-error"><b>${nameError}</b></div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label for="username"><b>Username:</b></label>
                            <input type="text" class="form-control" id="username" name="username" value="${admin.username}" required>
                            <c:if test="${not empty usernameError}">
                                <div class="fieldsError" id="username-error"><b>${usernameError}</b></div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label for="password"><b>Password:</b></label>
                            <input type="password" class="form-control" id="password" name="password" value="${password}" required>
                            <c:if test="${not empty passwordError}">
                                <div class="fieldsError" id="password-error"><b>${passwordError}</b></div>
                            </c:if>
                        </div>
                        <div class="form-group form-check">
                            <input type="checkbox" class="form-check-input" id="showPassword" onclick="togglePasswordVisibility()">
                            <label class="form-check-label" for="showPassword"><b>Show Password</b></label>
                        </div>
                        <button type="submit" id="submit-button" class="btn btn-primary btn-center">Update</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>