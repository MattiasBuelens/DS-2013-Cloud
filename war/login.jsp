<%@page import="ds.gae.view.JSPSite"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
	String renter = null;
	JSPSite currentSite = JSPSite.LOGIN;
%>   
 
<%@include file="_header.jsp" %>
			
			<div class="frameDiv" style="margin: 150px 280px;">
				<div class="groupLabel">Login</div>
				<form method="POST" action="/login">
				<div class="group">
					<div class="form">
						<span>Username: <input type="text" name="username" value="TestUser" size="10"></span>					
					</div>
					<div class="formsubmit">
						<input type="submit" value="Login" />
					</div>
					</form>
				</div>
			</div>

<%@include file="_footer.jsp" %>

