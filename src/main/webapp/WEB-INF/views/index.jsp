<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Index Page</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/index.css">
    <script src="${pageContext.request.contextPath}/js/loginFormValidation.js"></script>    
</head>
<body>
    <div class="center-container">
    	<h1>Login Form</h1>
    	<%-- Display error message if exists --%>
        <c:if test="${not empty errorMessage}">
            <div class="error-message alert alert-danger" id="error-message"><b>${errorMessage}</b></div>
        </c:if>
        
        <form action="<c:url value='/login'/>" method="POST" onsubmit="return validateForm()">
            <%-- Input fields for user details --%>
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
            <button type="submit" id="submit-button" class="btn btn-primary btn-center">Login</button>
            
            <%-- Display login error if exists --%>
			<c:if test="${not empty loginError}">
			    <div class="fieldsError" id="login-error">
			        <b>${loginErrorMessage}</b>
			    </div>
			</c:if>
	        
	        <%-- Display attempts remaining --%>
			<c:if test="${remainingAttempts > 0}">
			    <p class="mt-3">Remaining attempts: ${remainingAttempts}</p>
			</c:if>
			
			<%-- Display timer left for next try --%>
			<c:if test="${remainingAttempts == 0}">
			    <p class="mt-3">You have exceeded the maximum number of attempts. Please try again later.</p>
			    <p class="mt-3">Time left for form to be enabled: ${timerLeftForFormToNotBeDisabled} seconds</p>
			</c:if>            
            
            <!-- Registration Link -->
            <p class="mt-3">Don't have an account? <a routerLink="/register">Register here</a></p>
        </form>
    </div>
</body>
</html>
