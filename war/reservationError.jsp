<%@page import="ds.gae.view.JSPSite"%>
<%@page import="ds.gae.CarRentalModel"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
	String renter = (String)session.getAttribute("renter");
	JSPSite currentSite = JSPSite.RESERVATION_ERROR;
%>   
 
<%@include file="_header.jsp" %>
			
			<div class="frameDiv" style="margin: 150px 280px;">
				<div class="groupLabel">Reservation Error</div>
				<div class="group">
					<div class="form">
						<span><%= (String)session.getAttribute("errorMsg") %></span>					
					</div>
					</form>
				</div>
			</div>

<%@include file="_footer.jsp" %>

