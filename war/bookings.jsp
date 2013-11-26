<%@page import="java.util.List"%>
<%@page import="ds.gae.CarRentalModel"%>
<%@page import="ds.gae.entities.Reservation"%>
<%@page import="ds.gae.view.JSPSite"%>
<%@page import="ds.gae.view.ViewTools"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<% 
	String renter = (String)session.getAttribute("renter");
	JSPSite currentSite = JSPSite.RESERVATIONS;
	
%>   
 
<%@include file="_header.jsp" %>
			
			
			
	<% 
	 %>
			<div class="groupLabel">Current Reservations</div>
			<div class="group">
				<table>
					<tr>
						<th>Rental Company</th>					
						<th>Car Type/ID</th>
						<th>Rental Period</th>
						<th>Rental Price</th>			
					</tr>
						
	<%
	List<Reservation> reservations = CarRentalModel.get().getReservations(renter);
	
	if ( reservations != null && reservations.size() > 0) {
		
		for (Reservation r : reservations) { 
	 %>
					<tr>
						<td><%= r.getRentalCompany()%></td>
						<td><%= r.getCarType()%>/<%= r.getCarId()%></td>
						<td><%= ViewTools.DATE_FORMAT.format(r.getStartDate()) %> - <%= ViewTools.DATE_FORMAT.format(r.getEndDate())%></td>
						<td class="numbers"><%= r.getRentalPrice()%> €</td>
					</tr>
	<%
		} 
	} else {
	 %>
					<tr><td colspan="6">No Reservations</td></tr>
	<%
	} 
	 %>			
				</table>

			</div>

<%@include file="_footer.jsp" %>
