<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglib.jsp"%>
<%@ include file="/static/common/head.jsp"%>

<!DOCTYPE html>
<html>
<head>
	<title>在线预览</title>
    <style type="text/css">
        body{
            margin: 0 auto;
            width:900px;
            background-color: #CCB;
        }
        .container{
            width: 700px;
            height: 700px;
            margin: 0 auto;
        }
        img{
            width:auto;
            height:auto;
            max-width:100%;
            max-height:100%;
            padding-bottom: 36px;
        }
        span{
            display: block;
            font-size:20px;
            color:red;
        }
    </style>
    <script type="text/javascript" charset="UTF-8" src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
</head>
<body>
<div class="container">
    <img src="${ctx }/static/images/sorry.jpg" />
    <span>
         ${msg }
    </span>
</div>
<script type="text/javascript">
</script>
</body>

</html>