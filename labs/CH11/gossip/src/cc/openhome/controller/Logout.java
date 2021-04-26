package cc.openhome.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/logout")
@ServletSecurity(
    @HttpConstraint(rolesAllowed = {"member"})
)
public class Logout extends HttpServlet {
    private String LOGIN_PATH;
    
    @Override
    public void init() throws ServletException {
        LOGIN_PATH = getServletContext().getContextPath();
    }

	protected void doGet(
	        HttpServletRequest request, HttpServletResponse response) 
	                      throws ServletException, IOException {
	    request.logout();
        response.sendRedirect(LOGIN_PATH);
    }
}
