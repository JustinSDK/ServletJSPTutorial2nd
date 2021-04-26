package cc.openhome.web;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import cc.openhome.model.AccountDAOJdbcImpl;
import cc.openhome.model.GmailService;
import cc.openhome.model.MessageDAOJdbcImpl;
import cc.openhome.model.UserService;

@WebListener
public class GossipInitializer implements ServletContextListener {

    private DataSource dataSource() {
        try {
            var initContext = new InitialContext();
            var envContext = (Context) initContext.lookup("java:/comp/env");
            return (DataSource) envContext.lookup("jdbc/gossip");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void contextInitialized(ServletContextEvent sce) {
        var dataSource = dataSource();
        var context = sce.getServletContext();
        var acctDAO = new AccountDAOJdbcImpl(dataSource);
        var messageDAO = new MessageDAOJdbcImpl(dataSource);
        context.setAttribute("userService",
                       new UserService(acctDAO, messageDAO));
        
        context.setAttribute("emailService", 
                new GmailService(
                    context.getInitParameter("MAIL_USER"), 
                    context.getInitParameter("MAIL_PASSWORD")
               )
        );
    }
}
