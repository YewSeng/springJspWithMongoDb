<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.caltech.pojo.Doctor" %>
<%@ page import="com.caltech.constants.Status" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>All Doctors</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <!-- Bootstrap JS -->
	<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.1/dist/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/viewDoctors.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/viewDoctors.css">
</head>
<body>
    <div class="center-container">
        <h1>View Doctors Page</h1>
        <!-- Success or Error Message -->
        <c:if test="${not empty successMessage}">
            <div class="alert alert-success"><b>${successMessage}</b></div>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger"><b>${errorMessage}</b></div>
        </c:if>
        <c:if test="${not empty createdDoctor}">
		    <div class="alert alert-success"><b>Created Doctor Details:</b> 
		        <br><b>DoctorID:</b> ${createdDoctor.doctorId}, <b>Name:</b> ${createdDoctor.name}, <b>Username:</b> ${createdDoctor.username}
		    </div>
		</c:if>
		<c:if test="${not empty oldDoctor}">
		    <div class="alert alert-info"><b>Old Doctor Details:</b> 
		        <br><b>DoctorID:</b> ${oldDoctor.doctorId}, <b>Name:</b> ${oldDoctor.name}, <b>Username:</b> ${oldDoctor.username}
		    </div>
		</c:if>
		
		<c:if test="${not empty newDoctor}">
		    <div class="alert alert-success"><b>Updated Doctor Details:</b> 
		        <br><b>DoctorID:</b> ${newDoctor.doctorId}, <b>Name:</b> ${newDoctor.name}, <b>Username:</b> ${newDoctor.username}
		    </div>
		</c:if>
		
		<c:if test="${not empty deletedDoctor}">
		    <div class="alert alert-danger"><b>Deleted Doctor Details:</b> 
		        <br><b>DoctorID:</b> ${deletedDoctor.doctorId}, <b>Name:</b> ${deletedDoctor.name}, <b>Username:</b> ${deletedDoctor.username}
		    </div>
		</c:if>
		<form action="/api/v1/doctors/filter" method="GET" id="filterForm" class="form-inline mb-3">
		    <label for="searchType" class="mr-2"><b>Search By:</b></label>
		    <select name="searchType" id="searchType" class="form-control mr-2">
		        <option value="name">Name</option>
		        <option value="username">Username</option>
		        <option value="status">Status</option> 
		    </select>
		    <!-- Input for text search -->
		    <div id="text-search" class="form-group">
		        <input type="text" name="searchTerm" id="searchTerm" class="form-control mr-2" 
		               placeholder="${empty param.searchTerm ? 'Search term' : 'default search ->'}">
		    </div>
		    
		    <!-- Input for status search (Initially hidden) -->
		    <div id="status-search" class="form-group">
		        <select class="form-control" id="status" name="searchTerm">
		            <option value="">Select Status</option>
		            <c:forEach items="${statusValues}" var="status">
		                <option value="${status}">${status}</option>
		            </c:forEach>
		        </select>
		    </div>		    
		    <button type="submit" id="filter-button" class="btn btn-primary btn-center">${empty param.searchTerm ? 'Search' : 'Click To Return to Default Search'}</button>
		</form>
        <table class="table table-hover table-dark">
            <thead>
                <tr>
                    <th scope="col">Doctor ID</th>
                    <th scope="col">Name</th>
                    <th scope="col">Username</th>
                    <th scope="col">Availability</th>
                    <th scope="col">Update Doctor</th>
                    <th scope="col">Delete Doctor</th>
                </tr>
            </thead>
            <tbody>
				<c:forEach items="${doctors}" var="doctor" varStatus="loop">
				    <c:set var="index" value="${loop.index}" />
				    <c:set var="highlightClass" value="" />
				
				    <!-- Check if the current doctor is the created doctor -->
				    <c:if test="${not empty createdDoctor and doctor.doctorId eq createdDoctor.doctorId}">
				        <c:set var="highlightClass" value="highlight-created" />
				    </c:if>
				
				    <!-- Check if the current doctor is the updated doctor -->
				    <c:if test="${not empty newDoctor and doctor.doctorId eq newDoctor.doctorId}">
				        <c:set var="highlightClass" value="highlight-updated" />
				    </c:if>
				
				    <!-- Check if the current doctor is the deleted doctor -->
				    <c:if test="${not empty deletedDoctor and doctor.doctorId eq deletedDoctor.doctorId}">
				        <c:set var="highlightClass" value="highlight-deleted" />
				    </c:if>
				
				    <!-- Table row with highlight class -->
				    <tr class="${highlightClass}">
				        <td><c:out value="${doctor.doctorId}" /></td>
				        <td><c:out value="${doctor.name}" /></td>
				        <td><c:out value="${doctor.username}" /></td>
				        <td>
							<c:choose>
							    <c:when test="${doctor.status eq 'AVAILABLE'}">
							        <!-- Green color for Available status -->
							        <span class="available-status"><c:out value="${doctor.status}" /></span>
							    </c:when>
							    <c:otherwise>
							        <!-- Red color for Busy status -->
							        <span class="busy-status"><c:out value="${doctor.status}" /></span>
							    </c:otherwise>
							</c:choose>
				        </td>
				        <td>
				            <!-- Update Doctor button -->
				            <c:url var="updateDoctorUrl" value="/api/v1/doctors/editDoctor/${doctor.doctorId}" />
				            <form action="${updateDoctorUrl}">
				                <button type="submit" id="update-button" class="btn btn-warning btn-center">Update</button>
				            </form>
				        </td>
				        <td>
				            <!-- Delete Doctor button -->
				            <c:url var="deleteDoctorUrl" value="/api/v1/doctors/deleteDoctor/${doctor.doctorId}" />
				            <form action="${deleteDoctorUrl}" method="POST">
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
		                              /api/v1/doctors/filter
		                          </c:when>
		                          <c:otherwise>
		                              /api/v1/doctors/getAllDoctors
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
		            <c:when test="${empty doctors}">
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
		                              /api/v1/doctors/filter
		                          </c:when>
		                          <c:otherwise>
		                              /api/v1/doctors/getAllDoctors
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