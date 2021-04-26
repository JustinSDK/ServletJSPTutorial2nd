<%@page import="cc.openhome.model.*" %>
<%!
	private String MEMBER_PATH;
	
	private UserService userService;
	
	public void jspInit()  {
		MEMBER_PATH = getInitParameter("MEMBER_PATH");
		userService =
	            (UserService) getServletContext().getAttribute("userService");
	}
	private String getUsername(HttpServletRequest request) {
	    return (String) request.getSession().getAttribute("login");
	}
%>
<%
	String millis = request.getParameter("millis");
	
	if(millis != null) {
	    userService.deleteMessage(getUsername(request), millis);
	}
	
	response.sendRedirect(MEMBER_PATH);
%>