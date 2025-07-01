var EnumOAFlag = {
    DocFromOA: 1,
    DocFromNoOA: 0
}

//记录是否用户点击OA文件的保存按钮
var EnumDocSaveFlag = {
    OADocSave: 1,
    NoneOADocSave: 0
}

//标识文档的落地模式 本地文档落地 0 ，不落地 1
var EnumDocLandMode = {
    DLM_LocalDoc: 0,
    DLM_OnlineDoc: 1
}

//加载时会执行的方法
function OnWPSWorkTabLoad(ribbonUI) {
    wps.ribbonUI = ribbonUI;
    if (typeof (wps.Enum) == "undefined") { // 如果没有内置枚举值
        wps.Enum = WPS_Enum;
    }
    OnJSWorkInit(); //初始化文档事件(全局参数,挂载监听事件)
    // setTimeout(activeTab,2000); // 激活OA助手菜单
    OpenTimerRun(OnDocSaveByAutoTimer); //启动定时备份过程
    return true;
}

//文档各类初始化工作（WPS Js环境）
function OnJSWorkInit() {
    pInitParameters(); //OA助手环境的所有配置控制的初始化过程
    AddDocumentEvent(); //挂接文档事件处理函数
}

//初始化全局参数
function pInitParameters() {
    wps.PluginStorage.setItem(constStrEnum.OADocUserSave, EnumDocSaveFlag.NoneOADocSave); //初始化，没有用户点击保存按钮

    var l_wpsUserName = wps.WpsApplication().UserName;
    wps.PluginStorage.setItem(constStrEnum.WPSInitUserName, l_wpsUserName); //在OA助手加载前，先保存用户原有的WPS应用用户名称

    wps.PluginStorage.setItem(constStrEnum.OADocCanSaveAs, false); //默认OA文档不能另存为本地
    wps.PluginStorage.setItem(constStrEnum.AllowOADocReOpen, false); //设置是否允许来自OA的文件再次被打开
    wps.PluginStorage.setItem(constStrEnum.ShowOATabDocActive, false); //设置新打开文档是否默认显示OA助手菜单Tab  默认为false

    wps.PluginStorage.setItem(constStrEnum.DefaultUploadFieldName, "file"); //针对UploadFile方法设置上载字段名称

    wps.PluginStorage.setItem(constStrEnum.AutoSaveToServerTime, "10"); //自动保存回OA服务端的时间间隔。如果设置0，则关闭，最小设置3分钟
    wps.PluginStorage.setItem(constStrEnum.TempTimerID, "0"); //临时值，用于保存计时器ID的临时值

    // 以下是一些临时状态参数，用于打开文档等的状态判断
    wps.PluginStorage.setItem(constStrEnum.IsInCurrOADocOpen, false); //用于执行来自OA端的新建或打开文档时的状态
    wps.PluginStorage.setItem(constStrEnum.IsInCurrOADocSaveAs, false); //用于执行来自OA端的文档另存为本地的状态
    wps.PluginStorage.setItem(constStrEnum.RevisionEnableFlag, false) //按钮的标记控制
    wps.PluginStorage.setItem(constStrEnum.Save2OAShowConfirm, true); //弹出上传成功后的提示信息
}

//挂载WPS的文档事件
function AddDocumentEvent() {
    wps.ApiEvent.AddApiEventListener("WindowActivate", OnWindowActivate);
    wps.ApiEvent.AddApiEventListener("DocumentBeforeSave", OnDocumentBeforeSave);
    wps.ApiEvent.AddApiEventListener("DocumentBeforeClose", OnDocumentBeforeClose);
    wps.ApiEvent.AddApiEventListener("DocumentAfterClose", OnDocumentAfterClose);
    wps.ApiEvent.AddApiEventListener("DocumentBeforePrint", OnDocumentBeforePrint);
    wps.ApiEvent.AddApiEventListener("DocumentOpen", OnDocumentOpen);
    wps.ApiEvent.AddApiEventListener("DocumentNew", OnDocumentNew);

    console.log("AddDocumentEvent");
}


/**
 * 打开插入书签页面
 */
function OnInsertBookmarkToDoc() {
    if (!wps.WpsApplication().ActiveDocument) {
        return;
    }
    OnShowDialog("selectBookmark.html", "自定义书签", 700, 440, false);
}

/**
 * 作用：打开当前文档的页面设置对话框
 */
function OnPageSetupClicked() {
    var wpsApp = wps.WpsApplication();
    var doc = wpsApp.ActiveDocument;
    if (!doc) {
        return;
    }
    wpsApp.Dialogs.Item(wps.Enum && wps.Enum.wdDialogFilePageSetup || 178).Show();
}

/**
 * 作用：打开当前文档的打印设置对话框
 */
function OnPrintDocBtnClicked() {
    var wpsApp = wps.WpsApplication();
    var doc = wpsApp.ActiveDocument;
    if (!doc) {
        return;
    }
    wpsApp.Dialogs.Item(wps.Enum && wps.Enum.wdDialogFilePrint || 88).Show();
}


/**
 * 作用：接受所有修订内容
 *
 */
function OnAcceptAllRevisions() {
    //获取当前文档对象
    var l_Doc = wps.WpsApplication().ActiveDocument;
    if (!l_Doc) {
        return;
    }
    if (l_Doc.Revisions.Count >= 1) {
        if (!wps.confirm("目前有" + l_Doc.Revisions.Count + "个修订信息，是否全部接受？")) {
            return;
        }
        l_Doc.AcceptAllRevisions();
    }
}


/**
 * 作用：拒绝当前文档的所有修订内容
 */
function OnRejectAllRevisions() {
    var l_Doc = wps.WpsApplication().ActiveDocument;
    if (!l_Doc) {
        return;
    }
    if (l_Doc.Revisions.Count >= 1) {
        l_Doc.RejectAllRevisions();
    }
}


/**
 *  作用：把当前文档修订模式关闭
 */
function OnCloseRevisions() {
    //获取当前文档对象
    var l_Doc = wps.WpsApplication().ActiveDocument;
    OnRevisionsSwitch(l_Doc, false);
}


/**
 *  作用：把当前文档修订模式打开
 */
function OnOpenRevisions() {
    //获取当前文档对象
    var l_Doc = wps.WpsApplication().ActiveDocument;
    OnRevisionsSwitch(l_Doc, true);
}

