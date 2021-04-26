<%@page import="cc.openhome.model.*,java.util.*" %>
<%!
	private String USER_PATH;
	private UserService userService;
	
	public void jspInit() {
		USER_PATH = getInitParameter("USER_PATH");
		userService =
	            (UserService) getServletContext().getAttribute("userService");
	}

	private String getUsername(HttpServletRequest request) {
	    return request.getPathInfo().substring(1);
	}
%>
<%
	String username = getUsername(request);

	List<Message> messages = userService.messages(username);
	
	request.setAttribute("messages", messages);
	request.setAttribute("username", username);
	
	request.getRequestDispatcher(USER_PATH)
	       .forward(request, response);
%>