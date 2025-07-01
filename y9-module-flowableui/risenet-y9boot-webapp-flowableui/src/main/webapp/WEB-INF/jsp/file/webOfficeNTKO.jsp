<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ include file="/static/common/taglib.jsp" %>
<%@ include file="/static/common/head.jsp" %>
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
<script type="text/javascript">
    var ctx = "${ctx}";
</script>
<script src="${ctx}/static/js/ajaxfileupload.js" type="text/javascript" charset="UTF-8"></script>
<script src="${ctx}/static/js/jquery.form.js" type="text/javascript" charset="UTF-8"></script>
<script src="${ctx}/static/tags/js/ntkobackground.min.js" type="text/javascript" charset="UTF-8"></script>
<body topmargin=0 leftmargin=0 rightmargin=0 bottommargin=0
      marginwidth=0 marginheight=0 onload='initDocument();' Onunload='checkSaved();'>

<table cellspacing="0" cellpadding="0" bordercolor="#E5E2DE" border="0"
       width="100%" bgcolor="#E5E2DE" style="border-bottom-style: groove;height: 4vh;"
       bordercolorlight="#EEEEEE" bordercolordark="#B1AEA9">
    <tbody>
    <tr>
        <td align="left" style="font: Menu;">
            <div class="layui-btn-group" style="background-color: #1e9fff;width: 100%;">
                <button id="saveDocument" onclick="saveDocument('btn');" class="layui-btn layui-btn-normal">保存
                </button>
                <button id="showToolbars" onclick="showToolbars(this);" class="layui-btn layui-btn-normal">显示工具栏
                </button>
                <button id="showToolbars" onclick="showRevisions(this);" class="layui-btn layui-btn-normal">隐藏修订
                </button>
                <%-- <button id="openTaoHong" onclick="openTaoHong();"  class="layui-btn layui-btn-normal" >选择套红</button>
                <button id="undoTaoHong" onclick="undoTaoHong();" class="layui-btn layui-btn-normal">撤销红头</button>
                <button id="saveEFileAndTopdf" onclick="saveEFileAndTopdf(this);"  class="layui-btn layui-btn-normal" >转PDF并上传</button>

                <button id="upload" onclick="upload();"  class="layui-btn layui-btn-normal">上传</button>
                <form id="fileForm" action="" method="post" enctype="multipart/form-data" style="display: none;">
                  <input type="file" name="file" id="file" accept=".doc,.docx,.wps,.pdf,.tif" tabIndex="-1"  data-show-preview="true"
                      style="cursor: pointer; opacity: 0; -khtml-opacity: 0; -moz-opacity: 0;position: absolute;" />
                </form>
                <button id="downloadWord" onclick="download();"  class="layui-btn layui-btn-normal">下载</button>
                <a href="${ctx}/transactionWord/download?id=" id="download" style="display: none;">下载</a> --%>
                <span class="layui-btn layui-btn-normal"
                      style="float: right;border-left: 0px solid rgba(255, 255, 255, 0.5);"><b>当前人: </b>${userName}&nbsp;&nbsp;</span>
            </div>
        </td>
    </tr>
    </tbody>
