<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.caltech.pojo.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>All Users</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <!-- Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewUsers.css">
</head>
<body>
    <div class="center-container">
        <h1>View Users Page</h1>
        <!-- Success or Error Message -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success"><b>${successMessage}</b></div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger"><b>${errorMessage}</b></div>
        </c:if>
        <c:if test="${not empty createdUser}">
		    <div class="alert alert-success"><b>Created User Details:</b> 
		        <br><b>UserID:</b> ${createdUser.userId}, <b>Name:</b> ${createdUser.name}, <b>Username:</b> ${createdUser.username}
		    </div>
		</c:if>
		<c:if test="${not empty oldUser}">
		    <div class="alert alert-info"><b>Old User Details:</b> 
		        <br><b>UserID:</b> ${oldUser.userId}, <b>Name:</b> ${oldUser.name}, <b>Username:</b> ${oldUser.username}
		    </div>
		</c:if>
		
		<c:if test="${not empty newUser}">
		    <div class="alert alert-success"><b>Updated User Details:</b> 
		        <br><b>UserID:</b> ${newUser.userId}, <b>Name:</b> ${newUser.name}, <b>Username:</b> ${newUser.username}
		    </div>
		</c:if>
		
		<c:if test="${not empty deletedUser}">
		    <div class="alert alert-danger"><b>Deleted User Details:</b> 
		        <br><b>UserID:</b> ${deletedUser.userId}, <b>Name:</b> ${deletedUser.name}, <b>Username:</b> ${deletedUser.username}
		    </div>
		</c:if>
        <!-- Add form for filter -->
		<form action="/api/v1/admins/filterUsers" method="GET" class="form-inline mb-3">
		    <label for="searchType" class="mr-2"><b>Search By:</b></label>
		    <select name="searchType" id="searchType" class="form-control mr-2">
		        <option value="name">Name</option>
		        <option value="username">Username</option>
		    </select>
		    <input type="text" name="searchTerm" id="searchTerm" class="form-control mr-2" placeholder="${empty param.searchTerm ? 'Search term' : 'default search ->'}">
		    <button type="submit" id="filter-button" class="btn btn-primary btn-center">${empty param.searchTerm ? 'Search' : 'Click To Return to Default Search'}</button>
		</form>
        <table class="table table-hover table-dark">
            <thead>
                <tr>
                    <th scope="col">User ID</th>
                    <th scope="col">Name</th>
                    <th scope="col">Username</th>
                    <th scope="col">Update User</th>
                    <th scope="col">Delete User</th>
                </tr>
            </thead>
            <tbody>
				<c:forEach items="${users}" var="user" varStatus="loop">
				    <c:set var="index" value="${loop.index}" />
				    <c:set var="highlightClass" value="" />
				
				    <!-- Check if the current user is the created user -->
				    <c:if test="${not empty createdUser and user.userId eq createdUser.userId}">
				        <c:set var="highlightClass" value="highlight-created" />
				    </c:if>
				
				    <!-- Check if the current user is the updated user -->
				    <c:if test="${not empty newUser and user.userId eq newUser.userId}">
				        <c:set var="highlightClass" value="highlight-updated" />
				    </c:if>
				
				    <!-- Check if the current user is the deleted user -->
				    <c:if test="${not empty deletedUser and user.userId eq deletedUser.userId}">
				        <c:set var="highlightClass" value="highlight-deleted" />
				    </c:if>
				
				    <!-- Table row with highlight class -->
				    <tr class="${highlightClass}">
				        <td><c:out value="${user.userId}" /></td>
				        <td><c:out value="${user.name}" /></td>
				        <td><c:out value="${user.username}" /></td>
				        <td>
				            <!-- Update User button -->
				            <c:url var="updateUserUrl" value="/api/v1/admins/editUser/${user.userId}" />
				            <form action="${updateUserUrl}">
				                <button type="submit" id="update-button" class="btn btn-warning btn-center">Update</button>
				            </form>
				        </td>
				        <td>
				            <!-- Delete User button -->
				            <c:url var="deleteUserUrl" value="/api/v1/admins/deleteUser/${user.userId}" />
				            <form action="${deleteUserUrl}" method="POST">
				                <input type="hidden" name="_method" value="DELETE" />
				                <button type="submit" id="delete-button" class="btn btn-danger btn-center">Delete</button>
				            </form>
				        </td>
				    </tr>
				</c:forEach>
            </tbody>
        </table>
		<!-- Pagination controls -->
		<div>
		    <c:if test="${pageNumber > 0}">
		        <form action="<c:choose>
		                          <c:when test="${not empty param.searchType}">
		                              /api/v1/admins/filterUsers
		                          </c:when>
		                          <c:otherwise>
		                              /api/v1/admins/getAllUsers
		                          </c:otherwise>
		                      </c:choose>"
		              method="GET" class="pagination-form">
		            <input type="hidden" name="searchType" value="${param.searchType}" />
		            <input type="hidden" name="searchTerm" value="${param.searchTerm}" />
		            <input type="hidden" name="page" value="${pageNumber - 1}" /> <!-- Adjust page number for backend -->
		            <input type="hidden" name="size" value="${pageSize}" />
		            <button type="submit" id="previous-button" class="btn btn-success">Previous</button>
		        </form>
		    </c:if>
		    <span class="mx-3">
		        <c:choose>
		            <c:when test="${empty users}">
		                <b>Page 0 of 0</b>
		            </c:when>
		            <c:otherwise>
		                <b>Page ${pageNumber + 1} of ${totalPages}</b> <!-- Display page number correctly -->
		            </c:otherwise>
		        </c:choose>
		    </span>
		    <c:if test="${pageNumber < totalPages - 1}">
		        <form action="<c:choose>
		                          <c:when test="${not empty param.searchType}">
		                              /api/v1/admins/filterUsers
		                          </c:when>
		                          <c:otherwise>
		                              /api/v1/admins/getAllUsers
		                          </c:otherwise>
		                      </c:choose>"
		              method="GET" class="pagination-form">
		            <c:if test="${not empty param.searchType}">
		                <input type="hidden" name="searchType" value="${param.searchType}" />
		                <input type="hidden" name="searchTerm" value="${param.searchTerm}" />
		            </c:if>
		            <input type="hidden" name="page" value="${pageNumber + 1}" /> <!-- Adjust page number for backend -->
		            <input type="hidden" name="size" value="${pageSize}" />
		            <button type="submit" id="next-button" class="btn btn-success">Next</button>
		        </form>
		    </c:if>
		</div>
    </div>
</body>
</html>