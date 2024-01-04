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
		<link rel="stylesheet" href="${ctx}/static/jquery/layui/css/modules/layer/risesoft/style.css">
		<script type="text/javascript" charset="UTF-8" src="${ctx}/static/layer/layer.js"></script>
		<script src="${ctx}/static/jquery/layui/layui.all.js" type="text/javascript"></script>
	</head>
	<script src="${ctx}/static/js/ajaxfileupload.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${ctx}/static/js/jquery.form.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${ctx}/static/tags/js/ntkobackground.min.js?s=1" type="text/javascript" charset="UTF-8"></script>
	<body style="overflow: hidden;">
	    <span id="risesoftNTKOWord" style="color:red;display: none;">不能装载文档控件。请在检查浏览器的选项中检查浏览器的安全设置。或者下载安装<a href="${ctx}/static/tags/exe/risesoftNTKOWord.exe">跨浏览器插件</a></span>  
		<iframe id="wordformIframe" style="width: 100%;height: 100%;border: 0;overflow: hidden;display: none;"> </iframe>
	</body>	
	<script language="javascript">
		var ctx = "${ctx}";
		var itemId = "";
		var itembox = "";
		var processInstanceId = "";
		var taskId = "";
		var processSerialNumber = "";
		var browser = "";
		var tenantId = "";
		var userId = "";
		var fileId = "";
		var activitiUser = "";
		var taskDefKey = "";
		var positionId = "";
		$(document).ready(function() {
			
		});
		window.addEventListener("message",function(obj){//监听父页面消息
			var msgType = obj.data.msgType;
			if(msgType == "openWord"){//打开word
				itemId = obj.data.itemId;
				itembox = obj.data.itembox;
				processInstanceId = obj.data.processInstanceId;
				taskId = obj.data.taskId;
				processSerialNumber = obj.data.processSerialNumber;
				browser = obj.data.browser;
				tenantId = obj.data.tenantId;
				positionId = obj.data.positionId;
				userId = obj.data.userId;
				openWord();
			}if(msgType == "openFile"){//打开文件
                itemId = obj.data.itemId;
                itembox = obj.data.itembox;
                processInstanceId = obj.data.processInstanceId;
                taskId = obj.data.taskId;
                processSerialNumber = obj.data.processSerialNumber;
                browser = obj.data.browser;
                tenantId = obj.data.tenantId;
                userId = obj.data.userId;
                fileId = obj.data.fileId;
                positionId = obj.data.positionId;
                openFile();
            }else if(msgType == "printWord"){//打印word
				itemId = obj.data.itemId;
				itembox = obj.data.itembox;
				processInstanceId = obj.data.processInstanceId;
				taskId = obj.data.taskId;
				processSerialNumber = obj.data.processSerialNumber;
				browser = obj.data.browser;
				tenantId = obj.data.tenantId;
				userId = obj.data.userId;
				activitiUser = obj.data.activitiUser;
				taskDefKey = obj.data.taskDefKey;
				printWord();
			}		
	    },false);
			
		function openWord(){
			var ntkoUrl = ctx + "/transactionWord/showWord?itembox="+itembox+"&processSerialNumber="+processSerialNumber+"&itemId="+itemId+"&taskId="+taskId+"&processInstanceId="+processInstanceId+"&browser="+browser;
			var ignoreUrl = "/flowableUI/services/ntkoForm/showWord?itembox="+itembox+"&processSerialNumber="+processSerialNumber+"&itemId="
			+itemId+"&taskId="+taskId+"&processInstanceId="+processInstanceId+"&browser="+browser+"&tenantId="+tenantId+"&userId="+userId+"&positionId="+positionId;
			if (browser == "IE") {
				$('#wordformIframe').attr('src',ntkoUrl);
			}else if(browser == "firefox"){
				if(version < 50){	
					$('#wordformIframe').attr('src',ntkoUrl);
				}else{
					if(installed()){
						openWindow(ignoreUrl);
					}else{
						$('#wordformIframe').attr('src',ntkoUrl);
						$('#risesoftNTKOWord').css('display',"");
					}
				}
			}else if(browser == "chrome"){
					if(version < 42){	
						$('#wordformIframe').attr('src',ntkoUrl);
					}else{
						if(installed()){
							openWindow(ignoreUrl);
						}else{
							$('#wordformIframe').attr('src',ntkoUrl);
							$('#risesoftNTKOWord').css('display',"");
						}
					}
			}else{
				if(installed()){
					openWindow(ignoreUrl);
				}else{
					$('#wordformIframe').attr('src',ntkoUrl);
					$('#risesoftNTKOWord').css('display',"");
				}
			}
		}
		
		function openFile(){
            var ignoreUrl = "/flowableUI/services/ntkoFile/showWord?itembox="+itembox+"&processSerialNumber="+processSerialNumber+"&taskId="+taskId+"&positionId="+positionId+"&browser="+browser+"&tenantId="+tenantId+"&userId="+userId+"&fileId="+fileId;
            if (browser == "IE") {
                $('#wordformIframe').attr('src',ignoreUrl);
            }else if(browser == "firefox"){
                if(version < 50){   
                    $('#wordformIframe').attr('src',ignoreUrl);
                }else{
                    if(installed()){
                        openWindow(ignoreUrl);
                    }else{
                        $('#wordformIframe').attr('src',ignoreUrl);
                        $('#risesoftNTKOWord').css('display',"");
                    }
                }
            }else if(browser == "chrome"){
                    if(version < 42){   
                        $('#wordformIframe').attr('src',ignoreUrl);
                    }else{
                        if(installed()){
                            openWindow(ignoreUrl);
                        }else{
                            $('#wordformIframe').attr('src',ignoreUrl);
                            $('#risesoftNTKOWord').css('display',"");
                        }
                    }
            }else{
                if(installed()){
                    openWindow(ignoreUrl);
                }else{
                    $('#wordformIframe').attr('src',ntkoUrl);
                    $('#risesoftNTKOWord').css('display',"");
                }
            }
        }
		
		//word模板打印
		function printWord(){
			var printUrl = '/services/print/showPrintTemplate?processSerialNumber='+processSerialNumber+'&itembox='+itembox+'&taskId='+taskId
						+'&itemId='+itemId+'&taskDefKey='+taskDefKey+'&activitiUser='+activitiUser+'&tenantId='+tenantId+'&userId='+userId;
			if (browser == "IE") { 
				window.open(ctx + printUrl,'_blank'); 
			}else if(browser == "firefox"){
				if(version < 50){   
					window.open(ctx + printUrl,'_blank'); 
				}else{
					if(installed()){
						openWindow(printUrl);
					}else{
						window.open(ctx + printUrl,'_blank'); 
					}
				}
			}else if(browser == "chrome"){
				if(version < 42){	
					window.open(ctx + printUrl,'_blank'); 
				}else{
					if(installed()){
						openWindow(printUrl);
					}else{
						window.open(ctx + printUrl,'_blank'); 
					}
				}
			}else{
				if(installed()){
					openWindow(printUrl);
				}else{
					window.open(ctx + printUrl,'_blank'); 
				}
			}
		}
	</script>
</html>
