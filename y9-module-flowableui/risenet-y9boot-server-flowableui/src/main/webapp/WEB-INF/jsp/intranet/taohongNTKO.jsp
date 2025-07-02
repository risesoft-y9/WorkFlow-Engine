<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/static/common/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>套红模版选择</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv=X-UA-Compatible content="IE=edge">
<script src="${ctx}/static/js/jquery-1.8.1.min.js" type="text/javascript"></script>
<%-- <script src="${ctx}/static/js/jquery-2.1.0.js" type="text/javascript"></script> --%>
<link type="text/css" rel="stylesheet" href="${ctx}/static/css/listyle.css" />
</head>
<body style="background-color: #eee;">
<div class="ui-layout-center"  style="padding-left: 0.5%; width: 99.2%;">
    <div>
      <table width="100%" border="0" cellpadding="0" cellspacing="0" class="er_title" style="background-image: url(${ctx}/static/img/er_title_icon2.gif);">
        <tr>
          <td width="1%" style="padding: 0px;"></td>
          <td class="er_title_font" id="title1">可选套红模板</td>
          <td></td>
        </tr>
      </table>
    </div>
    <div style="overflow: auto; width: 100%" class="ui-layout-content" id="ui-layout-content">
      <table class="simple" width="430px" border="0" align="center">
      </table>
    </div>  
  </div>
</body>
<script language="JavaScript" type="text/javascript">
	var currentBureauGuid = "${currentBureauGuid}";
	var userId = "${userId}";
	var tenantId = "${tenantId}";
	$(document).ready(function(){
		buildTaohongTemplate();
		//setValue();
		//var divtemp="<div align=\"center\"><label>没有找到可用的套红模板</label></div>";
		//$(".ui-layout-content").prepend(divtemp);
	});
	function buildTaohongTemplate(){
		var url = "${ctx}/services/ntkoForm/list";
		$.ajax({
			 data : {
				 currentBureauGuid:currentBureauGuid,
				 tenantId : tenantId,
				 userId : userId
			 },
		     type: 'POST',
		     dataType:"json",
		     async: false,
		     url:  url,
		     success:function(data){
		    	 var divtemp="<div align=\"center\"><label>没有找到可用的套红模板</label></div>";
				 var trtemp="";
				 $.each(data,function(i,item){
						if(item.hasTaoHongTemplate==0){
							$(".ui-layout-content").prepend(divtemp);
						}else{
							trtemp=trtemp+"<tr><td class=\"li2_td_left\" style=\"width: 140px;\"><input type=\"radio\" name=\"taohong\" onclick=\"setValue(this)\" value=\""+item.templateGuid+"|"+item.template_fileName+"|"+item.templateType+" \"> "+item.template_fileName+"  </td></tr>"
						}
				 });
				 $(".simple").prepend(trtemp);
		     }
		 });
	}
	
	function setValue(obj){
		var v = obj.value.split("|");
		parent.DoTaoHong(v[0]);
		parent.layer.closeAll();
	}

</script>
</html>