<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Super Admin Home Page</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/superAdminHome.css">
</head>
<body>
    <div class="center-container">
    	<div class="container-fluid">
        	<h1 class="mt-4">Super Admin Dashboard</h1>
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
                            <p><b>Create An Admin</b></p>
                            <button class="btn btn-primary" id="createAdmin" onClick="window.location.href='<c:url value='/api/v1/superadmins/createAdmin'/>';">Create an Admin</button>
                            <p><b>View All Admins</b></p>
                            <button class="btn btn-secondary" id="viewAdmin" onClick="window.location.href='<c:url value='/api/v1/superadmins/getAllAdmins'/>';">View Admins</button>
                        </div>
                    </div>
                </div>
        
    </div>
</body>
</html>