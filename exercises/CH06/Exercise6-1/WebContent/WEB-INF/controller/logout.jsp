<%!
	private String LOGIN_PATH;
	
	public void jspInit() {
	    LOGIN_PATH = getInitParameter("LOGIN_PATH");
	}
%>
<%
	if(request.getSession().getAttribute("login") != null) {
	    request.getSession().invalidate(); 
	}
	response.sendRedirect(LOGIN_PATH);
%>