package cc.openhome.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    @GetMapping("register")
    public String registerForm() {
        return REGISTER_FORM_PAGE;
    }    
    
    @PostMapping("register")
    public String register(
            @Valid RegisterForm form,
            BindingResult bindingResult,
            Model model) {

        var errors = toList(bindingResult);
        
        String path;
        if(errors.isEmpty()) {
            path = REGISTER_SUCCESS_PAGE;
            
            var optionalAcct =
                    userService.tryCreateUser(form.getEmail(), form.getUsername(), form.getPassword());
            if(optionalAcct.isPresent()) {
                emailService.validationLink(optionalAcct.get());
            } else {
                emailService.failedRegistration(form.getUsername(), form.getEmail());
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
            @Valid ResetPasswordForm form,
            BindingResult bindingResult,
            @SessionAttribute("token") String storedToken,
            Model model) {
          if(storedToken == null || !storedToken.equals(form.getToken())) {
              return REDIRECT_INDEX_PATH;
          }
          
          var errors = toList(bindingResult);

          if (!errors.isEmpty()) {
              var optionalAcct = userService.accountByNameEmail(form.getName(), form.getEmail());
              model.addAttribute("errors", errors);
              model.addAttribute("acct", optionalAcct.get());
              return RESET_PW_PAGE;
          } else {
              userService.resetPassword(form.getName(), form.getPassword());
              return RESET_SUCCESS_PAGE;
          }
    }
    
    private List<String> toList(BindingResult bindingResult) {
        var errors = new ArrayList<String>(); 
        if(bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(err -> {
                errors.add(err.getDefaultMessage());
            });
        }
        return errors;
    }     
}
