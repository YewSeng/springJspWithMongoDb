<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Super Admin Login</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/superAdminLogin.css">
</head>
<body>
    <div class="center-container">
        <h1>Super Admin Login Form</h1>
        <form action="${pageContext.request.contextPath}/superAdminLogin" method="POST" onsubmit="return validateForm()">
            <%-- Input fields for admin details --%>
            <div class="form-group">
                <label for="superAdminKey"><b>Super Admin Key:</b></label>
                <input type="text" class="form-control" id="superAdminKey" name="superAdminKey" value="${superAdminKey}" required>
		        <c:if test="${not empty superAdminKeyError}">
		            <div class="fieldsError" id="superAdminKey-error"><b>${superAdminKeyError}</b></div>
		        </c:if>
            </div>
            <c:choose>
			    <c:when test="${lockout}">
			        <p class="mt-3">You have exceeded the maximum number of attempts. Please try again later.</p>
			        <p class="mt-3">Time left for form to be enabled: ${timerLeftForFormToNotBeDisabled} seconds</p>
			        <button type="button" id="submit-button" class="btn btn-primary btn-center" disabled>Login</button>
			    </c:when>
			    <c:otherwise>
			        <button type="submit" id="submit-button" class="btn btn-primary btn-center">Login</button>
			    </c:otherwise>
			</c:choose>
        </form>
    </div>
</body>
</html>