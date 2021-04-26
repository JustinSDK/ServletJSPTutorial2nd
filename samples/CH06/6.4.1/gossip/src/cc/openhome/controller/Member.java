package cc.openhome.controller;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.openhome.model.UserService;

@WebServlet(
    urlPatterns={"/member"}, 
    initParams={
        @WebInitParam(name = "MEMBER_PATH", value = "/WEB-INF/jsp/member.jsp")
    }
)
public class Member extends HttpServlet {
    private String MEMBER_PATH;
    
    private UserService userService;
    
    @Override
	public void init() throws ServletException {
    	MEMBER_PATH = getInitParameter("MEMBER_PATH");
    	userService =
                (UserService) getServletContext().getAttribute("userService");
	}
    
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
        processRequest(request, response);
    }
   
    protected void processRequest(
                HttpServletRequest request, HttpServletResponse response) 
                        throws ServletException, IOException {
        request.setAttribute("messages", userService.messages(getUsername(request)));
        request.getRequestDispatcher(MEMBER_PATH).forward(request, response);
    }

    private String getUsername(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("login");
    }
}
