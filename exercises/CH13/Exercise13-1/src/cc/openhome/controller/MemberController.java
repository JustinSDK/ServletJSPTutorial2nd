package cc.openhome.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cc.openhome.model.UserService;

@Controller
public class MemberController {
    @Value("${page.member}")
    private String MEMBER_PAGE;
    
    @Value("#{'redirect:' + '${path.redirect.member}'}")
    private String REDIRECT_MEMBER_PATH;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("member")
    @PostMapping("member")
    public String member(
            Principal principal, 
            Model model) {
        model.addAttribute("messages", userService.messages(principal.getName()));
        return MEMBER_PAGE;
    }
    
    @PostMapping("new_message")
    public String newMessage(
            @RequestParam String blabla,
            Principal principal) {
        
        if(blabla == null || blabla.length() == 0) {
            return REDIRECT_MEMBER_PATH;
        }        
       
        if(blabla.length() <= 140) {
            userService.addMessage(principal.getName(), blabla);
            return REDIRECT_MEMBER_PATH;
        }
        else {
            return MEMBER_PAGE;
        }
    }    
    
    @PostMapping("del_message")
    public String delMessage(
            @RequestParam String millis,
            Principal principal) {

        if(millis != null) {
            userService.deleteMessage(principal.getName(), millis);
        }
        
        return REDIRECT_MEMBER_PATH;
    }    
}