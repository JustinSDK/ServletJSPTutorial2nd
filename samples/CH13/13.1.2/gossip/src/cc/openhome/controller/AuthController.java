package cc.openhome.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import cc.openhome.model.UserService;

@Controller
public class AuthController {
    @Value("member")
    private String REDIRECT_MEMBER_PATH;
    
    @Value("/")
    private String FORWARD_LOGIN_PATH;    

    @PostMapping("login")
    protected void login(
            HttpServletRequest request, HttpServletResponse response) 
                            throws ServletException, IOException {
        var username = request.getParameter("username");
        var password = request.getParameter("password");  
        
        if(isInputted(username, password) && login(request, username, password)) {
            request.getSession().setAttribute("login", username);
            response.sendRedirect(REDIRECT_MEMBER_PATH);
        } else {
            request.setAttribute("errors", Arrays.asList("登入失敗"));
            request.getRequestDispatcher(FORWARD_LOGIN_PATH)
                   .forward(request, response);
        }
    }
    
    @GetMapping("logout")
    protected void logout(
            HttpServletRequest request, HttpServletResponse response) 
                          throws ServletException, IOException {
        request.logout();
        response.sendRedirect(request.getServletContext().getContextPath());
    }    
    
    private boolean login(HttpServletRequest request, String username, String password) {
        var optionalPasswd =
                userService(request).encryptedPassword(username, password);   
        try {
            request.login(username, optionalPasswd.get());
            return true; 
        } catch(NoSuchElementException | ServletException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean isInputted(String username, String password) {
        return username != null && password != null &&
                username.trim().length() != 0 && password.trim().length() != 0;
    }
    
    private UserService userService(HttpServletRequest request) {
        return (UserService) request.getServletContext().getAttribute("userService");
    }    
}
