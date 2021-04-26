package cc.openhome.controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class Login extends HttpServlet {
    private final String USERS = "c:/workspace/gossip/users";
    private final String SUCCESS_PATH = "member";
    private final String ERROR_PATH = "index.html";
    
    protected void doPost(
	        HttpServletRequest request, HttpServletResponse response) 
	                        throws ServletException, IOException {
        var username = request.getParameter("username");
        var password = request.getParameter("password");
	    

	    String page;
	    if(isInputted(username, password) &&login(username, password)) {
	        if(request.getSession(false) != null) {
	            request.changeSessionId();
	        }
	        request.getSession().setAttribute("login", username);
	        page = SUCCESS_PATH;
	    } else {
	        page = ERROR_PATH;
	    }
	    
	    response.sendRedirect(page);
    }

    private boolean isInputted(String username, String password) {
        return username != null && password != null &&
                username.trim().length() != 0 && password.trim().length() != 0;
    }
	
    private boolean login(String username, String password) 
                          throws IOException {
		var userhome = Paths.get(USERS, username);
		return Files.exists(userhome) && 
				isCorrectPassword(password, userhome);
    }

    private boolean isCorrectPassword(
            String password, Path userhome) throws IOException {
        var profile = userhome.resolve("profile");
        try(var reader = Files.newBufferedReader(profile)) {
            var data = reader.readLine().split("\t");
            var encrypt = Integer.parseInt(data[1]);
            var salt = Integer.parseInt(data[2]);
            return password.hashCode() + salt == encrypt;
        }
    } 
}
