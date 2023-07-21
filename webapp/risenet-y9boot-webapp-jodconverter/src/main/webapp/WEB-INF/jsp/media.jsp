<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglib.jsp"%>
<%@ include file="/static/common/head.jsp"%>

<!DOCTYPE html>
<html>
<head>
	<script type="text/javascript" charset="UTF-8" src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
    <meta charset="utf-8">
    <title>多媒体文件预览</title>
    <link rel="stylesheet" href="${ctx}/static/plyr/plyr.css">
</head>
<style>
    body{background-color: #262626}
    .m{ margin-left: auto; margin-right: auto; width:640px; margin-top: 100px; }
</style>
<body>
<div class="m">
    <video controls>
        <source src="${mediaUrl}">
    </video>
</div>
<script src="${ctx}/static/plyr/plyr.js"></script>
<script>
    plyr.setup();
</script>
</body>

</html>
