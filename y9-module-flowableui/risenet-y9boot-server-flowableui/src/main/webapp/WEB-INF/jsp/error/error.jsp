<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" %>
<%@page import="net.risesoft.y9.Y9Context" %>
<%@ include file="/static/common/taglib.jsp" %>
<%
    String rc7LogoutUrl = Y9Context.getLogoutUrl();
%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <%@ include file="/static/common/head.jsp" %>
    <title>${message}</title>
    <script type="text/javascript">
        var time = 6;

        function returnUrlByTime() {
            window.setTimeout('returnUrlByTime()', 1000);
            time = time - 1;
            document.getElementById("layer").innerHTML = time;
        }

        function logout() {
            location.href = rc7LogoutUrl;
        }
    </script>
    <style type="text/css">
        .STYLE1 {
            color: #FF0000
        }
    </style>
</head>

<body onload="returnUrlByTime()">
<br/>
<br/>
<br/>
<center>
    ${message}</br>
    <a href="javascript:logout();">请点此重新登录!!!!</a></center>
<%-- <%
    //转向语句
    response.setHeader("Refresh", "5;URL="+rc7LogoutUrl);
%>   --%>
</body>
</html>