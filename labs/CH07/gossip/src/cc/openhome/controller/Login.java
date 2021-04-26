package cc.openhome.controller;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.openhome.model.UserService;

@WebServlet(
    urlPatterns={"/login"}, 
    initParams={
        @WebInitParam(name = "SUCCESS_PATH", value = "member"),
        @WebInitParam(name = "LOGIN_PATH", value = "/WEB-INF/jsp/index.jsp")
    }
)
public class Login extends HttpServlet {
    private String SUCCESS_PATH;
    private String LOGIN_PATH;
    private UserService userService;
    
    @Override
	public void init() throws ServletException {
    	SUCCESS_PATH = getInitParameter("SUCCESS_PATH");
    	LOGIN_PATH = getInitParameter("LOGIN_PATH");
    	userService =
                (UserService) getServletContext().getAttribute("userService");
	}
    
    protected void doPost(
	        HttpServletRequest request, HttpServletResponse response) 
	                        throws ServletException, IOException {
        var username = request.getParameter("username");
        var password = request.getParameter("password");
	    
	    if(isInputted(username, password) && userService.login(username, password)) {
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
    }
	
    private boolean isInputted(String username, String password) {
        return username != null && password != null &&
                username.trim().length() != 0 && password.trim().length() != 0;
    }
}
