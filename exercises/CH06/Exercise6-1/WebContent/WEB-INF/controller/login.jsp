<%@page import="cc.openhome.model.*,java.util.*" %>
<%!

	private String SUCCESS_PATH;
	private String LOGIN_PATH;
	private UserService userService;
	
	public void jspInit() {
		SUCCESS_PATH = getInitParameter("SUCCESS_PATH");
		LOGIN_PATH = getInitParameter("LOGIN_PATH");
		userService =
	            (UserService) getServletContext().getAttribute("userService");
	}
%>
<%
	String username = request.getParameter("username");
	String password = request.getParameter("password");
	
	if(userService.login(username, password)) {
	    if(request.getSession(false) != null) {
	        request.changeSessionId();
	    }
	    request.getSession().setAttribute("login", username);
	    response.sendRedirect(SUCCESS_PATH);
	} else {
	    request.setAttribute("errors", Arrays.asList("登入失敗"));
	    request.getRequestDispatcher(LOGIN_PATH)
	           .forward(request, response);
	}
%>