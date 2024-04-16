<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit User</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/editUser.css">
    <script src="${pageContext.request.contextPath}/js/userFormValidation.js"></script>    
</head>
<body>
    <div class="center-container">
        <h1>Edit User</h1>
        <h4>USER ID: <span class="edit-data">${user.userId}</span></h4>
        <div class="row">
            <!-- Previous User Details -->
            <div class="col-md-5 previous-data-container">
                <div class="user-details-container">
                    <h2>Previous User Details</h2>
                    <table class="table table-bordered">
                        <tbody>
                            <tr>
                                <th scope="row">User ID</th>
                                <td>${user.userId}</td>
                            </tr>
                            <tr>
                                <th scope="row">Name</th>
                                <td>${user.name}</td>
                            </tr>
                            <tr>
                                <th scope="row">Username</th>
                                <td>${user.username}</td>
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
            <!-- Form for Editing User Details -->
            <div class="col-md-5 new-data-container">
                <div class="user-details-container">
                    <h2>Edit User Details</h2>
                    <c:if test="${not empty errorMessage}">
                        <div class="error-message alert alert-danger" id="error-message">${errorMessage}</div>
                    </c:if>
                    <form action="<c:url value='/api/v1/admins/updateUser/${user.userId}' />" method="POST" onsubmit="return validateForm()">
                        <!-- Hidden input field to specify the HTTP method as PUT -->
                        <input type="hidden" name="_method" value="PUT">
                        <!-- Input fields for user details -->
                        <div class="form-group">
                            <label for="name"><b>Name:</b></label>
                            <input type="text" class="form-control" id="name" name="name" value="${user.name}" required>
                            <c:if test="${not empty nameError}">
                                <div class="fieldsError" id="name-error">${nameError}</div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label for="username"><b>Username:</b></label>
                            <input type="text" class="form-control" id="username" name="username" value="${user.username}" required>
                            <c:if test="${not empty usernameError}">
                                <div class="fieldsError" id="username-error">${usernameError}</div>
                            </c:if>
                        </div>
                        <div class="form-group">
                            <label for="password"><b>Password:</b></label>
                            <input type="password" class="form-control" id="password" name="password" value="${password}" required>
                            <c:if test="${not empty passwordError}">
                                <div class="fieldsError" id="password-error">${passwordError}</div>
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
