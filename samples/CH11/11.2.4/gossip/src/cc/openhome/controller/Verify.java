package cc.openhome.controller;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.openhome.model.Account;
import cc.openhome.model.UserService;

@WebServlet(
    urlPatterns={"/verify"}, 
    initParams={
        @WebInitParam(name = "VERIFY_PATH", value = "/WEB-INF/jsp/verify.jsp")
    }
)
public class Verify extends HttpServlet {
    private String VERIFY_PATH;
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        VERIFY_PATH = getInitParameter("VERIFY_PATH");
        userService =
             (UserService) getServletContext().getAttribute("userService");
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
        var email = request.getParameter("email");
        var token = request.getParameter("token");
	    request.setAttribute("acct", userService.verify(email, token));
	    request.getRequestDispatcher(VERIFY_PATH).forward(request, response);
	}
}
