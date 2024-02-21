<%@page contentType="text/html;charset=utf-8"%>
<%@ include file="/static/common/taglib.jsp" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<meta name="renderer" content="webkit">
		<title>对不起，您没有访问权限！</title>
		<script type="text/javascript" language="javascript" src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
		<style>
			#but :hover{
				background-color:#26d0d0;
				border-radius: 10px;
			}
			#notsupport{
				z-index:99;
				display:none;
				position:absolute;
				width:100%;
				height:100%;
				background-color:#2D739C;
				}
			#notsupport img{
				margin:60px 25%;
			}
		</style>
	</head>
	<body style="background-color: #ffffff;">
		<div id="notsupport">
		<img src="${ctx}/static/img/exception/notsupport.png" />
		</div>
		<div style="height: 100%;width: 100%;">
			<div style="margin:0 auto;width:750px;height:100%;border: none;background-color:#ffffff;">
				<img src="${ctx}/static/img/exception/forbidden.jpg" style="display: block;margin: 0 auto 50px auto;width: 450px;height:340px; ;"/>
				<p style="text-align:center;color: #000000;font-family: 'microsoft yahei';font-size: 40px;font-weight: 500;">
					您没有浏览的权限...
				</p>
				<span id="but" style="display:block;margin:5px auto;width: 150px;height: 35px;background-color:#1EA8A8;border-radius: 10px;cursor: pointer !important;">
					<p style="line-height:35px;text-align:center;color: #ffffff;;font-family: 'microsoft yahei';font-size: 20px;">
					点我返回
				</p>
				</span>
			</div>
		</div>
		<script type="text/javascript">
			var ctx = "${ctx}";
			$(document).ready(function(){
				$("#but").click(function(){
					$.ajax({
						type : 'GET',
						url : '${ctx}/logout',
						dataType:'JSON',
						success : function(data, status){
							location.href =data.y9LogoutUrl;
						}
					});
				});
			})
		</script>
	</body>
</html>
