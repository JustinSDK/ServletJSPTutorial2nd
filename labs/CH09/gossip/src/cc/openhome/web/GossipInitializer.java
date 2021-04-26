package cc.openhome.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import cc.openhome.model.AccountDAOFileImpl;
import cc.openhome.model.MessageDAOFileImpl;
import cc.openhome.model.UserService;

@WebListener
public class GossipInitializer implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        var context = sce.getServletContext();
        var USERS = context.getInitParameter("USERS");
        var acctDAO = new AccountDAOFileImpl(USERS);
        var messageDAO = new MessageDAOFileImpl(USERS);
        context.setAttribute("userService",
                       new UserService(acctDAO, messageDAO));
    }
}
