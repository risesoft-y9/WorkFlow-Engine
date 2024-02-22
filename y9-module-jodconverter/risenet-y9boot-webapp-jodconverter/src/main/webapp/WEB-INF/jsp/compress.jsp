<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglib.jsp"%>
<%@ include file="/static/common/head.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<title>在线预览</title>
    <link href="${ctx}/static/css/zTreeStyle.css" rel="stylesheet" type="text/css">
    <style type="text/css">
        html, body, div, span, applet, object, iframe, h1, h2, h3, h4, h5, h6, p, blockquote, pre, a, abbr, acronym, address, big, cite, code, del, dfn, em, font, img, ins, kbd, q, s, samp, small, strike, strong, sub, sup, tt, var, dl, dt, dd, ol, ul, li, fieldset, form, label, legend, table, caption, tbody, tfoot, thead, tr, th, td {
            margin: 0;padding: 0;border: 0;outline: 0;font-weight: inherit;font-style: inherit;font-size: 100%;font-family: inherit;vertical-align: baseline;}
        body {color: #2f332a;font: 15px/21px Arial, Helvetica, simsun, sans-serif;background: #f0f6e4 \9;}
        body{
            margin:0 auto;
            width: 600px;
            background-color: #eee;
            font-size: 4em;
        }
        h1, h2, h3, h4, h5, h6 {color: #2f332a;font-weight: bold;font-family: Helvetica, Arial, sans-serif;padding-bottom: 5px;}
        h1 {font-size: 24px;line-height: 34px;text-align: center;}
        h2 {font-size: 14px;line-height: 24px;padding-top: 5px;}
        h6 {font-weight: normal;font-size: 12px;letter-spacing: 1px;line-height: 24px;text-align: center;}
        a {color:#3C6E31;text-decoration: underline;}
        a:hover {background-color:#3C6E31;color:white;}
        input.radio {margin: 0 2px 0 8px;}
        input.radio.first {margin-left:0;}
        input.empty {color: lightgray;}
        code {color: #2f332a;}
        .ztree li span.button{
        	width: 18px;
			height: 17px;
        }
        .ztree li a{
       		height: 22px;
        }
        div.zTreeDemoBackground {width:600px;text-align:center;background-color: #ffffff;}
    </style>
    <script type="text/javascript" charset="UTF-8" src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
</head>
<body>

<div class="zTreeDemoBackground left">
    <ul id="treeDemo" class="ztree" style="background-color: #fff;"></ul>
</div>
</body>
<script type="text/javascript" src="${ctx}/static/js/jquery.ztree.core.js"></script>
<script type="text/javascript">
    var data = JSON.parse('${fileTree}');
    var setting = {
        view: {
            fontCss : {"color":"blue"},
            showLine: true
        },
        data: {
            key: {
                children: 'childList',
                name: 'originName'
            }
        },
        callback:{
            beforeClick:function (treeId, treeNode, clickFlag) {
            	 console.log("节点参数：treeId-" + treeId + "treeNode-"
                         + JSON.stringify(treeNode) + "clickFlag-" + clickFlag);
            },
            onClick:function (event, treeId, treeNode) {
                if (!treeNode.directory) {
                	/**实现窗口最大化**/
                    var fulls = "left=0,screenX=0,top=0,screenY=0,scrollbars=1";    //定义弹出窗口的参数
                    if (window.screen) {
                        var ah = screen.availHeight - 30;
                        var aw = (screen.availWidth - 10) / 2;
                        fulls += ",height=" + ah;
                        fulls += ",innerHeight=" + ah;
                        fulls += ",width=" + aw;
                        fulls += ",innerWidth=" + aw;
                        fulls += ",resizable"
                    } else {
                    	fulls += ",resizable"; // 对于不支持screen属性的浏览器，可以手工进行最大化。 manually
                    }
                    if(treeNode.fileName.indexOf(".pdf") > -1 || treeNode.fileName.indexOf(".PDF") > -1){
                    	window.open("${ctx}/static/previewFile/" + treeNode.fileName, "_blank",fulls);
                    }else{
                    	window.open("onlinePreview?url="
                                + encodeURIComponent("${ctx}/static/previewFile/" + treeNode.fileName)+"&fileKey="+treeNode.fileKey, "_blank",fulls);
                    }
                }
            },
            onAsyncSuccess : function(event, treeId, treeNode, msg){
            }
        }
    };
    var height = 0;
    $(document).ready(function(){
    	nodes(data);
        var treeObj = $.fn.zTree.init($("#treeDemo"), setting, data);
        treeObj.expandAll(true);
        height = getZtreeDomHeight();
        $(".zTreeDemoBackground").css("height", height);
    });

    function nodes(data){
    	if(data.childList.length > 0){
    		var childList = data.childList;
    		for(var  i= 0; i < childList.length; i++){
    			nodes(childList[i]);
     	    }
    	}else{
    		console.log(data);
    		if(data.originName.indexOf(".doc") > 0){
    			data.icon = "${ctx}/static/images/word.png";
    			
    		}else if(data.originName.indexOf(".gzip") > 0 || data.originName.indexOf(".zip") > 0 || data.originName.indexOf(".rar") > 0 || data.originName.indexOf(".jar") > 0 || data.originName.indexOf(".tar") > 0){
    			data.icon = "${ctx}/static/images/zip.png";
    			
    		}else if(data.originName.indexOf(".mp3") > 0 || data.originName.indexOf(".mp4") > 0 || data.originName.indexOf(".flv") > 0 || data.originName.indexOf(".rmvb") > 0){
    			data.icon = "${ctx}/static/images/mp4.png";
    			
    		}else if(data.originName.indexOf(".xls") > 0){
    			data.icon = "${ctx}/static/images/xls.png";
    			
    		}else if(data.originName.indexOf(".ppt") > 0){
    			data.icon = "${ctx}/static/images/ppt.png";
    			
    		}else if(data.originName.indexOf(".pdf") > 0){
    			data.icon = "${ctx}/static/images/pdf.png";
    			
    		}else if(data.originName.indexOf(".xml") > 0){
    			data.icon = "${ctx}/static/images/xml.png";
    			
    		}else if(data.originName.indexOf(".txt") > 0){
    			data.icon = "${ctx}/static/images/txt.png";
    			
    		}else if(data.originName.indexOf(".png") > 0 || data.originName.indexOf(".jpg") > 0 || data.originName.indexOf(".jpeg") > 0 || data.originName.indexOf(".gif") > 0 || data.originName.indexOf(".bmp") > 0){
    			data.icon = "${ctx}/static/images/picture.png";
    		}else{
    			data.icon = "${ctx}/static/images/other.png";
    			
    		}
    	}
    }
    
    /**
     *  计算ztreedom的高度
     */
    function getZtreeDomHeight() {
        return $("#treeDemo").height() > window.document.documentElement.clientHeight - 1
                ? $("#treeDemo").height() : window.document.documentElement.clientHeight - 1;
    }
    /**
     * 页面变化调整高度
     */
    window.onresize = function(){
        height = getZtreeDomHeight();
        $(".zTreeDemoBackground").css("height", height);
    }
    /**
     * 滚动时调整高度
     */
    window.onscroll = function(){
        height = getZtreeDomHeight();
        $(".zTreeDemoBackground").css("height", height);
    }
</script>
</html>