package cc.openhome.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cc.openhome.model.UserService;

@WebServlet(
    urlPatterns={"/reset_password"}, 
    initParams={
        @WebInitParam(name = "RESET_PW_PATH", value = "/WEB-INF/jsp/reset_password.jsp"),
        @WebInitParam(name = "SUCCESS_PATH", value = "/WEB-INF/jsp/reset_success.jsp")
    }
)
public class ResetPassword extends HttpServlet {
    private String RESET_PW_PATH;
    private String SUCCESS_PATH;
    private String LOGIN_PATH;
    
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        RESET_PW_PATH = getInitParameter("RESET_PW_PATH");
        SUCCESS_PATH = getInitParameter("SUCCESS_PATH");
        LOGIN_PATH = getServletContext().getContextPath();
        userService =
                (UserService) getServletContext().getAttribute("userService");
    }    
    
    private final Pattern passwdRegex = Pattern.compile("^\\w{8,16}$");
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var name = request.getParameter("name");
        var email = request.getParameter("email");
        var token = request.getParameter("token");
        
        var optionalAcct = userService.accountByNameEmail(name, email);
        
        if(optionalAcct.isPresent()) {
            var acct = optionalAcct.get();
            if(acct.getEncrypt().equals(token)) {
                request.setAttribute("acct", acct);
                request.getSession().setAttribute("token", token);
                request.getRequestDispatcher(RESET_PW_PATH)
                       .forward(request, response);
                return;
            }
        }
        
        response.sendRedirect(LOGIN_PATH);
        
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          var token = request.getParameter("token");
          var storedToken = (String) request.getSession().getAttribute("token");
          if(storedToken == null || !storedToken.equals(token)) {
              response.sendRedirect(LOGIN_PATH);
              return;
          }
          
          var name = request.getParameter("name");
          var email = request.getParameter("email");
          var password = request.getParameter("password");
          var password2 = request.getParameter("password2");

          if (!validatePassword(password, password2)) {
              var optionalAcct = userService.accountByNameEmail(name, email);
              request.setAttribute("errors", Arrays.asList("請確認密碼符合格式並再度確認密碼"));
              request.setAttribute("acct", optionalAcct.get());

              request.getRequestDispatcher(RESET_PW_PATH)
                     .forward(request, response);
          } else {
              userService.resetPassword(name, password);
              request.getRequestDispatcher(SUCCESS_PATH)
                     .forward(request, response);
          }
    }

    private boolean validatePassword(String password, String password2) {
        return password != null && 
               passwdRegex.matcher(password).find() && 
               password.equals(password2);
    }    
}