function OnRevisionsSwitch(doc, openRevisions) {
    if (!doc) {
        return;
    }
    var l_activeWindow = doc.ActiveWindow;
    if (l_activeWindow) {
        var v = l_activeWindow.View;
        if (v) {
            //WPS 显示使用“修订”功能对文档所作的修订和批注
            v.ShowRevisionsAndComments = openRevisions;
            //WPS 显示从文本到修订和批注气球之间的连接线
            v.RevisionsBalloonShowConnectingLines = openRevisions;
        }
        if (openRevisions == true) {
            //去掉修改痕迹信息框中的接受修订和拒绝修订勾叉，使其不可用
            wps.WpsApplication().CommandBars.ExecuteMso("KsoEx_RevisionCommentModify_Disable");
        }

        //RevisionsMode:
        //wdBalloonRevisions	0	在左边距或右边距的气球中显示修订。
        //wdInLineRevisions	1	在正文中显示修订，使用删除线表示删除，使用下划线表示插入。 
        //                      这是 Word 早期版本的默认设置。
        //wdMixedRevisions	2	不支持。
        doc.TrackRevisions = openRevisions; // 开关修订
        l_activeWindow.ActivePane.View.RevisionsMode = 2; //2为不支持气球显示。

    }
}

/**
 *  作用：打开扫描仪
 */
function OnOpenScanBtnClicked() {
    var doc = wps.WpsApplication().ActiveDocument;
    if (!doc) {
        return;
    }
    //打开扫描仪
    try {
        wps.WpsApplication().WordBasic.InsertImagerScan(); //打开扫描仪
    } catch (err) {
        alert("打开扫描仪的过程遇到问题。");
    }
}

/**
 *  作用：在文档的当前光标处插入从前端传递来的图片
 *  OA参数中 picPath 是需要插入的图片路径
 *  图片插入的默认版式是在浮于文档上方
 */
function DoInsertPicToDoc() {
    console.log("DoInsertPicToDoc...");

    var l_doc; //文档对象
    l_doc = wps.WpsApplication().ActiveDocument;
    if (!l_doc) {
        return;
    }

    //获取当前文档对象对应的OA参数
    var l_picPath = GetDocParamsValue(l_doc, constStrEnum.picPath); // 获取OA参数传入的图片路径
    if (l_picPath == "") {
        // alert("未获取到系统传入的图片URL路径，不能正常插入图片");
        // return;
        //如果没有传，则默认写一个图片地址
        l_picPath = "http://127.0.0.1:3888/file/OA模板公章.png"
    }

    var l_picHeight = GetDocParamsValue(l_doc, constStrEnum.picHeight); //图片高
    var l_picWidth = GetDocParamsValue(l_doc, constStrEnum.picWidth); //图片宽

    if (l_picHeight == "") { //设定图片高度
        l_picHeight = 39.117798; //13.8mm=39.117798磅
    }
    if (l_picWidth == "") { //设定图片宽度
        l_picWidth = 72; //49.7mm=140.880768磅
    }

    var l_shape = l_doc.Shapes.AddPicture(l_picPath, false, true);
    l_shape.Select();
    // l_shape.WrapFormat.Type = wps.Enum&&wps.Enum.wdWrapBehind||5; //图片的默认版式为浮于文字上方，可通过此设置图片环绕模式
}

/**
 * 作用：模拟插入签章图片
 * @param {*} doc 文档对象
 * @param {*} picPath 图片路径
 * @param {*} picWidth 图片宽度
 * @param {*} picHeight 图片高度
 */
function OnInsertPicToDoc(doc, picPath, picWidth, picHeight, callBack) {
    // alert("图片路径："+picPath);
    if (!doc) {
        return;
    }
    if (typeof picPath == "undefined" || picPath == null || picPath == "") {
        alert("未获取到系统传入的图片URL路径，不能正常插入印章");
        return;
    }
    if (!picWidth) { //设定图片宽度
        picWidth = 95; //49.7mm=140.880768磅
    }
    if (!picHeight) { //设定图片高度
        picHeight = 40; //13.8mm=39.117798磅
    }

    var selection = doc.ActiveWindow.Selection; // 活动窗口选定范围或插入点
    var pagecount = doc.BuiltInDocumentProperties.Item(wps.Enum && wps.Enum.wdPropertyPages || 14); //获取文档页数
    selection.GoTo(wps.Enum && wps.Enum.wdGoToPage || 1, wps.Enum && wps.Enum.wdGoToPage || 1, pagecount.Value); //将光标指向文档最后一页
    DownloadFile(picPath, function (url) {
        selection.ParagraphFormat.LineSpacing = 12 //防止文档设置了固定行距
        var picture = selection.InlineShapes.AddPicture(url, true, true); //插入图片
        wps.FileSystem.Remove(url) //删除本地的图片
        picture.LockAspectRatio = 0; //在调整形状大小时可分别改变其高度和宽度
        picture.Height = picHeight; //设定图片高度
        picture.Width = picWidth; //设定图片宽度
        picture.LockAspectRatio = 0;
        picture.Select(); //当前图片为焦点

        //定义印章图片对象
        var seal_shape = picture.ConvertToShape(); //类型转换:嵌入型图片->粘贴版型图片

        seal_shape.RelativeHorizontalPosition = wps.Enum && wps.Enum.wdRelativeHorizontalPositionPage || 1;
        seal_shape.RelativeVerticalPosition = wps.Enum && wps.Enum.wdRelativeVerticalPositionPage || 1;
        seal_shape.Left = 315; //设置指定形状或形状范围的垂直位置（以磅为单位）。
        seal_shape.Top = 630; //指定形状或形状范围的水平位置（以磅为单位）。
        callBack && callBack()
    })

}


/**
 * 作用： 把当前文档保存为其他格式的文档并上传
 * @param {*} p_FileSuffix 输出的目标格式后缀名，支持：.pdf .uof .uot .ofd
 * @param {*} pShowPrompt 是否弹出用户确认框
 */
function OnDoChangeToOtherDocFormat(p_FileSuffix, pShowPrompt) {
    var l_suffix = p_FileSuffix; // params.suffix;
    if (!l_suffix) {
        return;
    }
    //获取当前执行格式转换操作的文档
    var l_doc = wps.WpsApplication().ActiveDocument;
    if (!l_doc) {
        return;
    }
    console.log(pShowPrompt)
    if (typeof (pShowPrompt) == "undefined") {
        pShowPrompt = true; //默认设置为弹出用户确认框
    }
    //默认设置为以当前文件的显示模式输出，即当前为修订则输出带有修订痕迹的
    pDoChangeToOtherDocFormat(l_doc, l_suffix, pShowPrompt, true);
}

