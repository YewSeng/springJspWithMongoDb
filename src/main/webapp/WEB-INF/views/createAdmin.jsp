<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Admin</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/createAdmin.css">
	<script src="${pageContext.request.contextPath}/js/adminFormValidation.js"></script>
</head>
<body>
    <div class="center-container">
        <h1>Create Admin</h1>
        <%-- Display error message if exists --%>
        <c:if test="${not empty errorMessage}">
            <div class="error-message alert alert-danger" id="error-message"><b>${errorMessage}</b></div>
        </c:if>
        <%-- Your form for creating a new admin --%>
        <form action="<c:url value='/api/v1/admins/registerAdmin'/>" method="POST" onsubmit="return validateForm()">
            <%-- Input fields for admin details --%>
            <div class="form-group">
                 <label for="name"><b>Name:</b></label>
                <input type="text" class="form-control" id="name" name="name" value="${name}" required>
		        <c:if test="${not empty nameError}">
		            <div class="fieldsError" id="name-error"><b>${nameError}</b></div>
		        </c:if>
            </div>
            <div class="form-group">
                <label for="username"><b>Username:</b></label>
                <input type="text" class="form-control" id="username" name="username" value="${username}" required>   
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
            <button type="submit" id="submit-button" class="btn btn-primary btn-center">Submit</button>
        </form>
    </div>
</body>
</html>