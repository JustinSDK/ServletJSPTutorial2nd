package cc.openhome;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import javax.servlet.http.Part;
import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;

@MultipartConfig
@WebServlet(
    urlPatterns={"/mail"},
    initParams={
        @WebInitParam(name = "host", value = "smtp.gmail.com"),
        @WebInitParam(name = "port", value = "587"),
        @WebInitParam(name = "username", value = "yourname@gmail.com"),
        @WebInitParam(name = "password", value = "yourpassword")
    }
)
public class Mail extends HttpServlet {
    private final Pattern fileNameRegex =
            Pattern.compile("filename=\"(.*)\"");    
    
    private final String html = 
    "<html>" + 
    "    <head>" +
    "        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>" +
    "    </head>" +
    "    <body>" +
    "    <img" +
    "        style='border: 3px solid ; font-weight: bold;'" +
    "            src='cid:#filename'" +
    "            hspace='3' vspace='3'> <br>" +
    "        #text" +
    "    </body>" +
    "</html>";
    
    private String host;
    private String port;
    private String username;
    private String password;
    private Properties props;

    @Override
    public void init() throws ServletException {
        host = getServletConfig().getInitParameter("host");
        port = getServletConfig().getInitParameter("port");
        username = getServletConfig().getInitParameter("username");
        password = getServletConfig().getInitParameter("password");

        props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", port);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        var from = request.getParameter("from");
        var to = request.getParameter("to");
        var subject = request.getParameter("subject");
        var text = request.getParameter("text");
        var part = request.getPart("image");

        try {
            var message = createMessage(from, to, subject, text, part);
            Transport.send(message);
            response.getWriter().println("郵件傳送成功");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private Message createMessage(String from, String to, String subject, String text, Part part)
            throws MessagingException, AddressException, IOException {

        var session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        var message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setSentDate(new Date());
        message.setContent(multiPart(text, part));

        return message;
    }

    private Multipart multiPart(String text, Part part)
            throws MessagingException, UnsupportedEncodingException, IOException {
        var filename = getSubmittedFileName(part);
        
        var htmlPart = new MimeBodyPart();
        htmlPart.setContent(html.replace("#filename", filename).replace("#text", text), "text/html;charset=UTF-8");

        var filePart = new MimeBodyPart();
        filePart.setFileName(MimeUtility.encodeText(filename, "UTF-8", "B"));
        filePart.setHeader("Content-ID", "<" + filename + ">");        
        filePart.setDataHandler(
                new DataHandler(
                    new ByteArrayDataSource(part.getInputStream(), part.getContentType())    
                )
            );
        
        var multiPart = new MimeMultipart();
        multiPart.addBodyPart(htmlPart);
        multiPart.addBodyPart(filePart);
        return multiPart;
    }

    private String getSubmittedFileName(Part part) {
        var header = part.getHeader("Content-Disposition");
        var matcher = fileNameRegex.matcher(header);
        matcher.find();

        var filename = matcher.group(1);
        if (filename.contains("\\")) {
            return filename.substring(filename.lastIndexOf("\\") + 1);
        }
        return filename;
    }
}
