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
	<script src="${ctx}/static/js/ajaxfileupload.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${ctx}/static/js/jquery.form.js" type="text/javascript" charset="UTF-8"></script>
	<script src="https://cdn.bootcss.com/babel-core/5.8.35/browser.min.js"></script>
	<script src="https://cdn.bootcss.com/babel-core/5.8.35/browser-polyfill.min.js"></script>
	<script src="${ctx}/static/js/wps/web-office-sdk-v1.1.8.umd.js" type="text/javascript" charset="UTF-8"></script>
	<script src="${ctx}/static/js/wps/wps_sdk.js" type="text/javascript" charset="UTF-8"></script>
	<script type="text/javascript" src="${ctx}/static/WpsOAAssist/js/json2.js"></script>
	<script type="text/javascript" src="${ctx}/static/WpsOAAssist/js/wpsjsrpcsdk.js"></script>
	<script type="text/javascript" src="${ctx}/static/WpsOAAssist/js/wps.js"></script>
	<%-- <script src="${ctx}/static/js/wps/jwps.js" type="text/javascript" charset="UTF-8"></script> --%>
<body topmargin=0 leftmargin=0 rightmargin=0 bottommargin=0 marginwidth=0 marginheight=0 >
	<table cellspacing="0" cellpadding="0" bordercolor="#E5E2DE" border="0"
		width="100%" bgcolor="#E5E2DE" style="border-bottom-style: groove;height: 5%;"
		bordercolorlight="#EEEEEE" bordercolordark="#B1AEA9">
		<tbody>
			<tr>
				<td align="left" style="font: Menu;">
					<div class="layui-btn-group" style="background-color: #1e9fff;width: 100%;">
					  <button id="openTaoHong" onclick="openTaoHong();"  class="layui-btn layui-btn-normal" >选择套红</button>
					  <button id="undoTaoHong" onclick="undoTaoHong();" class="layui-btn layui-btn-normal">撤销红头</button>
					  <button id="upload" onclick="upload();"  class="layui-btn layui-btn-normal">上传</button>
					  <form id="fileForm" action="" method="post" enctype="multipart/form-data" style="display: none;">
						  <input type="file" name="file" id="file" accept=".doc,.docx,.wps,.pdf,.tif" tabIndex="-1"  data-show-preview="true" 
								style="cursor: pointer; opacity: 0; -khtml-opacity: 0; -moz-opacity: 0;position: absolute;" />
					  </form>
					  <a <%-- href="${downloadUrl}"  --%>  href="${ctx}/docWps/download?id=${id}" id="download" class="layui-btn layui-btn-normal">下载</a>
					</div>
					</td>
			</tr>
		</tbody>
	</table>
	<div class="custom-mount" style="height: 94.5%;width: 100%;position: absolute;">
	</div>
	 <!-- <script language="javascript">
	 	function _WpsInvoke(funcs, front, jsPluginsXml) {
		    var info = {};
		    info.funcs = funcs;    
		    var func = bUseHttps ? WpsInvoke.InvokeAsHttps : WpsInvoke.InvokeAsHttp
		    func(WpsInvoke.ClientType.wps, // 组件类型
		        "WpsOAAssist", // 插件名，与wps客户端加载的加载的插件名对应
		        "dispatcher", // 插件方法入口，与wps客户端加载的加载的插件代码对应，详细见插件代码
		        info, // 传递给插件的数据        
		        function (result) { // 调用回调，status为0为成功，其他是错误
		            if (result.status) {
		                if (bUseHttps && result.status == 100) {
		                    WpsInvoke.AuthHttpesCert('请在稍后打开的网页中，点击"高级" => "继续前往"，完成授权。')
		                    return;
		                }
		                alert(result.message)

		            } else {
		                console.log(result.response)
		                showresult(result.response)
		            }
		        },
		        front,
		        jsPluginsXml)
		}
	    function newDoc() {
	        _WpsInvoke([{
	                "NewDoc": {}
	            }],
	            true,
	            "http://127.0.0.1:7056/flowableUI/static/WpsOAAssist/jsplugins.xml") // NewDoc方法对应于OA助手dispatcher支持的方法名
	    }
    </script> -->
	<script type="text/babel">
		var docUrl = "${docUrl}";
		var hasContent = "${hasContent}";
		var wps;
    	window.onload = function() {	
      		wps = WebOfficeSDK.config({
				mount: document.querySelector('.custom-mount'),
				//url:docUrl
        		url: 'https://wwo.wps.cn/office/w/2c918084770540fc01781b9ecded02f1?_w_userid=3&_w_filetype=db&_w_filepath=%E7%A9%BA%E6%96%87%E6%A1%A3.docx&_w_appid=5b8f173bd752464d81b7aa78001c697f&_w_redirectkey=123456&_w_signature=enR248IrgRS1JbWCRfwDC3IDJJA%3D',
			});
      		const test = async () => {
       			await wps.ready();
		const app = wps.Application;		
 
      		}


      		wps.on('fileOpen', function(data) {
       	 		test();
        		console.log('打开成功');
      		});


			wps.on('fileStatus', function(data) {
				if(data.status == "7" && hasContent == "0"){
					hasContent = "1";
					saveWps();
				}
			});
    	}
    </script>
	<script language="javascript">
		var ctx = "${ctx}";
		var processSerialNumber = "${processSerialNumber}";
		var processInstanceId = "${processInstanceId}";
		var itembox = "${itembox}";
		var tenantId = "${tenantId}";
		var userId = "${userId}";
		var taskId = "${taskId}";
		
		if(!(itembox == "todo" || itembox == "draft")){
			readOnlyPerssion(true,true,true);
		}
		//按钮权限控制openTaoHong=选择套红   undoTaoHong=撤销套红 upload=上传
		function readOnlyPerssion(openTaoHong,undoTaoHong,upload){
			if(openTaoHong){
				$("#openTaoHong").css("background-color","#cccccc");
				$("#openTaoHong").attr("disabled", true);
			}else{
				$("#openTaoHong").css("background-color","#1e9fff");
				$("#openTaoHong").attr("disabled", false);
			}
			if(undoTaoHong){
				$("#undoTaoHong").css("background-color","#cccccc");
				$("#undoTaoHong").attr("disabled", true);
			}else{
				$("#undoTaoHong").css("background-color","#1e9fff");
				$("#undoTaoHong").attr("disabled", false);
			}
			if(upload){
				$("#upload").css("background-color","#cccccc");
				$("#upload").attr("disabled", true);
			}else{
				$("#upload").css("background-color","#1e9fff");
				$("#upload").attr("disabled", false);
			}
		}
		
		function saveWps(){
			$.ajax({
			       async : false,  
			       cache : false,  
			       type: 'POST',
			       url:  '${ctx}/docWps/saveWps',
			       dataType:'json',
			       data:{
			    	   processSerialNumber:processSerialNumber
			       },
			       error: function () {
			    	   layer.alert("服务器发生异常", {icon: 2}); 
			       },
			       success:function(data){
			    	   
			       }
		   });
		}
		
		//上传正文
		function upload(){
			document.getElementById("file").click();
		} 
		
		$('input[type="file"]').change("propertychange", function() {
			parent.layer.load(1);
			var url = '${ctx}/docWps/upload';
			$("#fileForm").ajaxSubmit({
	            type: 'post', // 提交方式 get/post
	            url: url, // 需要提交的 url
	            data: {
	            	processInstanceId:processInstanceId,
					processSerialNumber:processSerialNumber
	            },
	            success: function(data) {
	            	parent.layer.closeAll('loading');
	            	parent.layer.msg(data.msg);
	        		var ntkoUrl = "${ctx}/docWps/showWps?itembox="+itembox+"&processSerialNumber="+processSerialNumber+"&processInstanceId="+processInstanceId;
	        		parent.$('#wordformIframe').attr('src',ntkoUrl);
	            }
	        });
		});
		
		//选择套红
		function openTaoHong() {
			var url = "${ctx}/docWps/openTaoHong";
			layer.open({
		 	      type: 2,
		 	      title: '套红模板',
		 	      shadeClose: true,
		 	      shade: 0.5,
		 	      move: false,
		 	      offset:['0vw',''],
		 	      skin:'layui-layer-lan',
		 	      area: ['45vw', '35vh'],
		 	      content: url
		 	}); 
		}
		
		 
		/*  var demo = WebOfficeSDK.config({
		    mount: document.querySelector('.custom-mount'),
		 	url: 'https://wwo.wps.cn/office/w/2c918084770540fc01781b9ecded02f1?_w_userid=3&_w_filetype=db&_w_filepath=%E7%A9%BA%E6%96%87%E6%A1%A3.docx&_w_appid=5b8f173bd752464d81b7aa78001c697f&_w_redirectkey=123456&_w_signature=enR248IrgRS1JbWCRfwDC3IDJJA%3D'
		});
	      
		console.log(demo)
		// 打开文档结果
		demo.on('fileOpen', function(data) {
		    console.log(data.success)
		    demo.ready();
		    var wordApp = WebOfficeSDK.WpsApplication;
		    console.log(wordApp)
			var bookmarks = wordApp.ActiveDocument.GetBookmarkText("RiseOffice_body");
			console.log(bookmarks);
		});   */
		
		
		
		/* var ctx = "${ctx}";
		var wps = WPS.config({
			mount: document.querySelector('.custom-mount'),
			wpsUrl: "https://wwo.wps.cn/office/w/2c918084770540fc01781b9ecded02f1?_w_userid=3&_w_filetype=db&_w_filepath=%E7%A9%BA%E6%96%87%E6%A1%A3.docx&_w_appid=5b8f173bd752464d81b7aa78001c697f&_w_redirectkey=123456&_w_signature=enR248IrgRS1JbWCRfwDC3IDJJA%3D"
		});
		$("#wps-iframe").css("height","100%");
		$("#wps-iframe").css("width","100%");
		wps.ready();
		var wordApp = wps.WpsApplication();
		if (wps.WpsApplication) { // 文字
		  	var result = wps.WpsApplication().ActiveDocument.ExportAsFixedFormatAsync();
		 	console.table(result)
		} */

      	
		     
		/* demo.on('fileStatus', function(data) {
			/*  {
				  status: 0, // 文档无更新
				  status: 1, // 版本保存成功, 触发场景： 手动保存、定时保存、关闭网页
				  status: 2, // 暂不支持保存空文件, 触发场景：内核保存完后文件为空
				  status: 3, // 空间已满
				  status: 4, // 保存中请勿频繁操作，触发场景：服务端处理保存队列已满，正在排队
				  status: 5, // 保存失败
				  status: 6, // 文件更新保存中，触发场景：修改文档内容触发的保存
				  status: 7, // 保存成功，触发场景：文档内容修改保存成功
			}  
			console.log("fileStatus")
			console.log(data)
		})  */
	</script>
</body>
</html>
