<%@page import="java.util.List"%>
<%@page import="ds.gae.CarRentalModel"%>
<%@page import="ds.gae.entities.Notification"%>
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
if (currentSite != JSPSite.LOGIN && currentSite != JSPSite.PERSIST_TEST && renter == null) {
 %>
	<meta http-equiv="refresh" content="0;URL='/login.jsp'">
<% 
  request.getSession().setAttribute("lastSiteCall", currentSite);
} 
 %>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link rel="stylesheet" type="text/css" href="style.css" />
	<title>Car Rental Application</title>
</head>
<body>
	<div id="mainWrapper">
		<div id="headerWrapper">
			<h1>Car Rental Application</h1>
		</div>
		<div id="navigationWrapper">
			<ul>
<% 
for (JSPSite site : JSPSite.publiclyLinkedValues()) {
	if (site == currentSite) {
 %> 
				<li><a class="selected" href="<%=site.url()%>"><%=site.label()%></a></li>
<% } else {
 %> 
				<li><a href="<%=site.url()%>"><%=site.label()%></a></li>
<% }}
 %> 

				</ul>
		</div>
		<div id="contentWrapper">
<% if (currentSite != JSPSite.LOGIN) { %>
			<div id="userProfile">
				<span>Logged-in as <%= renter %> (<a href="/login.jsp">change</a>)</span>
			</div>
<%
   }
 %>
			
			
	<% 
	 %>
			<div class="groupLabel">Notifications</div>
			<div class="group">
				<table>
					<tr>
						<th>Message</th>					
						<th>Time</th>			
					</tr>
						
	<%
	List<Notification> notifications = CarRentalModel.get().getNotifications(renter);
	
	if ( notifications != null && notifications.size() > 0) {
		
		for (Notification n : notifications) { 
	 %>
					<tr>
						<td><%= n.getMessage()%></td>
						<td><%= ViewTools.TIMESTAMP_FORMAT.format(n.getTimestamp())%></td>
					</tr>
	<%
		} 
	} else {
	 %>
					<tr><td colspan="6">No Notifications</td></tr>
	<%
	} 
	 %>			
				</table>

			</div>

<%@include file="_footer.jsp" %>
