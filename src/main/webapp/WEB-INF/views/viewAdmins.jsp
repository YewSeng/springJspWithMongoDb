<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.caltech.pojo.Admin" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>All Admins</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <!-- Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewAdmins.css">
</head>
<body>
    <div class="center-container">
        <h1>View Admins Page</h1>
        <!-- Success or Error Message -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success"><b>${successMessage}</b></div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger"><b>${errorMessage}</b></div>
        </c:if>
        <c:if test="${not empty createdAdmin}">
		    <div class="alert alert-success"><b>Created Admin Details:</b> 
		        <br><b>UserID:</b> ${createdAdmin.adminId}, <b>Name:</b> ${createdAdmin.name}, <b>Username:</b> ${createdAdmin.username}
		    </div>
		</c:if>
		<c:if test="${not empty oldAdmin}">
		    <div class="alert alert-info"><b>Old Admin Details:</b> 
		        <br><b>UserID:</b> ${oldAdmin.adminId}, <b>Name:</b> ${oldAdmin.name}, <b>Username:</b> ${oldAdmin.username}
		    </div>
		</c:if>
		
		<c:if test="${not empty newAdmin}">
		    <div class="alert alert-success"><b>Updated Admin Details:</b> 
		        <br><b>UserID:</b> ${newAdmin.adminId}, <b>Name:</b> ${newAdmin.name}, <b>Username:</b> ${newAdmin.username}
		    </div>
		</c:if>
		
		<c:if test="${not empty deletedAdmin}">
		    <div class="alert alert-danger"><b>Deleted Admin Details:</b> 
		        <br><b>UserID:</b> ${deletedAdmin.adminId}, <b>Name:</b> ${deletedAdmin.name}, <b>Username:</b> ${deletedAdmin.username}
		    </div>
		</c:if>
        <!-- Add form for filter -->
		<form action="/api/v1/superadmins/filter" method="GET" class="form-inline mb-3">
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
                    <th scope="col">Admin ID</th>
                    <th scope="col">Name</th>
                    <th scope="col">Username</th>
                    <th scope="col">Update Admin</th>
                    <th scope="col">Delete Admin</th>
                </tr>
            </thead>
            <tbody>
				<c:forEach items="${admins}" var="admin" varStatus="loop">
				    <c:set var="index" value="${loop.index}" />
				    <c:set var="highlightClass" value="" />
				
				    <!-- Check if the current admin is the created admin -->
				    <c:if test="${not empty createdAdmin and admin.adminId eq createdAdmin.adminId}">
				        <c:set var="highlightClass" value="highlight-created" />
				    </c:if>
				
				    <!-- Check if the current admin is the updated admin -->
				    <c:if test="${not empty newAdmin and admin.adminId eq newAdmin.adminId}">
				        <c:set var="highlightClass" value="highlight-updated" />
				    </c:if>
				
				    <!-- Check if the current admin is the deleted admin -->
				    <c:if test="${not empty deletedAdmin and admin.adminId eq deletedAdmin.adminId}">
				        <c:set var="highlightClass" value="highlight-deleted" />
				    </c:if>
				
				    <!-- Table row with highlight class -->
				    <tr class="${highlightClass}">
				        <td><c:out value="${admin.adminId}" /></td>
				        <td><c:out value="${admin.name}" /></td>
				        <td><c:out value="${admin.username}" /></td>
				        <td>
				            <!-- Update Admin button -->
				            <c:url var="updateAdminUrl" value="/api/v1/superadmins/editAdmin/${admin.adminId}" />
				            <form action="${updateAdminUrl}">
				                <button type="submit" id="update-button" class="btn btn-warning btn-center">Update</button>
				            </form>
				        </td>
				        <td>
				            <!-- Delete Admin button -->
				            <c:url var="deleteAdminUrl" value="/api/v1/superadmins/deleteAdmin/${admin.adminId}" />
				            <form action="${deleteAdminUrl}" method="POST">
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
		                              /api/v1/superadmins/filter
		                          </c:when>
		                          <c:otherwise>
		                              /api/v1/superadmins/getAllAdmins
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
		            <c:when test="${empty admins}">
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
		                              /api/v1/superadmins/filter
		                          </c:when>
		                          <c:otherwise>
		                              /api/v1/superadmins/getAllAdmins
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