/**
 * 作用：把当前文档输出为另外的格式保存
 * @param {*} p_Doc 文档对象
 * @param {*} p_Suffix 另存为的目标文件格式
 * @param {*} pShowPrompt 是否弹出用户确认框
 * @param {*} p_ShowRevision :是否强制关闭修订，如果是False，则强制关闭痕迹显示。如果为true则不做控制输出。
 */
function pDoChangeToOtherDocFormat(p_Doc, p_Suffix, pShowPrompt, p_ShowRevision) {
    if (!p_Doc) {
        return;
    }

    var l_suffix = p_Suffix;
    //获取该文档对应OA参数的上载路径
    var l_uploadPath = GetDocParamsValue(p_Doc, constStrEnum.uploadAppendPath);
    if (l_uploadPath == "" || l_uploadPath == null) {
        l_uploadPath = GetDocParamsValue(p_Doc, constStrEnum.uploadPath);
    }
    var l_FieldName = GetDocParamsValue(p_Doc, constStrEnum.uploadFieldName);
    if (l_FieldName == "") {
        l_FieldName = wps.PluginStorage.getItem(constStrEnum.DefaultUploadFieldName); //默认是'file'
    }

    if (l_uploadPath == "" && pShowPrompt == true) {
        alert("系统未传入有效上载文件路径！不能继续转换操作。"); //如果OA未传入上载路径，则给予提示
        return;
    }

    if (pShowPrompt == true) {
        if (!wps.confirm("当前文档将另存一份" + l_suffix + " 格式的副本，并上传到系统后台，请确认 ？")) {
            return;
        }
    }

    // 先把文档输出保存为指定的文件格式，再上传到后台
    wps.PluginStorage.setItem(constStrEnum.OADocUserSave, true); //设置一个临时变量，用于在BeforeSave事件中判断 
    if (p_ShowRevision == false) { // 强制关闭痕迹显示
        var l_SourceName = p_Doc.FullName;
        var l_NewName = "";
        if (p_Doc.Path.indexOf("\\") > 0) {
            l_NewName = p_Doc.Path + "\\B_" + p_Doc.Name;
        } else {
            l_NewName = p_Doc.Path + "/B_" + p_Doc.Name;
        }
        p_Doc.SaveAs2($FileName = l_NewName, $AddToRecentFiles = false);
        p_Doc.SaveAs2($FileName = l_SourceName, $AddToRecentFiles = false);

        //以下以隐藏模式打开另一个文档
        var l_textEncoding = wps.WpsApplication().Options.DefaultTextEncoding; //默认 936
        var l_Doc = wps.WpsApplication().Documents.Open(l_NewName, false, false, false, "", "", false, "", "", 0, l_textEncoding, false);

        l_Doc.TrackRevision = false; //关闭修订模式
        l_Doc.ShowRevision = false; //隐含属性，隐藏修订模式
        l_Doc.AcceptAllRevisions();
        l_Doc.Save();
        handleFileAndUpload(l_suffix, l_Doc, l_uploadPath, l_FieldName);
        l_Doc.Close();
        wps.FileSystem.Remove(l_NewName); //删除临时文档
    } else {
        handleFileAndUpload(l_suffix, p_Doc, l_uploadPath, l_FieldName);
    }

    wps.PluginStorage.setItem(constStrEnum.OADocUserSave, false);

    return;
}

/**
 * 把文档转换成UOT在上传
 */
function OnDoChangeToUOF() {
}

/**
 *  打开WPS云文档的入口
 */
function pDoOpenWPSCloundDoc() {
    wps.TabPages.Add("https://www.kdocs.cn");
}

/**
 *  执行另存为本地文件操作
 */
function OnBtnSaveAsLocalFile() {

    //初始化临时状态值
    wps.PluginStorage.setItem(constStrEnum.OADocUserSave, false);
    wps.PluginStorage.setItem(constStrEnum.IsInCurrOADocSaveAs, false);

    //检测是否有文档正在处理
    var l_doc = wps.WpsApplication().ActiveDocument;
    if (!l_doc) {
        alert("WPS当前没有可操作文档！");
        return;
    }

    // 设置WPS文档对话框 2 FileDialogType:=msoFileDialogSaveAs
    var l_ksoFileDialog = wps.WpsApplication().FileDialog(2);
    l_ksoFileDialog.InitialFileName = l_doc.Name; //文档名称

    if (l_ksoFileDialog.Show() == -1) { // -1 代表确认按钮
        //alert("确认");
        wps.PluginStorage.setItem(constStrEnum.OADocUserSave, true); //设置保存为临时状态，在Save事件中避免OA禁止另存为对话框
        l_ksoFileDialog.Execute(); //会触发保存文档的监听函数

        pSetNoneOADocFlag(l_doc);

        wps.ribbonUI.Invalidate(); //刷新Ribbon的状态

    }
    ;
}

//
/**
 * 作用：执行清稿按钮操作
 * 业务功能：清除所有修订痕迹和批注
 */
function OnBtnClearRevDoc() {
    var doc = wps.WpsApplication().ActiveDocument;
    if (!doc) {
        alert("尚未打开文档，请先打开文档再进行清稿操作！");
    }

    //执行清稿操作前，给用户提示
    if (!wps.confirm("清稿操作将接受所有的修订内容，关闭修订显示。请确认执行清稿操作？")) {
        return;
    }

    //接受所有修订
    if (doc.Revisions.Count >= 1) {
        doc.AcceptAllRevisions();
    }
    //去除所有批注
    if (doc.Comments.Count >= 1) {
        doc.RemoveDocumentInformation(wps.Enum && wps.Enum.wdRDIComments || 1);
    }

    //删除所有ink墨迹对象
    pDeleteAllInkObj(doc);

    doc.TrackRevisions = false; //关闭修订模式
    wps.ribbonUI.InvalidateControl("btnOpenRevision");

    return;
}

/**
 * 作用：删除当前文档的所有墨迹对象
 * @param {*} p_Doc
 */
function pDeleteAllInkObj(p_Doc) {
    var l_Count = 0;
    var l_IsInkObjExist = true;
    while (l_IsInkObjExist == true && l_Count < 20) {
        l_IsInkObjExist = pDeleteInkObj(p_Doc);
        l_Count++;
    }
    return;
}

/**
 * 删除墨迹对象
 */
