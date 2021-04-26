<%@page import="java.util.*,cc.openhome.model.*,java.util.regex.*" %>
<%!
    private final Pattern emailRegex = Pattern.compile(
        "^[_a-z0-9-]+([.][_a-z0-9-]+)*@[a-z0-9-]+([.][a-z0-9-]+)*$");

    private final Pattern passwdRegex = Pattern.compile("^\\w{8,16}$");
    
    private final Pattern usernameRegex = Pattern.compile("^\\w{1,16}$");
    
    private boolean validateEmail(String email) {
        return email != null && emailRegex.matcher(email).find();
    }
    
    private boolean validateUsername(String username) {
        return username != null && usernameRegex.matcher(username).find();
    }

    private boolean validatePassword(String password, String password2) {
        return password != null && 
               passwdRegex.matcher(password).find() && 
               password.equals(password2);
    }    
    
    private String SUCCESS_PATH;
    private String FORM_PATH;
    private UserService userService;
    
	public void jspInit() {
    	SUCCESS_PATH = getInitParameter("SUCCESS_PATH");
    	FORM_PATH = getInitParameter("FORM_PATH");
    	userService =
                (UserService) getServletContext().getAttribute("userService");
	}    
%>    
<%
    if(request.getMethod().equals("GET")) {
        request.getRequestDispatcher(FORM_PATH).forward(request, response);
    } else {
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");

        List<String> errors = new ArrayList<>(); 
        if (!validateEmail(email)) {
            errors.add("未填寫郵件或格式不正確");
        }
        if(!validateUsername(username)) {
            errors.add("未填寫使用者名稱或格式不正確");
        }
        if (!validatePassword(password, password2)) {
            errors.add("請確認密碼符合格式並再度確認密碼");
        }
        
        String path;
        if(errors.isEmpty()) {
            path = SUCCESS_PATH;
            userService.tryCreateUser(email, username, password);
        } else {
            path = FORM_PATH;
            request.setAttribute("errors", errors);
        }

        request.getRequestDispatcher(path).forward(request, response);        
    }
%>