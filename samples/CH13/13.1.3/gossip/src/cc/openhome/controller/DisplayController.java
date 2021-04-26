package cc.openhome.controller;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cc.openhome.model.UserService;

@Controller
public class DisplayController {
    @Value("${page.index}")
    private String INDEX_PAGE;
    
    @Value("${page.user}")
    private String USER_PAGE;
    
    @Autowired
    private UserService userService;
    
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    protected void index(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {        
        var newest = userService.newestMessages(10);
        request.setAttribute("newest", newest);
        
        request.getRequestDispatcher(INDEX_PAGE)
               .forward(request, response);
    }
    
    @GetMapping("user/*")
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
        var username = getUsername(request);
        var userExisted = userService.exist(username) ;
        
        var messages = userExisted ? 
                           userService.messages(username) : 
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
}