function pDeleteInkObj(p_Doc) {
    var l_IsInkObjExist = false;
    if (p_Doc) {
        for (var l_Index = 1; l_Index <= p_Doc.Shapes.Count; l_Index++) {
            var l_Item = p_Doc.Shapes.Item(l_Index);
            if (l_Item.Type == 23) {
                l_Item.Delete();
                //只要有一次找到Ink类型，就标识一下
                if (l_IsInkObjExist == false) {
                    l_IsInkObjExist = true;
                }
            }
        }
    }
    return l_IsInkObjExist;
}


/**
 *
 */
function pSaveAnotherDoc(p_Doc) {
    if (!p_Doc) {
        return;
    }
    var l_SourceDocName = p_Doc.Name;
    var l_NewName = "BK_" + l_SourceDocName;
    p_Doc.SaveAs2(l_NewName);
    wps.WpsApplication().Documents.Open();
}


//保存到OA后台服务器
function OnBtnSaveToServer() {
    // console.log('SaveToServer');
    var l_doc = wps.WpsApplication().ActiveDocument;
    if (!l_doc) {
        alert("空文档不能保存！");
        return;
    }

    //非OA文档，不能上传到OA
    if (pCheckIfOADoc() == false) {
        alert("非系统打开的文档，不能直接上传到系统！");
        return;
    }

    //如果是OA打开的文档，并且设置了保护的文档，则不能再上传到OA服务器
    if (pISOADocReadOnly(l_doc)) {
        wps.alert("系统设置了保护的文档，不能再提交到系统后台。");
        return;
    }
    console.log(constStrEnum)
    /**
     * 参数定义：OAAsist.UploadFile(name, path, url, field,  "OnSuccess", "OnFail")
     * 上传一个文件到远程服务器。
     * name：为上传后的文件名称；
     * path：是文件绝对路径；
     * url：为上传地址；
     * field：为请求中name的值；
     * 最后两个参数为回调函数名称；
     */
    var l_uploadPath = GetDocParamsValue(l_doc, constStrEnum.uploadPath); // 文件上载路径
    if (l_uploadPath == "") {
        wps.alert("系统未传入文件上载路径，不能执行上传操作！");
        return;
    }

    var l_showConfirm = wps.PluginStorage.getItem(constStrEnum.Save2OAShowConfirm)
    //if (l_showConfirm) {
    //if (!wps.confirm("先保存文档，并开始上传到系统后台，请确认？")) {
    // return;
    //}
    // }

    var l_FieldName = GetDocParamsValue(l_doc, constStrEnum.uploadFieldName); //上载到后台的业务方自定义的字段名称
    if (l_FieldName == "") {
        l_FieldName = wps.PluginStorage.getItem(constStrEnum.DefaultUploadFieldName); // 默认为‘file’
    }
    var l_UploadName = GetDocParamsValue(l_doc, constStrEnum.uploadFileName); //设置OA传入的文件名称参数
    if (l_UploadName == "") {
        l_UploadName = l_doc.Name; //默认文件名称就是当前文件编辑名称
    }

    var l_DocPath = l_doc.FullName; // 文件所在路径
    if (pIsOnlineOADoc(l_doc) == false) {
        //对于本地磁盘文件上传OA，先用Save方法保存后，再上传
        //设置用户保存按钮标志，避免出现禁止OA文件保存的干扰信息
        wps.PluginStorage.setItem(constStrEnum.OADocUserSave, EnumDocSaveFlag.OADocSave);
        l_doc.Save(); //执行一次保存方法
        //设置用户保存按钮标志
        wps.PluginStorage.setItem(constStrEnum.OADocUserSave, EnumDocSaveFlag.NoneOADocSave);
        //落地文档，调用UploadFile方法上传到OA后台
        try {
            //调用OA助手的上传方法
            UploadFile(l_UploadName, l_DocPath, l_uploadPath, l_FieldName, OnUploadToServerSuccess, OnUploadToServerFail);
        } catch (err) {
            alert("上传文件失败！请检查系统上传参数及网络环境！");
        }
    } else {
        // 不落地的文档，调用 Document 对象的不落地上传方法
        wps.PluginStorage.setItem(constStrEnum.OADocUserSave, EnumDocSaveFlag.OADocSave);
        try {
            //调用不落地上传方法
            l_doc.SaveAsUrl(l_UploadName, l_uploadPath, l_FieldName, "OnUploadToServerSuccess", "OnUploadToServerFail");
        } catch (err) {
            alert("上传文件失败！请检查系统上传参数及网络环境，重新上传。");
        }
        wps.PluginStorage.setItem(constStrEnum.OADocUserSave, EnumDocSaveFlag.NoneOADocSave);
    }

    //获取OA传入的 转其他格式上传属性
    var l_suffix = GetDocParamsValue(l_doc, constStrEnum.suffix);
    if (l_suffix == "") {
        console.log("上传需转换的文件后缀名错误，无法进行转换上传!");
        return;
    }

    //判断是否同时上传PDF等格式到OA后台
    var l_uploadWithAppendPath = GetDocParamsValue(l_doc, constStrEnum.uploadWithAppendPath); //标识是否同时上传suffix格式的文档
    if (l_uploadWithAppendPath == "1") {
        //调用转pdf格式函数，强制关闭转换修订痕迹，不弹出用户确认的对话框
        pDoChangeToOtherDocFormat(l_doc, l_suffix, false, false);
    }
    return;
}


/**
 * 作用：套红头
 * 所有与OA系统相关的业务功能，都放在oabuss 子目录下
 */
function OnInsertRedHeaderClick() {
    var l_Doc = wps.WpsApplication().ActiveDocument;
    if (!l_Doc) {
        return;
    }
    var bookMarks = l_Doc.Bookmarks;
    if (bookMarks.Item("quanwen")) { // 当前文档存在"quanwen"书签时候表示已经套过红头
        alert("当前文档已套过红头，请勿重复操作!");
        return;
    }
    var l_insertFileUrl = GetDocParamsValue(l_Doc, constStrEnum.insertFileUrl); //插入文件的位置
    //var l_BkFile = GetDocParamsValue(l_Doc, constStrEnum.bkInsertFile);
    //if (l_BkFile == "" || l_insertFileUrl == "") {
    var height = 250;
    var width = 400;
    OnShowDialog("redhead.html", "OA助手", width, height);
    return;
    //}
    //InsertRedHeadDoc(l_Doc);
}


/**
 * 作用：撤销红头
 * 所有与OA系统相关的业务功能，都放在oabuss 子目录下
 */
