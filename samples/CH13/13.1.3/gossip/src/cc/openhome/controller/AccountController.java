package cc.openhome.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import cc.openhome.model.Account;
import cc.openhome.model.EmailService;
import cc.openhome.model.UserService;

@Controller
public class AccountController {
    @Value("${page.register_success}")
    private String REGISTER_SUCCESS_PAGE;
    
    @Value("${page.register_form}")
    private String REGISTER_FORM_PAGE;
    
    @Value("${page.verify}")
    private String VERIFY_PAGE;
    
    @Value("${page.forgot}")
    private String FORGOT_PAGE;
    
    @Value("${page.reset_password}")
    private String RESET_PW_PAGE;
    
    @Value("${page.reset_success}")
    private String RESET_SUCCESS_PAGE;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
  
    private final Pattern emailRegex = Pattern.compile(
            "^[_a-z0-9-]+([.][_a-z0-9-]+)*@[a-z0-9-]+([.][a-z0-9-]+)*$");

    private final Pattern passwdRegex = Pattern.compile("^\\w{8,16}$");
    
    private final Pattern usernameRegex = Pattern.compile("^\\w{1,16}$");

    @GetMapping("register")
    protected void registerForm(
            HttpServletRequest request, HttpServletResponse response)
                 throws ServletException, IOException {
        request.getRequestDispatcher(REGISTER_FORM_PAGE)
               .forward(request, response);
    }    
    
    @PostMapping("register")
    protected void register(
            HttpServletRequest request, HttpServletResponse response)
                 throws ServletException, IOException {
        var email = request.getParameter("email");
        var username = request.getParameter("username");
        var password = request.getParameter("password");
        var password2 = request.getParameter("password2");

        var errors = new ArrayList<String>(); 
        if (!validateEmail(email)) {
            errors.add("未填寫郵件或格式不正確");
        }
        if(!validateUsername(username)) {
            errors.add("未填寫使用者名稱或格式不正確");
        }
        if (!validatePassword(password, password2)) {
            errors.add("請確認密碼符合格式並再度確認密碼");
        }
        
        String path;
        if(errors.isEmpty()) {
            path = REGISTER_SUCCESS_PAGE;
            
            var optionalAcct =
                    userService.tryCreateUser(email, username, password);
            if(optionalAcct.isPresent()) {
                emailService.validationLink(optionalAcct.get());
            } else {
                emailService.failedRegistration(username, email);
            }
        } else {
            path = REGISTER_FORM_PAGE;
            request.setAttribute("errors", errors);
        }

        request.getRequestDispatcher(path).forward(request, response);
    }
    
    @GetMapping("verify")
    protected void verify(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        var email = request.getParameter("email");
        var token = request.getParameter("token");
        request.setAttribute("acct", userService.verify(email, token));
        request.getRequestDispatcher(VERIFY_PAGE).forward(request, response);
    }
    
    @PostMapping("forgot")
    protected void forgot(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
        var name = request.getParameter("name");
        var email = request.getParameter("email");
        
        var optionalAcct = userService.accountByNameEmail(name, email);
        
        if(optionalAcct.isPresent()) {
            emailService.passwordResetLink(optionalAcct.get());
        }
        
        request.setAttribute("email", email);
        request.getRequestDispatcher(FORGOT_PAGE)
               .forward(request, response);
    }

    @GetMapping("reset_password")
    protected void resetPasswordForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var name = request.getParameter("name");
        var email = request.getParameter("email");
        var token = request.getParameter("token");
        
        var optionalAcct = userService.accountByNameEmail(name, email);
        
        if(optionalAcct.isPresent()) {
            var acct = optionalAcct.get();
            if(acct.getEncrypt().equals(token)) {
                request.setAttribute("acct", acct);
                request.getSession().setAttribute("token", token);
                request.getRequestDispatcher(RESET_PW_PAGE)
                       .forward(request, response);
                return;
            }
        }
        
        response.sendRedirect(request.getServletContext().getContextPath());
    }

    @PostMapping("reset_password")
    protected void resetPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
          var token = request.getParameter("token");
          var storedToken = (String) request.getSession().getAttribute("token");
          if(storedToken == null || !storedToken.equals(token)) {
              response.sendRedirect(request.getServletContext().getContextPath());
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

              request.getRequestDispatcher(RESET_PW_PAGE)
                     .forward(request, response);
          } else {
              userService.resetPassword(name, password);
              request.getRequestDispatcher(RESET_SUCCESS_PAGE)
                     .forward(request, response);
          }
    }

    private boolean validateEmail(String email) {
        return email != null && emailRegex.matcher(email).find();
    }
    
    private boolean validateUsername(String username) {
        return username != null && usernameRegex.matcher(username).find();
    }

    private boolean validatePassword(String password, String password2) {
        return password != null && 
               passwdRegex.matcher(password).find() && 
               password.equals(password2);
    }
}
