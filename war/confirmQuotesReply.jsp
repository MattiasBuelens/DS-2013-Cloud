<%@page import="ds.gae.view.JSPSite"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
	String renter = (String)session.getAttribute("renter");
	JSPSite currentSite = JSPSite.CONFIRM_QUOTES_RESPONSE;

%>   
 
<%@include file="_header.jsp" %>
			
			<div class="frameDiv" style="margin: 150px 150px;">
				<div class="groupLabel">Reply</div>
				<div class="group">
					<p>
					TODO: Here you can give some information to client who is currently 
							logged in as user <%=renter%>.
					</p>
				</div>
			</div>

<%@include file="_footer.jsp" %>