function OnRevokeRedHeaderClick() {
    var l_Doc = wps.WpsApplication().ActiveDocument;
    if (!l_Doc) {
        return;
    }
    var bookMarks = l_Doc.Bookmarks;
    if (bookMarks.Item("quanwen")) { // 当前文档存在"quanwen"书签时候表示已经套过红头
        var wpsApp = wps.WpsApplication();
        var selection = wpsApp.ActiveWindow.Selection;
        var revokeRedHeaderPath = GetDocParamsValue(l_Doc, "revokeRedHeaderPath"); // 获取OA参数传入的撤销套红路径
        var openType = GetDocParamsValue(l_Doc, "openType");
        l_Doc.Unprotect(openType.password);//取消文档保护
        l_Doc.TrackRevisions = false;
        selection.WholeStory(); //选取全文
        selection.Cut();
        selection.InsertFile(revokeRedHeaderPath);
    }
}

/**
 * 插入时间
 * params参数结构
 * params:{
 *
 * }
 */
function OnInsertDateClicked() {
    var l_Doc = wps.WpsApplication().ActiveDocument;
    if (l_Doc) {
        //打开插入日期对话框
        wps.WpsApplication().Dialogs.Item(wps.Enum && wps.Enum.wdDialogInsertDateTime || 165).Show();
    }
}


/**
 * 调用文件上传到OA服务端时，
 * @param {*} resp
 */
function OnUploadToServerSuccess(resp) {
    console.log("成功上传服务端后的回调：" + resp)
    console.log(resp)
    var l_doc = wps.WpsApplication().ActiveDocument;
    var l_showConfirm = wps.PluginStorage.getItem(constStrEnum.Save2OAShowConfirm);
    if (l_showConfirm) {
        if (wps.confirm("文件上传成功！继续编辑请确认，取消关闭文档。") == false) {
            if (l_doc) {
                console.log("OnUploadToServerSuccess: before Close");
                l_doc.Close(-1); //保存文档后关闭
                console.log("OnUploadToServerSuccess: after Close");
            }
        }
    }

    var l_NofityURL = GetDocParamsValue(l_doc, constStrEnum.notifyUrl);
    if (l_NofityURL != "") {
        l_NofityURL = l_NofityURL.replace("{?}", "2"); //约定：参数为2则文档被成功上传
        NotifyToServer(l_NofityURL);
    }
}

function OnUploadToServerFail(resp) {
    alert("文件上传失败！");
}

function OnbtnTabClick() {
    alert("OnbtnTabClick");
}


//判断当前文档是否是OA文档
function pCheckIfOADoc() {
    var doc = wps.WpsApplication().ActiveDocument;
    if (!doc)
        return false;
    return CheckIfDocIsOADoc(doc);
}

//根据传入的doc对象，判断当前文档是否是OA文档
function CheckIfDocIsOADoc(doc) {
    if (!doc) {
        return false;
    }

    var l_isOA = GetDocParamsValue(doc, constStrEnum.isOA);
    if (l_isOA == "") {
        return false
    }
    ;

    return l_isOA == EnumOAFlag.DocFromOA ? true : false;
}

//获取文件来源标识
function pGetDocSourceLabel() {
    return pCheckIfOADoc() ? "OA文件" : "非OA文件";
}


/**
 *  作用：设置用户名称标签
 */
function pSetUserNameLabelControl() {
    var l_doc = wps.WpsApplication().ActiveDocument;
    if (!l_doc) return "";

    var l_strUserName = "";
    if (pCheckIfOADoc() == true) { // OA文档，获取OA用户名
        var userName = GetDocParamsValue(l_doc, constStrEnum.userName);
        l_strUserName = userName == "" ? "未设置" : userName;
    } else {
        //非OA传来的文档，则按WPS安装后设置的用户名显示
        l_strUserName = "" + wps.PluginStorage.getItem(constStrEnum.WPSInitUserName);
    }

    return l_strUserName;
}


/**
 * 作用：判断是否是不落地文档
 * 参数：doc 文档对象
 * 返回值： 布尔值
 */
function pIsOnlineOADoc(doc) {
    var l_LandMode = GetDocParamsValue(doc, constStrEnum.OADocLandMode); //获取文档落地模式
    if (l_LandMode == "") { //用户本地打开的文档
        return false;
    }
    return l_LandMode == EnumDocLandMode.DLM_OnlineDoc;
}

/**
 *  作用：返回OA文档落地模式标签
 */
function pGetOADocLabel() {
    var l_Doc = wps.WpsApplication().ActiveDocument;
    if (!l_Doc) {
        return "";
    }

    var l_strLabel = ""; //初始化

    if (pIsOnlineOADoc(l_Doc) == true) { // 判断是否为不落地文档
        l_strLabel = "文档状态：不落地";
    } else {
        l_strLabel = l_Doc.Path != "" ? "文档状态：落地" : "文档状态：未保存";
    }

    //判断OA文档是否是受保护
    if (pISOADocReadOnly(l_Doc) == true) {
        l_strLabel = l_strLabel + "(保护)";
    }
    return l_strLabel;
}

//返回是否可以点击OA保存按钮的状态
function OnSetSaveToOAEnable() {
    return pCheckIfOADoc();
}


/**
 *  作用：根据OA传入参数，设置是否显示Ribbob按钮组
 *  参数：CtrlID 是OnGetVisible 传入的Ribbob控件的ID值
 */
function pShowRibbonGroupByOADocParam(CtrlID) {
    var l_Doc = wps.WpsApplication().ActiveDocument;
    if (!l_Doc) {
        return false; //如果未装入文档，则设置OA助手按钮组不可见
    }

    //获取OA传入的按钮组参数组
    var l_grpButtonParams = GetDocParamsValue(l_Doc, constStrEnum.buttonGroups); //disableBtns
    l_grpButtonParams = l_grpButtonParams + "," + GetDocParamsValue(l_Doc, constStrEnum.disableBtns);


    // 要求OA传入控制自定义按钮显示的参数为字符串 中间用 , 分隔开
    if (typeof (l_grpButtonParams) == "string") {
        var l_arrayGroup = new Array();
        l_arrayGroup = l_grpButtonParams.split(",");
        //console.log(l_grpButtonParams);

        // 判断当前按钮是否存在于数组
        if (l_arrayGroup.indexOf(CtrlID) >= 0) {
            return false;
        }
    }
    // 添加OA菜单判断
    if (CtrlID == "WPSWorkExtTab") {
        var l_value = wps.PluginStorage.getItem(constStrEnum.ShowOATabDocActive);
        wps.PluginStorage.setItem(constStrEnum.ShowOATabDocActive, false); //初始化临时状态变量
        console.log("菜单：" + l_value);
        return l_value;
    }

    //disableBtns

    return true;
}