</table>
</td>
</tr>
</tbody>
</table>
<script type="text/javascript" src="${ctx }/static/tags/js/ntkoofficecontrol.min.js"></script>
<script language="javascript">
    String.prototype.trim = function () {
        return this.replace(/(^\s*)|(\s*$)/g, "");
    };
    var fileType;
    var wordcount;
    var isSaveAs = false;
    var wordTitle;
    var NTKO = document.getElementById("riseOffice");
    var fileDocumentId = "";
    var wordReadOnly = "";
    var userName = "";
    var openWordOrPDF = "";
    var permission = 'YES';
    var documentTitle = "";
    var processSerialNumber = "";
    var processInstanceId = "";
    var itembox = "";
    var itemId = "";
    var taskId;
    var isSign;
    var taoHongWordReadOnly = false;//从待办进入正文的时候，如果是套红的word，只读
    var isTaoHong = "0";
    var browser = "";
    var hasPower = "${hasPower}";
    var tenantId = "";
    var userId = "";
    var documentTitle = "";
    var positionId = "";
    var fileName = "";

    function initDocument() {
        fileType = "${fileType}";
        wordReadOnly = '${wordReadOnly}';
        openWordOrPDF = '${openWordOrPdf}';
        fileDocumentId = '${fileId}';
        userName = '${userName}';
        itembox = "${itembox}";
        taskId = "${taskId}";
        itemId = "${itemId}";
        tenantId = "${tenantId}";
        userId = "${userId}";
        documentTitle = "${documentTitle}";
        isTaoHong = "${isTaoHong}";
        processSerialNumber = "${processSerialNumber}";
        processInstanceId = "${processInstanceId}";
        browser = "${browser}";
        positionId = "${positionId}";
        fileName = "${fileName}";
        NTKO = document.getElementById("riseOffice");
        if (fileDocumentId != "") {
            if (itembox != "add" && itembox != "todo" && itembox != "draft") {
                readOnlyPerssion(true, false, true, false, false, false);
                wordReadOnly = "YES";
            } else {
                readOnlyPerssion(false, false, true, false, false, false);
            }
            setTimeout('openDocument()', 500);
        }
        wordBtnControl(wordReadOnly);
        AddMyMenuItems();
        var userAgent = navigator.userAgent.toLowerCase();
        if (userAgent.indexOf("chrome") != -1 || userAgent.indexOf("firefox") != -1) {

        } else {
            NTKO.WebUserName = userName;
            //设置word控件自身按钮
            sysFileCtr(true);
        }
        //NTKO.ActiveDocument.Saved = true;
    }

    $(document).ready(function () {
        $(window).on('resize', function () {
            resize();
        });
    });

    //文档打开后的事件，需要加载或者处理的方法
    function setFileReadOnly(File, Document) {
        NTKO.ActiveDocument.Saved = true;
        NTKO.WebUserName = userName;
        NTKO.Activate(true);
        //处理正文word是否只读
        //wordBtnControl(wordReadOnly);
        //设置word控件自身按钮
        sysFileCtr(true);
        //NTKO.ActiveDocument.Saved = true;
    }

    function wordBtnControl(str) {
        //NTKO.Toolbars = false;
        if (str == 'YES') {
            NTKO.SetReadOnly(true, "", null, 3);
            readOnlyPerssion(true, true, true, true, false, true);
        } else {
            if (isTaoHong == '1') {
                //NTKO.SetReadOnly(true,"",3);
            } else {
                //trackRevisions(true);
            }
        }
    }

    //外部上传PDF和转PDF文件按钮控制
    function btnControl(flag) {
        if (flag == '2') {
            readOnlyPerssion(true, true, true, true, true, true);
        } else {
            readOnlyPerssion(true, true, true, false, false, false);
        }
    }

    //按钮权限控制saveDocument=保存   openTaoHong=选择套红   undoTaoHong=撤销套红
    function readOnlyPerssion(saveDocument, openTaoHong, undoTaoHong, saveEFileAndTopdf, showToolbars, upload) {
        if (saveDocument) {
            $("#saveDocument").css("background-color", "#cccccc");
            $("#saveDocument").attr("disabled", true);
        } else {
            $("#saveDocument").css("background-color", "#1e9fff");
            $("#saveDocument").attr("disabled", false);
        }
        /* if(openTaoHong){
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
        if(saveEFileAndTopdf){
            $("#saveEFileAndTopdf").css("background-color","#cccccc");
            $("#saveEFileAndTopdf").attr("disabled", true);
        }else{
            $("#saveEFileAndTopdf").css("background-color","#1e9fff");
            $("#saveEFileAndTopdf").attr("disabled", false);
        }
        if(showToolbars){
            $("#showToolbars").css("background-color","#cccccc");
            $("#showToolbars").attr("disabled", true);
        }else{
            $("#showToolbars").css("background-color","#1e9fff");
            $("#showToolbars").attr("disabled", false);
        }
        if(upload){
            $("#upload").css("background-color","#cccccc");
            $("#upload").attr("disabled", true);
        }else{
            $("#upload").css("background-color","#1e9fff");
            $("#upload").attr("disabled", false);
        } */
    }

    //判断是不是IE
    function checkIE() {
        if (!!window.ActiveXObject || "ActiveXObject" in window) {
            return true;
        } else {
            return false;
        }
    }

    function checkWps() {
        try {
            new ActiveXObject("WScript.Shell");
        }   //创建新控件
        catch (x) {
            return false;
        }
        try {
            new ActiveXObject("Wps.Application");
        }//创建word新应用程序
        catch (x) {
            return null;
        }
        return true;
    }

    function checkWord() {
        try {
            new ActiveXObject("Word.Application");
        }//创建word新应用程序
        catch (x) {
            return null;
        }
        return true;
    }

    function check360() {
        //application/vnd.chromium.remoting-viewer 可能为360特有
        var is360 = _mime("type", "application/vnd.chromium.remoting-viewer");
        if (isChrome() && is360) {
            return true;
        }
    }

    //检测是否是谷歌内核(可排除360及谷歌以外的浏览器)
    function isChrome() {
        var ua = navigator.userAgent.toLowerCase();
        return ua.indexOf("chrome") > 1;
    }

    //测试mime
    function _mime(option, value) {
        var mimeTypes = navigator.mimeTypes;
        for (var mt in mimeTypes) {
            if (mimeTypes[mt][option] == value) {
                return true;
            }
        }
        return false;
    }

    function resize() {
        var currHeight = document.body.clientHeight;
        var currWidth = document.body.clientWidth;
        if (browser == 'IE') {
            $("#riseOffice").css("height", '92vh');
            $("#riseOffice").css("width", '100%');
        } else {
            $("#riseOffice").css("height", '1000px');
            $("#riseOffice").css("width", '100%');
        }
    }

    function checkSaved() {
        if (itembox == 'add' || itembox == 'todo' || itembox == 'draft') {
            /* layer.open({
                  type: 1,
                  content: '传入任意的文本或html' //这里content是一个普通的String
                }); */
            if (NTKO.ActiveDocument.Saved == false) {
                //if (document.getElementById("riseOffice").ActiveDocument.Saved==false) {
                //if (wordcount!=NTKO.ActiveDocument.Words.Count) {
                //if (confirm("是否保存对文档的修改？")){

                saveDocument();
                //}
            }
        }
    }

    /* window.onbeforeunload = function() {
        //NTKO.ShowTipMessage("",NTKO.ActiveDocument.Saved);
        if (NTKO.ActiveDocument.Saved==false) {
            var isAccept = NTKO.ShowConfirmMessage("提示","是否保存对文档的修改？",false,true);
            if (isAccept)
            {
                saveDocument();
            }
        }
        return;
    } */

    /*  window.onunload = function(){
          if (NTKO.ActiveDocument.Saved==false) {
              NTKO.ShowTipMessage("","请先保存正文！");
              window.top.location.href = "javascript:void(0);";
              return false;
          }
    }   */

    function checkSaveDocument() {
        if (NTKO.ActiveDocument.Saved == false) {
            //if (wordcount!=NTKO.ActiveDocument.Words.Count) {
            saveDocument();
        }
    }

    function newDocument() {
        resize();
        fileType = '.doc';
        readOnlyPerssion(false, false, true, true, false, false);
        try {
            var ntkoUrl = "${ctx}/transactionWord/openBlankWordTemplate";
            if (browser != "IE") {
                /* if(browser=="chrome"){
                    if(version<"42"){
                        ntkoUrl = "
                ${ctx}/transactionWord/openBlankWordTemplate";
					}else{
						ntkoUrl = "
                ${ctx}/services/ntkoForm/openBlankWordTemplate";
					}
				}else if(browser=="firefox"){
					console.log(version);
					if(version<"50"){
						ntkoUrl = "
                ${ctx}/transactionWord/openBlankWordTemplate";
					}else{
						ntkoUrl = "
                ${ctx}/services/ntkoForm/openBlankWordTemplate";
					}
				} */
                ntkoUrl = "${ctx}/services/ntkoForm/openBlankWordTemplate";
            }
            NTKO.OpenFromURL(ntkoUrl);
        } catch (e) {
            NTKO.BeginOpenFromURL("${ctx}/transactionWord/openBlankWordTemplate");
        }
    }

    function openDocument() {
        resize();
        try {
            isSign = false;
            NTKO.IsShowOpeningDocUI = false;
            var hUrl = "${ctx}/transactionWord/openDocument?processSerialNumber="
                + processSerialNumber + "&itemId=${itemId}";
            /* if(browser!='IE'){
                hUrl = "
            ${ctx}/services/print/openDoc?processSerialNumber="
					+ processSerialNumber + "&itemId=
            ${itemId}"+"&tenantId="+tenantId+"&userId="+userId;
			} */
            if (browser != "IE") {
                /* if(browser=="chrome"){
                    if(version<"42"){
                        hUrl = "
                ${ctx}/transactionWord/openDocument?processSerialNumber="
							+ processSerialNumber + "&itemId=
                ${itemId}";
					}else{
						hUrl = "
                ${ctx}/services/ntkoForm/openDocument?processSerialNumber="
							+ processSerialNumber + "&itemId=
                ${itemId}"+"&tenantId="+tenantId+"&userId="+userId;
					}
				}else if(browser=="firefox"){
					if(version<"50"){
						hUrl = "
                ${ctx}/transactionWord/openDocument?processSerialNumber="
							+ processSerialNumber + "&itemId=
                ${itemId}";
					}else{
						hUrl = "
                ${ctx}/services/ntkoForm/openDocument?processSerialNumber="
							+ processSerialNumber + "&itemId=
                ${itemId}"+"&tenantId="+tenantId+"&userId="+userId;
					}
				} */
                var hUrl = "${ctx}/services/ntkoFile/openDoc?fileId=" + fileDocumentId + "&tenantId=" + tenantId;
            }
            NTKO.OpenFromURL(hUrl, false);
            NTKO.Activate(true);
        } catch (e) {
            alert(e.name + ": " + e.message + ",请联系管理员");
        }
    }

    //跳转至转换后的PDF文件(谷歌和火狐回调方法)
    function openToPDFCallBack() {
        readOnlyPerssion(true, true, true, false, false, false);
        loadPDF();
    }

    //跳转至转换后的PDF文件
    function openToPDF() {
        readOnlyPerssion(true, true, true, false, false, false);
        loadPDF();
    }

    function loadPDF() {
        resize();
        var riseOffice = document.getElementById('riseOffice');
        //TANGER_OCX_OBJ = document.all("TANGER_OCX");
        //---------以下是告知文档控件，增加对PDF文档类型的支持。
        //AddDocTypePlugin方法参数说明:第一个参数扩展名,第二个参数ProID,第三个参数版本号，第四个参数是pdf插件相对当前网页的URL，
        //第5个参数是文档类型标识,第6个参数是是否自动下载插件dll,当设定为自动下载时，会比较当前客户机安装的版本，如果比第3个参数
        //指定的版本底，或者客户机没有安装，会自动下载并注册插件dll。
        if (window.navigator.platform == "Win32") {
            riseOffice.AddDocTypePlugin(".pdf", "PDF.NtkoDocument", "4.0.0.8", "${ctx}/static/tags/cab/ntkooledocall.cab", 51, true);
            riseOffice.AddDocTypePlugin(".tif", "TIF.NtkoDocument", "4.0.0.8", "${ctx}/static/tags/cab/ntkooledocall.cab", 52);
        } else {
            riseOffice.AddDocTypePlugin(".pdf", "PDF.NtkoDocument", "4.0.0.8", "${ctx}/static/tags/cab/ntkooledocallX64.cab", 51, true);
            riseOffice.AddDocTypePlugin(".tif", "TIF.NtkoDocument", "4.0.0.8", "${ctx}/static/tags/cab/ntkooledocallX64.cab", 52);
        }

        var url = "${ctx}/transactionWord/openPdf?processSerialNumber=" + processSerialNumber;
        if (browser != "IE") {
            url = "${ctx}/services/ntkoForm/openPdf?processSerialNumber=" + processSerialNumber + "&tenantId=" + tenantId + "&userId=" + userId;
        }
        riseOffice.BeginOpenFromURL(url, false); //打开PDF从URL
    }

    function setFont() {
        var fonts = NTKO.ActiveDocument.Application.FontNames;
        var flag = 0;
        for (var i = 1; i <= fonts.Count; i++) {
            if (fonts(i) == '华文中宋') {
                flag = 1;
                break;
            }
        }
        if (flag == 0) {
            NTKO.ActiveDocument.Content.Font.Name = '宋体';
        }
    }

    var save = false;
    var noMsg = '';

    function saveDocument(str) {
        var info = "";
        noMsg = str;
        if (itembox == 'add' || itembox == 'todo' || itembox == 'draft') {
            //trackRevisions(false);
            //acceptAllRevisions();
            try {
                var url = "${ctx}/services/ntkoFile/uploadWord?fileId=" + fileDocumentId + "&positionId="
                    + positionId + "&processSerialNumber=" + processSerialNumber + "&taskId=" + taskId + "&tenantId=" + tenantId + "&userId=" + userId;
                info = NTKO.SaveToURL(url, "currentDoc", "", "huifuzhengwen/FileName", 0, false);
                var userAgent = navigator.userAgent.toLowerCase();
                if (userAgent.indexOf("chrome") != -1 || userAgent.indexOf("firefox") != -1) {

                } else {
                    if (!save) {
                        if (info == "success:true") {
                            if (str == 'btn') {
                                alert("保存文件成功");
                            }
                            return true;
                        } else {
                            alert("保存文件错误，请联系管理员！");
                            return;
                        }
                        NTKO.ActiveDocument.Saved = true;
                    }
                }
            } catch (e) {
                if (!save) {
                    alert('无法保存文件');
                }
            }
        }
        return info;
    }

    //谷歌和火狐是异步的，所以需要方法回调
    function OnSaveToURL(type, code, html) {
        if (!save) {
            if (html == "success:true") {
                if (noMsg == 'btn') {
                    NTKO.ShowTipMessage("", "保存成功！")
                    //alert("保存文件成功");
                }
                NTKO.ActiveDocument.Saved = true;

            } else {
                NTKO.ShowTipMessage("", "保存文件错误，请联系管理员！")

            }
        }
    }

    function saveEFileAndTopdf(flag) {
        var isPDFCreatorInstalled = NTKO.IsPDFCreatorInstalled();
        if (isPDFCreatorInstalled) {
            var s = flag.innerHTML;
            if (s == '转PDF并上传') {
                flag.innerHTML = '撤销PDF';
                saveToPDFBeforeDocument();
                setTimeout('wordTopdf("3")', 200);
            } else {
                flag.innerHTML = '转PDF并上传';
                revokepdf("1");
            }
        } else {
            layer.open({
                type: 2,
                title: '安装提示',
                area: ['30vw', '20vh'],
                shadeClose: true,
                shade: 0.5,
                move: false,
                maxmin: false,
                skin: 'layui-layer-lan',
                offset: 't',
                content: '${ctx}/services/ntkoForm/showDownPdfTool'
            })

            /* if(browser=='IE'){
                parent.layer.msg("本机没有安装PDFCreator(1.2.3版本)转换器");
            }else{
                alert("本机没有安装PDFCreator(1.2.3版本)转换器");
            } */
            return false;
        }
    }

    function wordTopdf(saveflag) {
        //trackRevisions(false);
        //acceptAllRevisions();
        //saveToPDFBeforeDocument();//转PDF之前先保存一份正文，以便撤销PDF加载原来的word正文
        try {
            fileType = ".pdf";
            isTaoHong = saveflag;
            var pdfUrl = "${ctx}/transactionWord/saveAsPDFFile?processSerialNumber=" + processSerialNumber + "&taskId=" + taskId
                + "&isTaoHong=" + saveflag + "&fileType=" + fileType + "&processInstanceId=" + processInstanceId;
            if (browser != "IE") {
                pdfUrl = "${ctx}/services/ntkoForm/saveAsPDFFile?processSerialNumber=" + processSerialNumber + "&taskId=" + taskId
                    + "&isTaoHong=" + saveflag + "&fileType=" + fileType + "&processInstanceId=" + processInstanceId + "&tenantId=" + tenantId + "&userId=" + userId;
            }
            NTKO.PublishAsPDFToURL(pdfUrl, "currentDoc", "", "xxxxx.pdf", "0");
            btnControl(saveflag);
        } catch (e) {
            if (browser == 'IE') {
                parent.layer.msg("转换失败");
            } else {
                alert("转换失败");
            }
            $('#saveEFileAndTopdf').text("转PDF并上传");

        }
    }


    function saveToPDFBeforeDocument() {
        fileType = '.doc';
        isTaoHong = "1";
        var url = "${ctx}/transactionWord/uploadWord?processInstanceId=" + processInstanceId + "&fileType="
            + fileType + "&processSerialNumber=" + processSerialNumber + "&taskId=" + taskId + "&isTaoHong=" + isTaoHong;
        if (browser != "IE") {
            url = "${ctx}/services/ntkoForm/uploadWord?processInstanceId=" + processInstanceId + "&fileType="
                + fileType + "&processSerialNumber=" + processSerialNumber + "&taskId=" + taskId + "&isTaoHong=" + isTaoHong + "&tenantId=" + tenantId + "&userId=" + userId;
        }

        var info = NTKO.SaveToURL(url, "currentDoc", "", "huifuzhengwen/FileName", 0);
        NTKO.ActiveDocument.Saved = true;
        console.log(info);
    }

    //撤销pdf:删除PDF,加载转PDF之前的word文档
    function revokepdf(openFlag) {
        try {
            var docType = NTKO.DocType;
            if (docType != 51) {
                if (browser == 'IE') {
                    parent.layer.msg("当前非PDF文档,不可以执行撤销PDF操作");
                } else {
                    alert("当前非PDF文档,不可以执行撤销PDF操作");
                }
                return false;
            }
            var url = "${ctx}/transactionWord/openRevokePDFAfterDocument?processSerialNumber="
                + processSerialNumber + "&istaohong=" + openFlag;
            if (browser != "IE") {
                url = "${ctx}/services/ntkoForm/openRevokePDFAfterDocument?processSerialNumber="
                    + processSerialNumber + "&istaohong=" + openFlag + "&tenantId=" + tenantId + "&userId=" + userId;
            }
            NTKO.OpenFromURL(url, false);
            fileType = '.doc';
            isTaoHong = "1";
            readOnlyPerssion(false, false, false, false, false, false);
        } catch (e) {
            if (browser == 'IE') {
                parent.layer.msg('撤销失败');
            } else {
                alert('撤销失败');
            }

        }
    }

    function showMenubar(bool) {
        NTKO.Menubar = bool;
    }

    function showStatusbar(bool) {
        NTKO.Statusbar = bool;
    }

    function showToolbars(da) {
        var s = da.innerHTML;
        if (s == '显示工具栏') {
            NTKO.Toolbars = true;
            da.innerHTML = '隐藏工具栏';
        } else {
            NTKO.Toolbars = false;
            da.innerHTML = '显示工具栏';
        }
    }

    function showRevisions(da) {
        var s = da.innerHTML;
        if (s == '显示修订') {
            NTKO.ActiveDocument.ShowRevisions = true;
            da.innerHTML = '隐藏修订';
        } else {
            NTKO.ActiveDocument.ShowRevisions = false;
            da.innerHTML = '显示修订';
        }
    }

    function acceptAllRevisions() {
        NTKO.ActiveDocument.AcceptAllRevisions();
    }

    function rejectAllRevisions() {
        NTKO.ActiveDocument.RejectAllRevisions();
    }

    //进入或退出强制痕迹保留状态:就是打开或者关闭修订模式以及wps本身可操作的工具栏
    function setMarkModify(bool) {
        trackRevisions(bool);
        enableReviewBar(bool);
    }

    //打开或者关闭修订模式
    function trackRevisions(bool) {
        NTKO.ActiveDocument.TrackRevisions = bool;
        NTKO.ActiveDocument.EnterRevisionMode = bool;
        /* NTKO.ActiveDocument.TrackRevisions(true);
        NTKO.ActiveDocument.EnterRevisionMode(true); */
    }

    //允许或禁止显示修订工具栏和工具菜单（保护修订,用户不能更改当前修订状态）
    function enableReviewBar(bool) {
        try {
            NTKO.ActiveDocument.CommandBars("Reviewing").Enabled = bool;
            NTKO.ActiveDocument.CommandBars("Track Changes").Enabled = bool;
            NTKO.IsShowToolMenu = bool;
        } catch (e) {

            var doc = NTKO.ActiveDocument;
            var app = doc.Application;
            var doctype = NTKO.DocType;
            if (6 != doctype || 1 != doctype) {
                return;
            }
            var cmdbars = app.CommandBars;
            NTKO.IsShowToolMenu = !bool;
            doc.TrackRevisions = bool;
            cmdbars("Reviewing").Enabled = !bool;
            cmdbars("Reviewing").Visible = !bool;
            cmdbars(40).Enabled = !bool;
            cmdbars(40).Visible = !bool;
        }
    }

    function MyDoHandSign() {
        try {
            NTKO.DoHandSign2("刘翔",
                "{BFA80B14-0000-0000-444A-9EEE00000061}");
        } catch (e) {
            parent.layer.msg(e);
        }
    }

    //接受所有修订
    function acceptAllRevisions() {
        NTKO.ActiveDocument.AcceptAllRevisions();
    }

    //拒绝所有修订
    function rejectAllRevisions() {
        NTKO.ActiveDocument.RejectAllRevisions();
    }

    //用来显示文档中的签名印章信息
    function MyCheckSign() {
        var result = NTKO.DoCheckSign(true, "SignKey4456");
        parent.layer.msg(trimResult(result));
    }

    //隐藏电子印章
    function hideSign() {
        NTKO.SetSignsVisible("*", false, "SignKey4456", 0);
    }

    function trimResult(info) {
        var Rstr = "";
        Rstr = info.replace(/#|\s使用者:手写签名/g, '');
        Rstr = Rstr.replace(/印章:/g, '操作:');
        Rstr = Rstr.replace(/使用者/g, '印章隶属');
        return (Rstr);
    }

    //从URL增加印章到当前光标所在的段落的指定位置。
    function MyAddSignFromURL() {
        NTKO.AddSignFromURL("刘翔", "${ctx}/static/signature/risesoft.esp",
            50, 50, "SignKey4456", 1, 100, 0);
    }

    /* function SetDocUser(userName) {
        var NTKO = document.getElementById("riseOffice");
        with (NTKO.ActiveDocument.Application) {
            UserName = userName;
        }
    } */

    function executeAction() {
        //setMarkModify(false);
        //NTKO.SetReadONly(false, "");
        //trackRevisions(false);
        var BookMarkName = "RiseOffice_body";
        var ac = NTKO.ActiveDocument;
        var picName = "hongtou.png";
        var picRange = ac.shapes.item(picName);
        if (picRange.Name == picName) {
            picRange.Delete();
        }

        var sel = ac.Application.Selection;
        var bmObj = ac.BookMarks.item(BookMarkName);
        var saverange = bmObj.Range;
        saverange.Cut();
        sel.WholeStory();
        sel.Delete();
        sel.HomeKey(6);
        saverange.Paste();
        //trackRevisions(true);
        isTaoHong = "0";
        saveDocument('delTaohong');
        readOnlyPerssion(false, false, false, true, false, false);
    }

    function deleteTaoHong(isTaoHong) {
        var url = '${ctx}/transactionWord/deleteWordByIsTaoHong';
        if (browser != "IE") {
            url = '${ctx}/services/ntkoForm/deleteWordByIsTaoHong?tenantId=' + tenantId + '&userId=' + userId;
        }
        $.ajax({
            data: {
                processSerialNumber: processSerialNumber,
                isTaoHong: isTaoHong
            },
            type: 'POST',
            dataType: "json",
            async: false,
            url: url,
            success: function (data) {

            }
        });
    }

    function undoTaoHong() {
        var BookMarkName = "RiseOffice_body";
        if (!NTKO.ActiveDocument.BookMarks.Exists(BookMarkName)) {
            //parent.layer.msg("没有发现套红模板或者模板中不存在RiseOffice_body书签。");

        } else {
            executeAction();
        }
    }

    //套红前要接受所有的修订，且不可以打开修订，之后不能修改正文的内容
    function DoTaoHong(guid) {
        try {
            //(false);
            //trackRevisions(false);
            //acceptAllRevisions();

            var BookMarkName = "RiseOffice_body";
            var bkCount = NTKO.ActiveDocument.BookMarks.Count;//书签数量
            var curSel = NTKO.ActiveDocument.Application.Selection;//选取word当前所有的文本
            var picName = "hongtou.png";
            var pic = NTKO.ActiveDocument.shapes;
            var beforeCount = pic.Count;

            if (!NTKO.ActiveDocument.BookMarks.Exists(BookMarkName)) {
                curSel.WholeStory();
                curSel.Cut();
            } else {
                if (beforeCount > 0) {
                    var shape1 = pic.item(beforeCount);
                    if (shape1.Name == picName) {
                        pic.item(picName).Delete();
                        beforeCount = NTKO.ActiveDocument.shapes.Count;
                    }
                }
                var srange = NTKO.ActiveDocument.BookMarks.item(BookMarkName).Range;
                srange.Cut();
                curSel.WholeStory();
                curSel.Delete();
            }
            isSign = true;
            var url = "${ctx}/transactionWord/openTaohongTemplate?templateGUID=" + guid + "&processSerialNumber=" + processSerialNumber;
            if (browser != "IE") {
                url = "${ctx}/services/ntkoForm/openTaohongTemplate?templateGUID=" + guid + "&processSerialNumber=" + processSerialNumber + "&tenantId=" + tenantId + "&userId=" + userId;
            }
            NTKO.AddTemplateFromURL(url);

            var userAgent = navigator.userAgent.toLowerCase();
            if (userAgent.indexOf("chrome") != -1 || userAgent.indexOf("firefox") != -1) {

            } else {
                var BookMarkName = "RiseOffice_body";//用来定位文本内容
                var curSel = NTKO.ActiveDocument.Application.Selection;//选取word当前所有的文本
                var srange = NTKO.ActiveDocument.BookMarks.item(BookMarkName).Range;
                curSel.GoTo(-1, 0, 0, BookMarkName);
                srange.Paste();
                NTKO.ActiveDocument.BookMarks.Add(BookMarkName, srange);
                var afterPic = NTKO.ActiveDocument.shapes;
                var afterCount = afterPic.Count;
                if (afterCount > beforeCount) {
                    var shape = pic.item(afterCount);
                    shape.Name = picName;
                }
                //trackRevisions(true);
                readOnlyPerssion(false, false, false, false, false, false);
                isTaoHong = "1";
                saveDocument('taohong');
            }
        } catch (e) {
            //parent.layer.msg("套红发生异常");
        }
    }

    function OnAddTemplateFromURL() { //谷歌和火狐浏览器套红回调方法
        //NTKO.ShowTipMessage("","套红回调成功！")
        if (isSign) {
            //trackRevisions(true);
            var NTKO = document.getElementById("riseOffice");
            var BookMarkName = "RiseOffice_body";//用来定位文本内容
            var curSel = NTKO.ActiveDocument.Application.Selection;//选取word当前所有的文本
            var srange = NTKO.ActiveDocument.BookMarks.item(BookMarkName).Range;
            curSel.GoTo(-1, 0, 0, BookMarkName);
            srange.Paste();
            NTKO.ActiveDocument.BookMarks.Add(BookMarkName, srange);
            //trackRevisions(true);
        }
        readOnlyPerssion(false, true, false, false, false, false);
        isTaoHong = "1";
        saveDocument('taohong');
    }

    //选择套红
    function openTaoHong() {
        var activitiUser = "${activitiUser}";
        var BookMarkName = "RiseOffice_body";
        //acceptAllRevisions();
        var url = "${ctx}/transactionWord/openTaoHong?activitiUser=" + activitiUser;
        if (browser != "IE") {
            url = "${ctx}/services/ntkoForm/openTaoHong?activitiUser=" + activitiUser + "&tenantId=" + tenantId + "&userId=" + userId;
        }
        layer.open({
            type: 2,
            title: '套红模板',
            shadeClose: true,
            shade: 0.5,
            move: false,
            offset: ['0vw', '25vw'],
            skin: 'layui-layer-lan',
            area: ['45vw', '30vh'],
            content: url
        });

    }

    function AddMyMenuItems() {
        try {
            NTKO.AddCustomMenu2(1, "菜单   ");
            NTKO.AddCustomMenuItem2(1, 0, -1, false, '接受所有修订', false, 0);////////////////
            NTKO.AddCustomMenuItem2(1, 1, -1, false, '拒绝所有修订', false, 1);
            /* NTKO.AddCustomMenuItem2(1,2,-1,false,'手写签名',  false, 2);
            NTKO.AddCustomMenuItem2(1,3,-1,false,'电子印章', false,  3);
            NTKO.AddCustomMenuItem2(1,4,-1,false,'签章验证', false,  4);  */
        } catch (err) {
            //parent.layer.msg("不能创建新对象：" + err.number + ":" + err.description);
            alert("不能创建新对象：" + err.number + ":" + err.description);
        } finally {
        }
    }

    function selectCustomMenuCmd(menuPos, submenuPos, subsubmenuPos, menuCaption, menuID) {
        switch (menuID) {
            case 0:
                acceptAllRevisions();
                break;
            case 1:
                rejectAllRevisions();
                break;
            case 2:
                MyDoHandSign();
                break;
            case 3:
                MyAddSignFromURL();
                break;
            case 4:
                MyCheckSign();
                break;
        }
    }

    function sysFileCtr(boolvalue) {
        //允许或禁止文件－>新建菜单
        NTKO.FileNew = true;
        //打开菜单
        NTKO.FileOpen = true;
        //关闭菜单
        NTKO.FileClose = true;
        //保存菜单
        NTKO.FileSave = boolvalue;
        //另存为菜单
        NTKO.FileSaveAs = boolvalue;
        //打印菜单
        NTKO.FilePrint = true;
        //打印预览菜单
        NTKO.FilePrintPreview = true;
    }

    /* function goToFileCommand(cmd,canceled){
         console.log(cmd);
         if(cmd == 3) {
            NTKO.CancelLastCommand = true;
            saveDocument();
          }
    }  */


    function upload() {
        document.getElementById("file").click();
    }

    $('input[type="file"]').change("propertychange", function () {
        layer.load(1);
        var url = '${ctx}/transactionWord/upload';
        if (browser != "IE") {
            url = '${ctx}/services/ntkoForm/upload?tenantId=' + tenantId + '&userId=' + userId;
        }
        $("#fileForm").ajaxSubmit({
            type: 'post', // 提交方式 get/post
            url: url, // 需要提交的 url
            data: {
                processInstanceId: processInstanceId,
                processSerialNumber: processSerialNumber,
                taskId: taskId
            },
            success: function (data) { // data 保存提交后返回的数据，一般为 json 数据
                if (!data.success) {
                    if (browser == 'IE') {
                        parent.layer.msg(data.msg);
                    } else {
                        if (browser == "chrome") {
                            if (version < "42") {
                                //layer.msg("上传成功");
                                NTKO.ShowTipMessage("", "上传失败！");
                            } else {
                                alert("上传失败");
                            }
                        } else {
                            alert("上传失败");
                        }
                    }
                } else {
                    if (browser == 'IE') {
                        layer.msg("上传成功");
                    } else {
                        if (browser == "chrome") {
                            if (version < "42") {
                                //layer.msg("上传成功");
                                NTKO.ShowTipMessage("", "上传成功！");
                            } else {
                                alert("上传成功");
                            }
                        } else {
                            alert("上传成功");
                        }
                    }
                    if (data.isPdf) {
                        loadPDF();
                        isTaoHong = "2";
                        readOnlyPerssion(true, true, true, true, true, false);
                    } else {
                        openDocument();
                    }
                }
                layer.closeAll('loading');
            }
        });

    });

    function openPDFOrTIF() {
        parent.openPDF();
    }

    function download() {
        var url = '${ctx}/transactionWord/getUpdateWord';
        if (browser != "IE") {
            url = '${ctx}/services/ntkoForm/getUpdateWord?tenantId=' + tenantId + '&userId=' + userId;
        }
        $.ajax({
            data: {
                processSerialNumber: processSerialNumber
            },
            type: 'POST',
            dataType: "json",
            async: false,
            url: url,
            success: function (data) {
                var id = data.fileStoreId;
                if (id == undefined || id == "undefined" || id == "" || id == null) {
                    if (browser == 'IE') {
                        parent.layer.msg('暂无文件下载');
                        return;
                    } else {
                        alert("暂无文件下载");
                        return;
                    }
                }
                var ntkoUrl = "${ctx}/transactionWord/download?id=" + id + "&processSerialNumber=" + processSerialNumber + "&processInstanceId=" + processInstanceId + "&fileType=" + fileType;
                if (browser != 'IE') {
                    ntkoUrl = "${ctx}//services/ntkoForm/downloadWord?id=" + id + "&processInstanceId=" + processInstanceId + "&fileType=" + fileType + "&tenantId=" + tenantId + "&userId=" + userId;
                }
                $("#download").attr("href", ntkoUrl);
                document.getElementById("download").click();

            }
        });
    }
</script>
<script language="JScript" for="riseOffice"
        event="AfterPublishAsPDFToURL(ret,code)">
    if (code != 0) {

    } else {
        /* if(confirm('是否要保存word原稿？')){
            SaveAsEFile();
        } */
        openToPDF();
    }
</script>

<script language="JScript" for="riseOffice"
        event="OnCustomMenuCmd2(menuPos,submenuPos,subsubmenuPos,menuCaption,menuID)">
    switch (menuID) {
        case 0:
            acceptAllRevisions();
            break;
        case 1:
            rejectAllRevisions();
            break;
        case 2:
            MyDoHandSign();
            break;
        case 3:
            MyAddSignFromURL();
            break;
        case 4:
            MyCheckSign();
            break;
    }
</script>

<script language="JScript" for="riseOffice"
        event="OnFileCommand(cmd,canceled)">
    if (cmd == 3) {//监听文档保存菜单按钮操作
        saveDocument('btn');//接管保存操作
        NTKO.CancelLastCommand = true;//取消默认保存操作
    } else if (cmd == 2) {//监听文档关闭菜单按钮操作
        //window.close();
        checkSaved();
        NTKO.CancelLastCommand = true;
    } else if (cmd == 4) {//监听文档另存为菜单按钮操作
        isSaveAs = true;
        var fileName = documentTitle;
        if ((NTKO.DocType == 51) || (NTKO.DocType == 52) || (NTKO.DocType == 1)) {

            NTKO.WebFileName = fileName + ".doc";
            NTKO.ShowDialog(2);
        } else {
            NTKO.Activate(true);
            var dg = NTKO.ActiveDocument.Application.FileDialog(2);
            dg.InitialFileName = fileName + ".pdf";
            dg.FilterIndex = 4;//设置保存文档的类型，属性可读写。
            if (dg.show() == -1) {
                dg.Execute();
            }
        }
        NTKO.CancelLastCommand = true;
    }
</script>

<script language="JScript" for="riseOffice"
        event="OnCustomMenuCmd(menuIndex,menuCaption,menuID)">
    switch (menuID) {
        case 1:
            acceptAllRevisions();
            break;
        case 2:
            rejectAllRevisions();
            break;
        case 3:
            MyDoHandSign();
            break;
        case 4:
            MyCheckSign();
            break;
        case 5:
            MyAddSignFromURL();
            break;
    }
</script>
<script language="JScript" for="riseOffice"
        event="OnDocumentOpened(File, Document)">
    wordcount = Document.Words.Count;
    document.getElementById("riseOffice").ActiveDocument.Saved = true;
    setMarkModify(true);//修订模式
</script>
</body>
</html>
