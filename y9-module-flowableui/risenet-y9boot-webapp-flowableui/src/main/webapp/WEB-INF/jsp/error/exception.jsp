<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/static/common/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/static/common/head.jsp" %>
    <title>错误页面</title>
</head>
<body>
<div style="text-align: center;">
    <div style="font-size: 16px"><font color="red">系统内部异常！</font></div>
    <a href="javascript:" onclick="openexception()" style="font-size: 12px">查看详细信息</a>
</div>
<table width="100%" border="1" style="display: none" id="erro">
    <tr>
        <td width="5%">类名&nbsp;</td>
        <td>${error.exception}</td>
    </tr>
    <tr>
        <td width="5%">错误信息&nbsp;</td>
        <td>${error.message}</td>
    </tr>
    <tr>
        <td width="5%">详细信息&nbsp;</td>
        <td>${error.stackTrace}</td>
    </tr>
</table>
<script type="text/javascript">
    function openexception() {
        if (document.getElementById('erro').style.display != "block") {
            document.getElementById('erro').style.display = "block";
        } else {
            document.getElementById('erro').style.display = "none";
        }
    }
</script>
</body>
</html>