/**
 * 根据传入Document对象，获取OA传入的参数的某个Key值的Value
 * @param {*} Doc
 * @param {*} Key
 * 返回值：返回指定 Key的 Value
 */
function GetDocParamsValue(Doc, Key) {
    if (!Doc) {
        return "";
    }

    var l_Params = wps.PluginStorage.getItem(Doc.DocID);
    if (!l_Params) {
        return "";
    }

    var l_objParams = JSON.parse(l_Params);
    if (typeof (l_objParams) == "undefined") {
        return "";
    }

    var l_rtnValue = l_objParams[Key];
    if (typeof (l_rtnValue) == "undefined" || l_rtnValue == null) {
        return "";
    }
    return l_rtnValue;
}

/**
 *
 * @param {*} params
 * @param {*} Key
 */
function GetParamsValue(Params, Key) {
    if (typeof (Params) == "undefined") {
        return "";
    }

    var l_rtnValue = Params[Key];
    return l_rtnValue;
}


/**
 *  作用：插入二维码图片
 */
function OnInsertQRCode() {
    OnShowDialog("QRCode.html", "插入二维码", 400, 400);
}


/**
 *   打开本地文档并插入到当前文档中指定位置（导入文档）
 */
function OnOpenLocalFile() {
    OpenLocalFile();
}


/**
 * 插入电子印章的功能
 */
function OnInsertSeal() {
    OnShowDialog("selectSeal.html", "印章", 730, 500);
}

/**
 * 导入模板到文档中
 */
function OnImportTemplate() {
    OnShowDialog("importTemplate.html", "导入模板", 560, 400);
}


//自定义菜单按钮的点击执行事件
function OnAction(control) {
    var eleId;
    if (typeof control == "object" && arguments.length == 1) { //针对Ribbon的按钮的
        eleId = control.Id;
    } else if (typeof control == "undefined" && arguments.length > 1) { //针对idMso的
        eleId = arguments[1].Id;
        console.log(eleId)
    } else if (typeof control == "boolean" && arguments.length > 1) { //针对checkbox的
        eleId = arguments[1].Id;
    } else if (typeof control == "number" && arguments.length > 1) { //针对combox的
        eleId = arguments[2].Id;
    }
    switch (eleId) {
        case "btnOpenWPSYUN": //打开WPS云文档入口
            pDoOpenWPSCloundDoc();
            break;
        case "btnOpenLocalWPSYUN": //打开本地文档并插入到文档中
            OnOpenLocalFile();
            break;
        case "WPSWorkExtTab":
            OnbtnTabClick();
            break;
        case "btnSaveToServer": //保存到OA服务器
            wps.PluginStorage.setItem(constStrEnum.Save2OAShowConfirm, true)
            OnBtnSaveToServer();
            break;
        case "btnSaveAsFile": //另存为本地文件
            OnBtnSaveAsLocalFile();
            break;
        case "btnChangeToPDF": //转PDF文档并上传
            OnDoChangeToOtherDocFormat(".pdf");
            break;
        case "btnChangeToUOT": //转UOF文档并上传
            OnDoChangeToOtherDocFormat(".uof");
            break;
        case "btnChangeToOFD": //转OFD文档并上传
            OnDoChangeToOtherDocFormat(".ofd");
            break;
        //------------------------------------
        case "btnInsertRedHeader": //插入红头
            OnInsertRedHeaderClick(); //套红头功能
            break;
        case "btnRevokeRedHeader": //撤销红头
            OnRevokeRedHeaderClick(); //撤销红头
            break;
        case "btnUploadOABackup": //文件备份
            OnUploadOABackupClicked();
            break;
        case "btnInsertSeal": //插入印章
            OnInsertSeal();
            break;
        //------------------------------------
        //修订按钮组
        case "btnClearRevDoc": //执行 清稿 按钮
            OnBtnClearRevDoc();
            break;
        case "btnOpenRevision": //打开修订
        {
            let bFlag = wps.PluginStorage.getItem(constStrEnum.RevisionEnableFlag)
            wps.PluginStorage.setItem(constStrEnum.RevisionEnableFlag, !bFlag)
            //通知wps刷新以下几个按钮的状态
            wps.ribbonUI.InvalidateControl("btnOpenRevision")
            wps.ribbonUI.InvalidateControl("btnCloseRevision")
            OnOpenRevisions(); //
            break;
        }
        case "btnCloseRevision": //关闭修订
        {
            let bFlag = wps.PluginStorage.getItem(constStrEnum.RevisionEnableFlag)
            wps.PluginStorage.setItem(constStrEnum.RevisionEnableFlag, !bFlag)
            //通知wps刷新以下几个按钮的状态
            wps.ribbonUI.InvalidateControl("btnOpenRevision")
            wps.ribbonUI.InvalidateControl("btnCloseRevision")
            OnCloseRevisions();
            break;
        }
        case "btnShowRevision":
            break;
        case "btnAcceptAllRevisions": //接受所有修订功能
            OnAcceptAllRevisions();
            break;
        case "btnRejectAllRevisions": //拒绝修订
            OnRejectAllRevisions();
            break;
        //------------------------------------
        case "btnInsertPic": //插入图片
            DoInsertPicToDoc();
            break;
        case "btnInsertDate": //插入日期
            OnInsertDateClicked();
            break;
        case "btnOpenScan": //打开扫描仪
            OnOpenScanBtnClicked();
            break;
        case "btnPageSetup": //打开页面设置
            OnPageSetupClicked();
            break;
        case "btnQRCode": //插入二维码
            OnInsertQRCode(); //
            break;
        case "btnPrintDOC": // 打开打印设置
            OnPrintDocBtnClicked();
            break;
        case "lblDocSourceValue": //OA公文提示
            OnOADocInfo();
            break;
        case "btnUserName": //点击用户
            OnUserNameSetClick();
            break;
        case "btnInsertBookmark": //插入书签
            OnInsertBookmarkToDoc();
            break;
        case "btnImportTemplate": //导入模板
            OnImportTemplate();
            break;
        // case "FileSaveAsMenu": //通过idMso进行「另存为」功能的自定义
        // case "FileSaveAs":
        case "FileSave": //通过idMso进行「保存」功能的自定义
        {
            if (pCheckIfOADoc()) { //文档来源是业务系统的，做自定义
                console.log("这是OA文档，将Ctrl+S动作做了重定义，可以调用OA的保存文件流到业务系统的接口。")
                OnBtnSaveToServer();
            } else { //本地的文档，期望不做自定义，通过转调idMso的方法实现
                // wps.WpsApplication().CommandBars.ExecuteMso("FileSave");
                wps.WpsApplication().CommandBars.ExecuteMso("SaveAll");
                //此处一定不能去调用与重写idMso相同的ID，否则就是个无线递归了，即在这个场景下不可调用FileSaveAs和FileSaveAsMenu这两个方法
            }
            break;
        }
        case "FileNew":
        case "FileNewMenu":
        case "WindowNew":
        case "FileNewBlankDocument": {
            if (pCheckIfOADoc()) { //文档来源是业务系统的，做自定义
                alert("这是OA文档，将Ctrl+N动作做了禁用")
            }
        }
        case "ShowAlert_ContextMenuText": {
            let selectText = wps.WpsApplication().Selection.Text;
            alert("您选择的内容是：\n" + selectText);
            break;
        }
        case "btnSendMessage1": {
            /**
             * 内部封装了主动响应前端发送的请求的方法
             */
                //参数自定义，这里只是负责传递参数，在WpsInvoke.RegWebNotify方法的回调函数中去做接收，自行解析参数
            let params = {
                    type: 'executeFunc1',
                    message: "当前时间为：" + currentTime()
                }
            /**
             * WebNotify:
             * 参数1：发送给业务系统的消息
             * 参数2：是否将消息加入队列，是否防止丢失消息，都需要设置为true
             */
            wps.OAAssist.WebNotify(JSON.stringify(params), true); //如果想传一个对象，则使用JSON.stringify方法转成对象字符串。
            break;
        }
        case "btnSendMessage2": {
            /**
             * 内部封装了主动响应前端发送的请求的方法
             */
            let msgInfo =
                {
                    id: 1,
                    name: 'kingsoft',
                    since: "1988"
                }
            //参数自定义，这里只是负责传递参数，在WpsInvoke.RegWebNotify方法的回调函数中去做接收，自行解析参数

            let params = {
                type: 'executeFunc2',
                message: "当前时间为：" + currentTime(),
                msgInfoStr: JSON.stringify(msgInfo)
            }
            /**
             * WebNotify:
             * 参数1：发送给业务系统的消息
             * 参数2：是否将消息加入队列，是否防止丢失消息，都需要设置为true
             */
            wps.OAAssist.WebNotify(JSON.stringify(params), true); //如果想传一个对象，则使用JSON.stringify方法转成对象字符串。
            break;
        }
        case "btnAddWebShape": {
            let l_doc = wps.WpsApplication().ActiveDocument;
            l_doc.Shapes.AddWebShape("https://www.wps.cn");
            break;
        }
        default:
            break;
    }
    return true;
}

