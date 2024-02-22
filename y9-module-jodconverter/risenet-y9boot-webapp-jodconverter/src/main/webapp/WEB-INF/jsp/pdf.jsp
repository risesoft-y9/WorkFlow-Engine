<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglib.jsp"%>
<%@ include file="/static/common/head.jsp"%>

<!DOCTYPE html>
<html>
<head>
	<title>在线预览</title>
	<script type="text/javascript" charset="UTF-8" src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
    <style type="text/css">
        body{
            margin: 0;
            padding:0;
            border:0;
        }
    </style>
</head>
<body>
    <iframe id="content" src="" width="100%" height="100%" frameborder="0"></iframe>
</body>
<script type="text/javascript">
	var fileName = "${fileName}";
	$("#content").attr("src","${ctx}/static/pdfjs/web/viewer.html?file=${ctx}/static/previewFile/"+encodeURIComponent(fileName));
    //document.getElementsByTagName('iframe')[0].height = document.documentElement.clientHeight-10;
    /**
     * 页面变化调整高度
     */
    //window.onresize = function(){
        //var fm = document.getElementsByTagName("iframe")[0];
       //fm.height = window.document.documentElement.clientHeight-10;
    //}
    
    function isIE() { //ie?
		var ua = navigator.userAgent.toLocaleLowerCase();
        if (ua.match(/msie/) != null || ua.match(/trident/) != null) {
			return true;
		}
		return false;
	}
</script>
</html>