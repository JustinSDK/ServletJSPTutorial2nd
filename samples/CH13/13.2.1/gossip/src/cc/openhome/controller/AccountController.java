package cc.openhome.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import cc.openhome.model.EmailService;
import cc.openhome.model.UserService;

@Controller
@SessionAttributes("token")
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

    @Value("#{'redirect:' + '${path.redirect.index}'}")
    private String REDIRECT_INDEX_PATH;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
  
    private final Pattern emailRegex = Pattern.compile(
            "^[_a-z0-9-]+([.][_a-z0-9-]+)*@[a-z0-9-]+([.][a-z0-9-]+)*$");

    private final Pattern passwdRegex = Pattern.compile("^\\w{8,16}$");
    
    private final Pattern usernameRegex = Pattern.compile("^\\w{1,16}$");

    @GetMapping("register")
    public String registerForm() {
        return REGISTER_FORM_PAGE;
    }    
    
    @PostMapping("register")
    public String register(
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,  
            @RequestParam String password2,
            Model model) {

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
            model.addAttribute("errors", errors);
        }

        return path;
    }
    
    @GetMapping("verify")
    public String verify(
            @RequestParam String email, 
            @RequestParam String token,
            Model model) {
        model.addAttribute("acct", userService.verify(email, token));
        return VERIFY_PAGE;
    }
    
    @PostMapping("forgot")
    public String forgot(
            @RequestParam String name,
            @RequestParam String email,
            Model model) {
        var optionalAcct = userService.accountByNameEmail(name, email);
        
        if(optionalAcct.isPresent()) {
            emailService.passwordResetLink(optionalAcct.get());
        }
        
        model.addAttribute("email", email);
        return FORGOT_PAGE;
    }

    @GetMapping("reset_password")
    public String resetPasswordForm(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String token,
            Model model) {
        
        var optionalAcct = userService.accountByNameEmail(name, email);
        
        if(optionalAcct.isPresent()) {
            var acct = optionalAcct.get();
            if(acct.getEncrypt().equals(token)) {
                model.addAttribute("acct", acct);
                model.addAttribute("token", token);
                return RESET_PW_PAGE;
            }
        }
        
        return REDIRECT_INDEX_PATH;
    }

    @PostMapping("reset_password")
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String password2,
            @SessionAttribute("token") String storedToken,
            Model model) {
          if(storedToken == null || !storedToken.equals(token)) {
              return REDIRECT_INDEX_PATH;
          }

          if (!validatePassword(password, password2)) {
              var optionalAcct = userService.accountByNameEmail(name, email);
              model.addAttribute("errors", Arrays.asList("請確認密碼符合格式並再度確認密碼"));
              model.addAttribute("acct", optionalAcct.get());

              return RESET_PW_PAGE;
          } else {
              userService.resetPassword(name, password);
              return RESET_SUCCESS_PAGE;
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