/**
 * 作用：重新设置当前用户名称
 */
function OnUserNameSetClick() {
    var l_UserPageUrl = "setUserName.html"
    OnShowDialog(l_UserPageUrl, "OA助手用户名称设置", 500, 300);
}

/**
 * 作用：展示当前文档，被OA助手打开后的，操作记录及相关附加信息
 */
function OnOADocInfo() {
    return;
}

/**
 * 作用：自定义菜单按钮的图标
 */
function GetImage(control) {
    var eleId;
    if (typeof control == "object" && arguments.length == 1) { //针对Ribbon的按钮的
        eleId = control.Id;
    } else if (typeof control == "undefined" && arguments.length > 1) { //针对idMso的
        eleId = arguments[1].Id;
    } else if (typeof control == "boolean" && arguments.length > 1) { //针对checkbox的
        eleId = arguments[1].Id;
    } else if (typeof control == "number" && arguments.length > 1) { //针对combox的
        eleId = arguments[2].Id;
    }
    switch (eleId) {
        case "btnOpenWPSYUN":
            return "./icon/w_WPSCloud.png"; //打开WPS云文档
        case "btnOpenLocalWPSYUN": //导入文件
            return "./icon/w_ImportDoc.png"
        case "btnSaveToServer": //保存到OA后台服务端
            return "./icon/w_Save.png";
        case "btnSaveAsFile": //另存为本地文件
            return "./icon/w_SaveAs.png";
        case "btnChangeToPDF": //输出为PDF格式
            return "./icon/w_PDF.png";
        case "btnChangeToUOT": //
            return "./icon/w_DocUOF.png";
        case "btnChangeToOFD": //转OFD上传
            return "./icon/w_DocOFD.png"; //
        case "btnInsertRedHeader": //套红头
            return "./icon/w_GovDoc.png";
        case "btnRevokeRedHeader": //撤销红头
            return "./icon/w_RejectRev.png";
        case "btnInsertSeal": //印章
            return "./icon/c_seal.png";
        case "btnClearRevDoc": //清稿
            return "./icon/w_DocClear.png"
        case "btnUploadOABackup": //备份正文
            return "./icon/w_BackDoc.png";
        case "btnOpenRevision": //打开 修订
        case "btnShowRevision": //
            return "./icon/w_OpenRev.png";
        case "btnCloseRevision": //关闭修订
            return "./icon/w_CloseRev.png";
        case "btnAcceptAllRevisions": // 接受修订
            return "./icon/w_AcceptRev.png";
        case "btnRejectAllRevisions": // 拒绝修订
            return "./icon/w_RejectRev.png";
        case "btnSaveAsFile":
            return "";
        case "btnInsertPic": //插入图片
            return "./icon/w_InsPictures.png";
        case "btnOpenScan": //打开扫描仪
            return "./icon/w_Scanner16.png"; //
        case "btnPageSetup": //打开页面设置
            return "./icon/w_PageGear.png";
        case "btnInsertDate": //插入日期
            return "./icon/w_InsDate.png";
        case "btnQRCode": //二维码
            return "./icon/w_DocQr.png";
        case "btnPrintDOC": // 打印设置
            return "./icon/c_printDoc.png";
        case "btnInsertBookmark":
            return "./icon/c_bookmark.png";
        case "btnImportTemplate":
            return "./icon/w_ImportDoc.png";
        case "btnSendMessage1":
            return "./icon/3.svg"
        case "btnSendMessage2":
            return "./icon/3.svg"
        default:
            ;
    }
    return "./icon/c_default.png";
}

