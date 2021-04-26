package cc.openhome.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
    urlPatterns={"/logout"}, 
    initParams={
        @WebInitParam(name = "LOGIN_PATH", value = "index.html")
    }
)
public class Logout extends HttpServlet {
    private String LOGIN_PATH;
    
    @Override
    public void init() throws ServletException {
        LOGIN_PATH = getInitParameter("LOGIN_PATH");
    }

	protected void doGet(
	        HttpServletRequest request, HttpServletResponse response) 
	                      throws ServletException, IOException {
        if(request.getSession().getAttribute("login") != null) {
            request.getSession().invalidate(); 
        }
        response.sendRedirect(LOGIN_PATH);
    }
}
