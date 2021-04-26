package cc.openhome.controller;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cc.openhome.model.UserService;

@Controller
public class DisplayController {
    @Value("/WEB-INF/jsp/index.jsp")
    private String INDEX_PAGE;
    
    @Value("/WEB-INF/jsp/user.jsp")
    private String USER_PAGE;
    
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    protected void index(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {        
        var newest = userService(request).newestMessages(10);
        request.setAttribute("newest", newest);
        
        request.getRequestDispatcher(INDEX_PAGE)
               .forward(request, response);
    }
    
    @GetMapping("user/*")
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
        var username = getUsername(request);
        var userExisted = userService(request).exist(username) ;
        
        var messages = userExisted ? 
                           userService(request).messages(username) : 
                           Collections.emptyList();
        
        request.setAttribute("userExisted", userExisted);
        request.setAttribute("messages", messages);
        request.setAttribute("username", username);
        
        request.getRequestDispatcher(USER_PAGE)
               .forward(request, response);
    }
    
    private String getUsername(HttpServletRequest request) {
        return request.getRequestURI().replace("/gossip/user/", "");
    }
    
    private UserService userService(HttpServletRequest request) {
        return (UserService) request.getServletContext().getAttribute("userService");
    }    
}