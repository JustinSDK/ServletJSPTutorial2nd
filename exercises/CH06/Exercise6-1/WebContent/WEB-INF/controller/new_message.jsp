<%@page import="cc.openhome.model.*" %>
<%!
	private String MEMBER_PATH;
	
	private UserService userService;
	
	public void jspInit() {
		MEMBER_PATH = getInitParameter("MEMBER_PATH");
		userService =
	            (UserService) getServletContext().getAttribute("userService");
	}

	private String getUsername(HttpServletRequest request) {
	    return (String) request.getSession().getAttribute("login");
	}
%>
<%
	request.setCharacterEncoding("UTF-8");
	String blabla = request.getParameter("blabla");
	
	if(blabla == null || blabla.length() == 0) {
	    response.sendRedirect(MEMBER_PATH);
	    return;
	}        
	
	if(blabla.length() <= 140) {
	    userService.addMessage(getUsername(request), blabla);
	    response.sendRedirect(MEMBER_PATH);
	}
	else {
	    request.getRequestDispatcher(MEMBER_PATH).forward(request, response);
	}
%>