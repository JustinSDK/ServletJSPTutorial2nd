package cc.openhome.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String index(Model model) {        
        var newest = userService.newestMessages(10);
        model.addAttribute("newest", newest);
        return INDEX_PAGE;
    }
    
    @GetMapping("user/{username}")
    public String doGet(
            @PathVariable("username") String username,
            Model model) {
        var userExisted = userService.exist(username) ;
        
        var messages = userExisted ? 
                           userService.messages(username) : 
                           Collections.emptyList();
        
        model.addAttribute("userExisted", userExisted);
        model.addAttribute("messages", messages);
        model.addAttribute("username", username);
        
        return USER_PAGE;
    }
}