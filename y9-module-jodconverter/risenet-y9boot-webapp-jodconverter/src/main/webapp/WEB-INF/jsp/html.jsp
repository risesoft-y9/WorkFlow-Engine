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
	<!-- <font id="msg" style="dispaly:none;  margin-left: 47%;position: absolute;top: 2%;" color="red">文件转换失败!</font> -->
    <iframe src="${ctx}/static/previewFile/${fileName}" width="100%" height="100%" frameborder="0"></iframe>
</body>
<script type="text/javascript">
    //document.getElementsByTagName('iframe')[0].height = document.documentElement.clientHeight-10;
    
    /**
     * 页面变化调整高度
     */
    var fileName = "${fileName}";
    /* window.onresize = function(){
        var fm = document.getElementsByTagName("iframe")[0];
        fm.height = window.document.documentElement.clientHeight-10;
    } */
    if(fileName == ""){
    	$("#msg").show();
    }
</script>
</html>