package cc.openhome.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import cc.openhome.model.UserService;

@WebListener
public class GossipInitializer implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        var context = sce.getServletContext();
        var USERS = context.getInitParameter("USERS");
        context.setAttribute("userService", new UserService(USERS));
    }
}
