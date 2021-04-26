<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset='UTF-8'>
<title>Gossip 微網誌</title>
<link rel='stylesheet' href='../css/member.css' type='text/css'>
</head>
<body>

    <div class='leftPanel'>
        <img src='../images/caterpillar.jpg' alt='Gossip 微網誌' />
    </div>

    <c:choose>
        <c:when test="${!requestScope.userExisted}">
            <p>${requestScope.username} 不存在</p>
        </c:when>    
        <c:when test="${empty requestScope.messages}">
            <p>${requestScope.username} 尚未發表訊息</p>
        </c:when>
        <c:otherwise>
            ${requestScope.username} 的微網誌
            <table border='0' cellpadding='2' cellspacing='2'>
            <thead> 
            <tr><th><hr></th></tr>
            </thead>  
            <tbody>     
			          
            <c:forEach var="message" items="${requestScope.messages}">
                <tr>
                <td style='vertical-align: top;'>${message.username}<br>
                    ${message.blabla}<br> ${message.localDateTime}
		        <hr>
                </td>
                </tr>   
            </c:forEach>
	            
	        </tbody>
	        </table>
        </c:otherwise>
    </c:choose>

</body>
</html>
