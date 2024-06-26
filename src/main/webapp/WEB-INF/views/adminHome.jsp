<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin Home Page</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/adminHome.css">
</head>
<body>
    <div class="center-container">
    	<div class="container-fluid">
        	<h1 class="mt-4">Admin Dashboard</h1>
        	<h3>Welcome Home, <span class="authenticated-user">${username}</span>!</h3>
			<div class="row d-flex justify-content-center">
				<div class="card">
					<img src="../../assets/images/user.png" class="card-img-top" alt="admin">
                    <div class="card-body">
                        <h5 class="card-title">User</h5>
                        <p class="card-text">Try to ensure that your acts of kindness don't become open doors of exploitation to others. - Stewart Stafford</p>
                    </div>
                    <div class="card-footer">
                        <small class="text-muted">Last updated eons ago</small>
                        <div class="btn-group">
                            <p><b>Create A User</b></p>
                            <button class="btn btn-primary" id="createUser" onClick="window.location.href='<c:url value='/api/v1/admins/registerUser'/>';">Create a User</button>
                            <p><b>View All Users</b></p>
                            <button class="btn btn-secondary" id="viewUser" onClick="window.location.href='<c:url value='/api/v1/users/viewUsers'/>';">View Users</button>
                        </div>
                    </div>
                </div>
        
    </div>
</body>
</html>