package cc.openhome.controller;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/member")
public class Member extends HttpServlet {
    private final String USERS = "c:/workspace/gossip/users";
    private final String MEMBER_PATH = "member.view";
    private final String LOGIN_PATH = "index.html";
    
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    protected void doPost(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {
        processRequest(request, response);
    }
   
    protected void processRequest(
                HttpServletRequest request, HttpServletResponse response) 
                        throws ServletException, IOException {
        if(request.getSession().getAttribute("login") == null) {
            response.sendRedirect(LOGIN_PATH);
            return;
        }
        
        request.setAttribute("messages", messages(getUsername(request)));
        request.getRequestDispatcher(MEMBER_PATH).forward(request, response);
    }

    private String getUsername(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("login");
    }

    private Map<Long, String> messages(String username) throws IOException {
        var userhome = Paths.get(USERS, username);
        var messages = new TreeMap<Long, String>(Comparator.reverseOrder());
        try(var txts = Files.newDirectoryStream(userhome, "*.txt")) {
            for(var txt : txts) {
                var millis = txt.getFileName().toString().replace(".txt", "");
                var blabla = Files.readAllLines(txt).stream()
                          .collect(
                              Collectors.joining(System.lineSeparator())
                          );
                messages.put(Long.parseLong(millis), blabla);
            }
        }

        return messages;
    }
}
