<%@page import="cc.openhome.model.*,java.util.*" %>
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
    List<Message> messages = userService.messages(getUsername(request));
	
	request.setAttribute("messages", messages);
	request.getRequestDispatcher(MEMBER_PATH).forward(request, response);
%>