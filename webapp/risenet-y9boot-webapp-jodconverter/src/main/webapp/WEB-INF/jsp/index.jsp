<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglib.jsp"%>
<%@ include file="/static/common/head.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>在线预览</title>
     <link rel="stylesheet" href="${ctx}/static/css/viewer.min.css">
    <link rel="stylesheet" href="${ctx}/static/css/loading.css">
   <link rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/static/css/bootstrap-table.css" />
    <style type="text/css">
    </style>
    <script type="text/javascript" charset="UTF-8" src="${ctx}/static/js/jquery-1.10.2.min.js"></script>
</head>

<body>
<h1>文件预览项目接入和测试界面</h1>
<div class="panel-group" id="accordion">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordion"
                   href="#collapseOne">
                    接入说明
                </a>
            </h4>
        </div>
        <div id="collapseOne" class="panel-collapse collapse">
            <div class="panel-body">
                <div>
	     文本预览格式（txt，html，xml，java，properties，sql，log）；<br>
	  Office文件预览格式（docx，doc，xls，xlsx，ppt，pptx，pdf），其中xls，xlsx转html模式预览，其他转PDF模式预览；<br>
	     图片预览格式（jpg，jpeg，png，gif，bmp，ico，raw）；<br>
	     多媒体预览格式（mp3，mp4，flv，rmvb）；<br>
	     压缩包预览格式（rar，zip，jar，7-zip，tar，gzip，7z）；<br>
	  <br>
                    如果你的项目需要接入文件预览项目，达到文件的预览效果，那么通过在你的项目中加入下面的代码就可以
                    成功实现：
                    <pre style="background-color: #eee;color: red">
                    	<input readonly="readonly" 
                    		value="<a target='_blank' href='https://www.youshengyun.com/jodconverter/onlinePreview?url=encodeURIComponent(fileUrl)'>预览</a>" 
                    		style="width: 60%;border: none;background-color: #eee;resize:none;">
                    	</input>
                    </pre>
                </div>
            </div>
        </div>
    </div>
    <div class="panel panel-default">
        <div class="panel-heading">
            <h4 class="panel-title">
                <a data-toggle="collapse" data-parent="#accordion"
                   href="#collapseTwo">
                    预览测试
                </a>
            </h4>
        </div>
        <div id="collapseTwo" class="panel-collapse collapse">
            <div class="panel-body">
                <p style="color: red;">因为是测试所以上传的文件没有写入数据库，刷新网页后会重置列表。</p>
                <div style="padding: 10px">
                    <form enctype="multipart/form-data" id="fileUpload">
                        <input type="file" name="file" />
                        <input type="button" id="btnsubmit" value=" 上 传 " />
                    </form>
                </div>
                <div>
                    <table id="table" data-pagination="true"></table>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="loading_container">
    <div class="spinner">
        <div class="spinner-container container1">
            <div class="circle1"></div>
            <div class="circle2"></div>
            <div class="circle3"></div>
            <div class="circle4"></div>
        </div>
        <div class="spinner-container container2">
            <div class="circle1"></div>
            <div class="circle2"></div>
            <div class="circle3"></div>
            <div class="circle4"></div>
        </div>
        <div class="spinner-container container3">
            <div class="circle1"></div>
            <div class="circle2"></div>
            <div class="circle3"></div>
            <div class="circle4"></div>
        </div>
    </div>
</div>
<script src="${ctx}/static/js/jquery-3.0.0.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/jquery.form.min.js" type="text/javascript"></script>
<script src="${ctx}/static/js/bootstrap.min.js"></script>
<script src="${ctx}/static/js/bootstrap-table.js"></script>
<script>
	
	function deleteFile(id) {
		var ids = new Array();
		ids[0] = id;
		$('#table').bootstrapTable('remove',   {
	        field: 'id',
	        values: ids
	      });
	}

    $(function () {
    	 $('#table').bootstrapTable({
             url: '',
             columns: [{
                 field: 'id',
                 visible:false,
                 title: 'ID'
             },{
                 field: 'fileName',
                 title: '文件名'
             },{
                 field: 'fileUrl',
                 title: '文件路径'
             }, {
                 field: 'action',
                 title: '操作'
             },]
         }).on('pre-body.bs.table', function (e,data) {
             // 每个data添加一列用来操作
             $(data).each(function (index, item) {
            	 item.id = "t"+index;
                 item.action = "<a class='btn btn-default' target='_blank' href='${ctx}/onlinePreview?url="
                     + encodeURIComponent(item.fileUrl) +"'>预览</a>" +
                     "<a class='btn btn-default'  href='javascript:void(0);' onclick='deleteFile(\""+item.id+"\")'>删除</a>";
             });
             return data;
         }).on('post-body.bs.table', function (e,data) {
             return data;
         });

         /**
          *
          */
         function showLoadingDiv() {
             var height = window.document.documentElement.clientHeight - 1;
             $(".loading_container").css("height", height).show();
         }

         $("#btnsubmit").click(function () {
             showLoadingDiv();
             $("#fileUpload").ajaxSubmit({
                 success: function (data) {
                     // 上传完成，刷新table
                     $('#table').bootstrapTable('append', data);
                     $(".loading_container").hide();
                 },
                 error: function (error) { alert(error); $(".loading_container").hide();},
                 url: 'fileUpload', /*设置post提交到的页面*/
                 type: "post", /*设置表单以post方法提交*/
                 dataType: "json" /*设置返回值类型为文本*/
             });
         });
    });
</script>
</body>
</html>