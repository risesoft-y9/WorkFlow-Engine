<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglib.jsp"%>
<%@ include file="/static/common/head.jsp"%>

<!DOCTYPE html>
<html>
<head>
	<title>在线预览</title>
	<script type="text/javascript" charset="UTF-8" src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
</head>
<body>
<%--  <iframe id="content" src="${ctx}/static/previewFile/${fileName}" style="width:98%;height: 98%;"  frameborder="0"></iframe> --%>
<script language="javascript" type="text/javascript">
     window.location.href="${ctx}/static/previewFile/${fileName}"; 
</script>

</body>

</html>
