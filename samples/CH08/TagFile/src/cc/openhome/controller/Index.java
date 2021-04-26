package cc.openhome.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        urlPatterns={""}, 
        initParams={
            @WebInitParam(name = "INDEX_PATH", value = "/WEB-INF/jsp/index.jsp")
        }
    )
public class Index extends HttpServlet {
    private String INDEX_PATH;
    
    @Override
    public void init() throws ServletException {
        INDEX_PATH = getInitParameter("INDEX_PATH");
    }
    
    protected void doGet(
            HttpServletRequest request, HttpServletResponse response) 
                    throws ServletException, IOException {        
        request.getRequestDispatcher(INDEX_PATH)
               .forward(request, response);
    }
}
