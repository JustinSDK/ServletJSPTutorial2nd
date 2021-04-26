package cc.openhome.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.openhome.model.Message;
import cc.openhome.model.UserService;

@WebServlet(
    urlPatterns={"/user/*"}, 
    initParams={
        @WebInitParam(name = "USER_PATH", value = "/WEB-INF/jsp/user.jsp")
    }
)
public class User extends HttpServlet {
	private String USER_PATH;
    private UserService userService;
    
    @Override
	public void init() throws ServletException {
		USER_PATH = getInitParameter("USER_PATH");
		userService =
                (UserService) getServletContext().getAttribute("userService");
	}

	protected void doGet(
                HttpServletRequest request, HttpServletResponse response) 
                        throws ServletException, IOException {

        var username = getUsername(request);
        var messages = userService.messages(username);
        
        request.setAttribute("messages", messages);
        request.setAttribute("username", username);
        
        request.getRequestDispatcher(USER_PATH)
               .forward(request, response);
    }

    private String getUsername(HttpServletRequest request) {
        return request.getPathInfo().substring(1);
    }
}
