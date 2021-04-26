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
import cc.openhome.model.EmailService;
import cc.openhome.model.UserService;

@WebServlet(
    urlPatterns={"/forgot"}, 
    initParams={
        @WebInitParam(name = "FORGOT_PATH", value = "/WEB-INF/jsp/forgot.jsp")
    }
)
public class Forgot extends HttpServlet {
    private String FORGOT_PATH;
    private UserService userService;
    private EmailService emailService;
    
    @Override
    public void init() throws ServletException {
        FORGOT_PATH = getInitParameter("FORGOT_PATH");
        userService =
                (UserService) getServletContext().getAttribute("userService");
        emailService =
                (EmailService) getServletContext().getAttribute("emailService");
    }
    
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
        var name = request.getParameter("name");
        var email = request.getParameter("email");
        
        var optionalAcct = userService.accountByNameEmail(name, email);
        
        if(optionalAcct.isPresent()) {
            emailService.passwordResetLink(optionalAcct.get());
        }
        
        request.setAttribute("email", email);
        request.getRequestDispatcher(FORGOT_PATH)
               .forward(request, response);
    }
}
