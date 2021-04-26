package cc.openhome.controller;

import java.util.Arrays;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import cc.openhome.model.UserService;

@Controller
@SessionAttributes("login")
public class AuthController {
    @Value("#{'redirect:' + '${path.redirect.member}'}")
    private String REDIRECT_MEMBER_PATH;
    
    @Value("#{'redirect:' + '${path.redirect.index}'}")
    private String REDIRECT_INDEX_PATH;
    
    @Autowired
    private UserService userService;

    @PostMapping("login")
    public String login(
            HttpServletRequest request,
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttrs) {
        
        if(isInputted(username, password) && login(request, username, password)) {
            model.addAttribute("login", username);
            return REDIRECT_MEMBER_PATH;
        } else {
            redirectAttrs.addFlashAttribute("errors", Arrays.asList("登入失敗"));
            return REDIRECT_INDEX_PATH;
        }
    }
    
    @GetMapping("logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return REDIRECT_INDEX_PATH;
    }    
    
    private boolean login(HttpServletRequest request, String username, String password) {
        var optionalPasswd =
                userService.encryptedPassword(username, password);   
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
}
