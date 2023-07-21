<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglib.jsp"%>
<%@ include file="/static/common/head.jsp"%>

<!DOCTYPE html>
<html>
<head>
    <title>图片预览</title>
    <link rel="stylesheet" href="${ctx}/static/css/viewer.min.css">
    <style>
        * { margin: 0; padding: 0;}
        #dowebok { width: 800px; margin: 0 auto; font-size: 0;}
        #dowebok li {  display: inline-block;width: 50px;height: 50px; margin-left: 1%; padding-top: 1%;}
        /*#dowebok li img { width: 200%;}*/
    </style>
    <script type="text/javascript" charset="UTF-8" src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
</head>
<body>
	<ul id="dowebok">
	       <c:forEach items="${imgurls}" var="img">
	       	<li><img id="${img}"  url="${img}" src="${img}" width="1px" height="1px"></li>
	       </c:forEach>
	</ul>
	<script src="${ctx}/static/js/viewer.min.js"></script>
	<script>
	    var viewer = new Viewer(document.getElementById('dowebok'), {
	        url: 'src',
	       navbar:false
	    });
	    document.getElementById("${currentUrl}").click();
	</script>
</body>

</html>
