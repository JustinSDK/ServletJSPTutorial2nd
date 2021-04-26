package cc.openhome.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.openhome.model.UserService;

@WebServlet(
    urlPatterns={"/del_message"}, 
    initParams={
        @WebInitParam(name = "MEMBER_PATH", value = "member")
    }
)
@ServletSecurity(
    @HttpConstraint(rolesAllowed = {"member"})
)
public class DelMessage extends HttpServlet {
    private String MEMBER_PATH;
    
    private UserService userService;
    
    @Override
	public void init() throws ServletException {
    	MEMBER_PATH = getInitParameter("MEMBER_PATH");
    	userService =
                (UserService) getServletContext().getAttribute("userService");
	}
    
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        var millis = request.getParameter("millis");
        if(millis != null) {
            userService.deleteMessage(getUsername(request), millis);
        }
        
        response.sendRedirect(MEMBER_PATH);
    }
    
    private String getUsername(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("login");
    }    
}
