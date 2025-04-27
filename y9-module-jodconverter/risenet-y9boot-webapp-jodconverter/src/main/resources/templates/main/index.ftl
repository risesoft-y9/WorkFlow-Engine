<!DOCTYPE html>

<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>文件预览-演示首页</title>
    <link rel="icon" href="./favicon.ico" type="image/x-icon">
    <link rel="stylesheet" href="css/loading.css"/>
    <link rel="stylesheet" href="bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="bootstrap-table/bootstrap-table.min.css"/>
    <link rel="stylesheet" href="css/theme.css"/>
    <script type="text/javascript" src="js/jquery-3.7.1.min.js"></script>
    <script type="text/javascript" src="js/jquery.form.min.js"></script>
    <script type="text/javascript" src="bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="bootstrap-table/bootstrap-table.min.js"></script>
    <script type="text/javascript" src="js/base64.min.js"></script>
    <style>
        <#-- 删除文件密码弹窗居中 -->
        .alert {
            width: 50%;
        }

        <#-- 删除文件验证码弹窗居中 -->
        .modal {
            width: 100%;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            -ms-transform: translate(-50%, -50%);
        }
    </style>
</head>

<body>

<#-- 删除文件验证码弹窗  -->
<#if deleteCaptcha >
    <div id="deleteCaptchaModal" class="modal fade" tabindex="-1" role="dialog">
        <div class="modal-dialog modal-sm" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title">删除文件</h4>
                </div>
                <br>
                <input type="text" id="deleteCaptchaFileName" style="display: none">
                <div class="modal-body input-group">
                <span style="display: table-cell; vertical-align: middle;">
                    <img id="deleteCaptchaImg" alt="deleteCaptchaImg" src="">
                    &nbsp;&nbsp;&nbsp;&nbsp;
                </span>
                    <input type="text" id="deleteCaptchaText" class="form-control" placeholder="请输入验证码">
                </div>
                <div class="modal-footer" style="text-align: center">
                    <button type="button" id="deleteCaptchaConfirmBtn" class="btn btn-danger">确认删除</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                </div>
            </div>
        </div>
    </div>
</#if>

<!-- Fixed navbar -->
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand" href="#" target='_blank'>文件预览</a>
        </div>
        <ul class="nav navbar-nav">
            <li class="active"><a href="./index">首页</a></li>
        </ul>
    </div>
</nav>

<div class="container theme-showcase" role="main">
    <#--  接入说明  -->
    <div>
        <ol>
            <li>支持 doc, docx, xls, xlsx, xlsm, ppt, pptx, csv, tsv, dotm, xlt, xltm, dot, dotx, xlam, xla, pages 等 Office 办公文档</li>
            <li>支持 wps, dps, et, ett, wpt 等国产 WPS Office 办公文档</li>
            <li>支持 odt, ods, ots, odp, otp, six, ott, fodt, fods 等OpenOffice、LibreOffice 办公文档</li>
            <li>支持 vsd, vsdx 等 Visio 流程图文件</li>
            <li>支持 wmf, emf 等 Windows 系统图像文件</li>
            <li>支持 psd, eps 等 Photoshop 软件模型文件</li>
            <li>支持 pdf ,ofd, rtf 等文档</li>
            <li>支持 xmind 软件模型文件</li>
            <li>支持 bpmn 工作流文件</li>
            <li>支持 eml 邮件文件</li>
            <li>支持 epub 图书文档</li>
            <li>支持 obj, 3ds, stl, ply, gltf, glb, off, 3dm, fbx, dae, wrl, 3mf, ifc, brep, step, iges, fcstd, bim 等 3D 模型文件</li>
            <li>支持 dwg, dxf, dwf, iges , igs, dwt, dng, ifc, dwfx, stl, cf2, plt 等 CAD 模型文件</li>
            <li>支持 txt, xml(渲染), md(渲染), java, php, py, js, css 等所有纯文本</li>
            <li>支持 zip, rar, jar, tar, gzip, 7z 等压缩包</li>
            <li>支持 jpg, jpeg, png, gif, bmp, ico, jfif, webp 等图片预览（翻转，缩放，镜像）</li>
            <li>支持 tif, tiff 图信息模型文件</li>
            <li>支持 tga 图像格式文件</li>
            <li>支持 svg 矢量图像格式文件</li>
            <li>支持 mp3,wav,mp4,flv 等音视频格式文件</li>
            <li>支持 avi,mov,rm,webm,ts,rm,mkv,mpeg,ogg,mpg,rmvb,wmv,3gp,ts,swf 等视频格式转码预览</li>
            <li>支持 dcm 等医疗数位影像预览</li>
            <li>支持 drawio 绘图预览</li>
        </ol>
    </div>
    <#--  输入下载地址预览文件  -->
    <div class="panel panel-success">
        <div class="panel-heading">
            <h3 class="panel-title">输入下载地址预览</h3>
        </div>
        <div class="panel-body">
            <form>
                <div class="row">
                    <div class="col-md-10">
                        <div class="form-group">
                            <input type="url" class="form-control" id="_url" placeholder="请输入预览文件 url">
                        </div>
                    </div>
                    <div class="col-md-2">
                        <button id="previewByUrl" type="button" class="btn btn-success">预览</button>
                    </div>
                </div>

                <div class="alert alert-danger alert-dismissable hide" role="alert" id="previewCheckAlert">
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <strong>请输入正确的url</strong>
                </div>

            </form>
        </div>
    </div>

    <#--  预览测试  -->
    <div class="panel panel-success">
        <div class="panel-heading">
            <h3 class="panel-title">上传本地文件预览</h3>
        </div>
        <div class="panel-body">
            <#if fileUploadDisable == false>
                <form enctype="multipart/form-data" id="fileUpload">
                    <input type="file" id="file" name="file" style="float: left; margin: 0 auto; font-size:22px;" placeholder="请选择文件"/>
                    <input type="button" id="fileUploadBtn" class="btn btn-success" value=" 上 传 "/>
                </form>
            </#if>
            <table id="table" data-pagination="true"></table>
        </div>
    </div>