function pGetOpenRevisionButtonLabel() {
    return "打开修订";
}

function pGetShowRevisionButtonLabel() {
    return "显示修订";
}


//xml文件中自定义按钮的文字处理函数
function OnGetLabel(control) {
    var eleId;
    if (typeof control == "object" && arguments.length == 1) { //针对Ribbon的按钮的
        eleId = control.Id;
    } else if (typeof control == "undefined" && arguments.length > 1) { //针对idMso的
        eleId = arguments[1].Id;
    } else if (typeof control == "boolean" && arguments.length > 1) { //针对checkbox的
        eleId = arguments[1].Id;
    } else if (typeof control == "number" && arguments.length > 1) { //针对combox的
        eleId = arguments[2].Id;
    }
    switch (eleId) {
        case "btnOpenWPSYUN": //打开WPS云文档
            return "WPS云文档";
        case "btnOpenLocalWPSYUN": //打开本地云文档目录
            return "导入文档";
        case "btnSaveAsFile":
            return "另存为本地";
        case "btnChangeToPDF": //转PDF并上传
            return "转PDF上传";
        case "btnChangeToUOT": //转UOF并上传
            return "转UOF上传";
        case "btnChangeToOFD": //转OFD格式并上传
            return "转OFD上传";
        case "lblDocSourceValue": //文件来源标签：
            return pGetDocSourceLabel();
        case "lblUserName": //用户名：lableControl 控件
            return "编辑人:"; //pSetUserNameLabelControl();
        case "btnUserName":
            return pSetUserNameLabelControl();
        //======================================================
        case "btnInsertRedHeader": //套红头
            return "套红头";
        case "btnRevokeRedHeader": //撤销红头
            return "撤销红头";
        case "btnInsertSeal": //插入印章
            return "印章";
        case "btnUploadOABackup": //文件备份
            return "文件备份";
        //======================================================
        case "btnOpenRevision": //打开修订按钮
            return pGetOpenRevisionButtonLabel();
        case "btnShowRevision": //显示修订按钮
            return pGetShowRevisionButtonLabel();
        case "btnCloseRevision": //关闭修订按钮
            return "关闭修订";
        case "btnClearRevDoc": //显示 清稿 
            return "清稿";
        case "btnAcceptAllRevisions": //显示 接受修订
            return "接受修订";
        case "btnRejectAllRevisions": //显示 拒绝修订
            return "拒绝修订";
        case "lblDocLandMode": //显示 文档落地方式 ：不落地还是本地，包括是否受保护
            return pGetOADocLabel();
        //---------------------------------------------
        case "btnInsertPic": //插入图片
            return "插图片";
        case "btnInsertDate": //插入日期
            return "插日期";
        case "btnOpenScan": //打开扫描仪
            return "扫描仪";
        case "btnPageSetup": //打开页面设置
            return "页面设置";
        case "btnPrintDOC": //打开页面设置
            return "打印设置";
        case "btnInsertBookmark":
            return "导入书签";
        case "btnImportTemplate":
            return "导入模板";
        default:
            ;
    }
    return "";
}

/**
 * 作用：处理Ribbon按钮的是否可显示
 * @param {*} control  ：Ribbon 的按钮控件
 */
function OnGetVisible(control) {
    var eleId;
    if (typeof control == "object" && arguments.length == 1) { //针对Ribbon的按钮的
        eleId = control.Id;
    } else if (typeof control == "undefined" && arguments.length > 1) { //针对idMso的
        eleId = arguments[1].Id;
    } else if (typeof control == "boolean" && arguments.length > 1) { //针对checkbox的
        eleId = arguments[1].Id;
    } else if (typeof control == "number" && arguments.length > 1) { //针对combox的
        eleId = arguments[2].Id;
    }
    var l_value = false;

    //关闭一些测试中的功能
    switch (eleId) {
        case "lblDocLandMode": //文档落地标签
            return true;
        case "btnOpenScan":
            return false;
        case "btnAddWebShape": {
            if (wps.WpsApplication().Build.toString().indexOf("11.1") != -1) {
                return true;
            }
            return false;
        }
            break;
        default:

    }

    //按照 OA文档传递过来的属性进行判断
    l_value = pShowRibbonGroupByOADocParam(eleId);
    return l_value;
}

/**
 * 作用：处理Ribbon按钮的是否可用
 * @param {*} control  ：Ribbon 的按钮控件
 */
function OnGetEnabled(control) {
    var eleId;
    if (typeof control == "object" && arguments.length == 1) { //针对Ribbon的按钮的
        eleId = control.Id;
    } else if (typeof control == "undefined" && arguments.length > 1) { //针对idMso的
        eleId = arguments[1].Id;
    } else if (typeof control == "boolean" && arguments.length > 1) { //针对checkbox的
        eleId = arguments[1].Id;
    } else if (typeof control == "number" && arguments.length > 1) { //针对combox的
        eleId = arguments[2].Id;
    }
    switch (eleId) {
        case "btnSaveToServer": //保存到OA服务器的相关按钮。判断，如果非OA文件，禁止点击
        case "btnChangeToPDF": //保存到PDF格式再上传
        case "btnChangeToUOT": //保存到UOT格式再上传
        case "btnChangeToOFD": //保存到OFD格式再上传
        case "SaveAll": //保存所有文档
        //以下四个idMso的控制是关于文档新建的
        case "FileNew":
        case "FileNewMenu":
        case "WindowNew":
        case "FileNewBlankDocument":
            return OnSetSaveToOAEnable();
        case "btnCloseRevision": {
            let bFlag = wps.PluginStorage.getItem(constStrEnum.RevisionEnableFlag)
            return bFlag
        }
        case "btnOpenRevision": {
            let bFlag = wps.PluginStorage.getItem(constStrEnum.RevisionEnableFlag)
            return !bFlag
        }
        case "PictureInsert":
            return false;
        case "TabInsert"://WPS自身tab：插入
            return true;
        case "TabDeveloper": //WPS自身tab：开发工具
            // case "TabPageLayoutWord": //WPS自身tab：页面布局
            // case "TabReferences": //WPS自身tab：引用
            // case "TabReviewWord": //WPS自身tab：审阅
            // case "TabView": //WPS自身tab：视图
        {
            if (pCheckIfOADoc()) {
                return false;//如果是OA打开的文档，把这个几个tab不可用/隐藏
            } else {
                return true;
            }
        }
        default:
            ;
    }
    return true;
}