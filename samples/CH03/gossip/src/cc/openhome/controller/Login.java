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
    private final String USERS = "c:/workspace/Gossip/users";
    private final String SUCCESS_PATH = "member.html";
    private final String ERROR_PATH = "index.html";
    
    protected void doPost(
	        HttpServletRequest request, HttpServletResponse response) 
	                        throws ServletException, IOException {
        var username = request.getParameter("username");
        var password = request.getParameter("password");
	    

        response.sendRedirect(
            login(username, password) ? SUCCESS_PATH : ERROR_PATH);
    }

    private boolean login(String username, String password) 
                          throws IOException {
        
        if(username != null && username.trim().length() != 0 && 
                password != null) {
            var userhome = Paths.get(USERS, username);
            return Files.exists(userhome) && 
                    isCorrectPassword(password, userhome);
        }
        return false;
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
