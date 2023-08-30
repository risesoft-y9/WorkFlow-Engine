<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglib.jsp"%>
<%@ include file="/static/common/head.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv=X-UA-Compatible content="IE=edge">
<link rel="stylesheet" href="${ctx}/static/jquery/layui/css/layui.css" media="all"/>
<script type="text/javascript" charset="UTF-8" src="${ctx}/static/layer/layer.js"></script>
<script src="${ctx}/static/jquery/layui/layui.all.js" type="text/javascript"></script>
</head>
<script type="text/javascript">
    var ctx = "${ctx}";
</script>
<body>
<div class="layui-card">
  <!-- <div class="layui-card-header">卡片面板</div> -->
  <div class="layui-card-body">
            您的电脑未安装PDF转换工具，请点击下载“<a href="${ctx }/static/tags/exe/PDFCreator-1_2_3_setup.exe"><font color="red"><strong>PDF转换工具</strong></font></a>”
  </div>
</div>
</body>
</html>
