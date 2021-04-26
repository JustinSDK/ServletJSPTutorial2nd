package cc.openhome.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.openhome.model.UserService;

@WebServlet("")
public class Index extends HttpServlet {
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService =
                (UserService) getServletContext().getAttribute("userService");
    }
    
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {        
        processRequest(request, response);
    }
    
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {        
        processRequest(request, response);
    }
    
    protected void processRequest(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {        
        var newest = userService.newestMessages(10);
        request.setAttribute("newest", newest);
        
        request.getRequestDispatcher("/WEB-INF/jsp/index.jsp")
               .forward(request, response);
    }
}
