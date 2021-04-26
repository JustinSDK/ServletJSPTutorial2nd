package cc.openhome;

import java.util.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class Login extends HttpServlet {
    private Map<String, String> users = new HashMap<>() {{
        put("caterpillar", "12345678");
        put("momor", "87654321");
        put("irene", "13577531");
    }};
    
    @Override
    protected void doPost(HttpServletRequest request, 
                          HttpServletResponse response)
                             throws ServletException, IOException {
       var name = request.getParameter("name");
       var passwd = request.getParameter("passwd");

       var page = "form.html";
       if(users.containsKey(name) && users.get(name).equals(passwd)) {
           request.getSession().setAttribute("user", 
                   new User(name, request.getRemoteAddr(), request.getHeader("user-agent")));
           page = "welcome.view";
       }
       response.sendRedirect(page);
    } 
}