</div>

<div class="loading_container" style="position: fixed;">
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
<#if beian?? && beian != "default">
    <div style="display: grid; place-items: center;">
        <div>
            <a target="_blank" href="https://beian.miit.gov.cn/">${beian}</a>
        </div>
    </div>
</#if>
<script>
    <#if deleteCaptcha >
    $("#deleteCaptchaImg").click(function () {
        $("#deleteCaptchaImg").attr("src", "${baseUrl}deleteFile/captcha?timestamp=" + new Date().getTime());
    });
    $("#deleteCaptchaConfirmBtn").click(function () {
        var fileName = $("#deleteCaptchaFileName").val();
        var deleteCaptchaText = $("#deleteCaptchaText").val();
        $.get('${baseUrl}deleteFile?fileName=' + fileName + '&password=' + deleteCaptchaText, function (data) {
            if ("删除文件失败，密码错误！" === data.msg) {
                alert(data.msg);
            } else {
                $('#table').bootstrapTable("refresh", {});
                $("#deleteCaptchaText").val("");
                $("#deleteCaptchaModal").modal("hide");
            }
        });
    });

    function deleteFile(fileName) {
        $("#deleteCaptchaImg").click();
        $("#deleteCaptchaFileName").val(fileName);
        $("#deleteCaptchaText").val("");
        $("#deleteCaptchaModal").modal("show");
    }

    <#else>

    function deleteFile(fileName) {
        if (window.confirm('你确定要删除文件吗？')) {
            password = prompt("请输入默认密码:123456");
            $.ajax({
                url: '${baseUrl}deleteFile?fileName=' + fileName + '&password=' + password,
                success: function (data) {
                    if ("删除文件失败，密码错误！" === data.msg) {
                        alert(data.msg);
                    } else {
                        $("#table").bootstrapTable("refresh", {});
                    }
                }
            });
        } else {
            return false;
        }
    }

    </#if>

    function showLoadingDiv() {
        var height = window.document.documentElement.clientHeight - 1;
        $(".loading_container").css("height", height).show();
    }

    function onFileSelected() {
        var file = $("#fileSelect").val();
        $("#fileName").text(file);
    }

    function checkUrl(url) {
        //url= 协议://(ftp的登录信息)[IP|域名](:端口号)(/或?请求参数)
        var strRegex = '^((https|http|ftp)://)'//(https或http或ftp)
            + '(([\\w_!~*\'()\\.&=+$%-]+: )?[\\w_!~*\'()\\.&=+$%-]+@)?' //ftp的user@  可有可无
            + '(([0-9]{1,3}\\.){3}[0-9]{1,3}' // IP形式的URL- 3位数字.3位数字.3位数字.3位数字
            + '|' // 允许IP和DOMAIN（域名）
            + '(localhost)|'	//匹配localhost
            + '([\\w_!~*\'()-]+\\.)*' // 域名- 至少一个[英文或数字_!~*\'()-]加上.
            + '\\w+\\.' // 一级域名 -英文或数字  加上.
            + '[a-zA-Z]{1,6})' // 顶级域名- 1-6位英文
            + '(:[0-9]{1,5})?' // 端口- :80 ,1-5位数字
            + '((/?)|' // url无参数结尾 - 斜杆或这没有
            + '(/[\\w_!~*\'()\\.;?:@&=+$,%#-]+)+/?)$';//请求参数结尾- 英文或数字和[]内的各种字符
        var re = new RegExp(strRegex, 'i');//i不区分大小写
        //将url做uri转码后再匹配，解除请求参数中的中文和空字符影响
        return re.test(encodeURI(url));
    }

    $(function () {
        $('#table').bootstrapTable({
            url: 'listFiles',
            pageNumber: ${homePageNumber},//初始化加载第一页
            pageSize: ${homePageSize}, //初始化单页记录数
            pagination: ${homePagination}, //是否分页
            pageList: [5, 10, 20, 30, 50, 100, 200, 500],
            search: ${homeSearch}, //显示查询框
            columns: [{
                field: 'fileName',
                title: '文件名'
            }, {
                field: 'action',
                title: '操作',
                align: 'center',
                width: 160
            }]
        }).on('pre-body.bs.table', function (e, data) {
            // 每个data添加一列用来操作
            $(data).each(function (index, item) {
                item.action = "<a class='btn btn-success' target='_blank' href='${baseUrl}onlinePreview?url=" + encodeURIComponent(Base64.encode('${baseUrl}' + item.fileName)) + "'>预览</a>" +
                    "<a class='btn btn-danger' style='margin-left:10px;' href='javascript:void(0);' onclick='deleteFile(\"" + encodeURIComponent(Base64.encode('${baseUrl}' + item.fileName)) + "\")'>删除</a>";
            });
            return data;
        }).on('post-body.bs.table', function (e, data) {
            return data;
        });

        $('#previewByUrl').on('click', function () {
            var _url = $("#_url").val();
            if (!checkUrl(_url)) {
                $("#previewCheckAlert").addClass("show");
                window.setTimeout(function () {
                    $("#previewCheckAlert").removeClass("show");
                }, 3000);//显示的时间
                return false;
            }

            var b64Encoded = Base64.encode(_url);

            window.open('${baseUrl}onlinePreview?url=' + encodeURIComponent(b64Encoded));
        });

        $("#fileUploadBtn").click(function () {
            var filepath = $("#file").val();
            if (!checkFileSize(filepath)) {
                return false;
            }
            showLoadingDiv();
            $("#fileUpload").ajaxSubmit({
                success: function (data) {
                    // 上传完成，刷新table
                    if (1 === data.code) {
                        alert(data.msg);
                    } else {
                        $('#table').bootstrapTable('refresh', {});
                    }
                    $("#fileName").text("");
                    $(".loading_container").hide();
                },
                error: function () {
                    alert('上传失败，请联系管理员');
                    $(".loading_container").hide();
                },
                url: 'fileUpload', /*设置post提交到的页面*/
                type: "post", /*设置表单以post方法提交*/
                dataType: "json" /*设置返回值类型为文本*/
            });
        });
    });

    function checkFileSize(filepath) {
        var daxiao = "${size}";
        daxiao = daxiao.replace("MB", "");
        // console.log(daxiao)
        var maxsize = daxiao * 1024 * 1024;
        var errMsg = "上传的文件不能超过${size}喔！！！";
        var tipMsg = "您的浏览器暂不支持上传，确保上传文件不要超过${size}，建议使用IE、FireFox、Chrome浏览器";
        try {
            var filesize = 0;
            var ua = window.navigator.userAgent;
            if (ua.indexOf("MSIE") >= 1) {
                //IE
                var img = new Image();
                img.src = filepath;
                filesize = img.fileSize;
            } else {
                filesize = $("#file")[0].files[0].size; //byte
            }
            if (filesize > 0 && filesize > maxsize) {
                alert(errMsg);
                return false;
            } else if (filesize === -1) {
                alert(tipMsg);
                return false;
            }
        } catch (e) {
            alert("上传失败，请重试");
            return false;
        }
        return true;
    }
</script>
</body>
</html>
