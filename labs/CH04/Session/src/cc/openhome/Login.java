package cc.openhome;

import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("/login")
public class Login extends HttpServlet {
    @Override
    protected void doPost(
          HttpServletRequest request, HttpServletResponse response)
                      throws ServletException, IOException {
        String name = request.getParameter("name");
        String passwd = request.getParameter("passwd");
        
        String page;
        if("caterpillar".equals(name) && "12345678".equals(passwd)) {
            processCookie(request, response);
            page = "user";
        }
        else {
            page = "login.html";
        }
        response.sendRedirect(page);
    }
    
    private static final int ONE_WEEK = 7 * 24 * 60 * 60; 

    private void processCookie(
              HttpServletRequest request, HttpServletResponse response) {
        // 建立並加入 Cookie
    }
}
