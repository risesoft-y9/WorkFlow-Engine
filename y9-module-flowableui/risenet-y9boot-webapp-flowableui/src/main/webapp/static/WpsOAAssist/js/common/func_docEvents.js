// 打印前监听事件
function OnDocumentBeforePrint(doc) {
    return;
}

//切换窗口时触发的事件
function OnWindowActivate() {
    var l_doc = wps.WpsApplication().ActiveDocument;
    SetCurrDocEnvProp(l_doc); // 设置当前文档对应的用户名
    showOATab(); // 根据文件是否为OA文件来显示OA菜单再进行刷新按钮
    setTimeout(activeTab, 2000); // 激活页面必须要页签显示出来，所以做1秒延迟
    return;
}

/**
 *  作用：判断OA文档是否被另存为了
 */
function CheckIfOADocSaveAs(doc) {
    if (!doc) {
        return;
    }
    // 获取OA文档的原始保存路径
    var l_Path = GetDocParamsValue(doc, constStrEnum.SourcePath);
    // 原路径和当前文件的路径对比
    return l_Path == doc.FullName;
}


// 当文件保存前触发的事件
function OnDocumentBeforeSave(doc) {
    //设置变量，判断是否当前用户按了自定义的OA文件保存按钮
    var l_IsOADocButtonSave = false;
    l_IsOADocButtonSave = wps.PluginStorage.getItem(constStrEnum.OADocUserSave);

    //根据传入参数判断当前文档是否能另存为，默认不能另存为
    if (pCheckCurrOADocCanSaveAs(doc) == false) { //先根据OA助手的默认设置判断是否允许OA文档另存为操作        
        //0.如果配置文件：OA文档不允许另存为，则再判断
        //1.先判断是否是在线文档且是通过WPS自身按钮或快捷键保存，则取消弹出另存到本地的弹出框
        if (pIsOnlineOADoc(doc) == true && l_IsOADocButtonSave == false) {
            alert("来自OA的不落地文档，禁止另存为本地文档！");
            //如果是OA文档，则禁止另存为
            wps.ApiEvent.Cancel = true;
        }
        //2.如果是落地打开的OA文档并且通过WPS自身按钮或者快捷键保存，则执行保存到本地临时目录，取消弹出对话框
        if (pIsOnlineOADoc(doc) == false && l_IsOADocButtonSave == false){
            //用户手动另存为操作时，在这里被屏蔽掉
            doc.Save();
            //如果是OA文档，则禁止另存为
            wps.ApiEvent.Cancel = true;
        }        
    }
    //保存文档后，也要刷新一下Ribbon按钮的状态
    showOATab();
    return;
}


//文档保存前关闭事件
/**
 * 作用：
 * @param {*} doc 
 */
function OnDocumentBeforeClose(doc) {
    console.log('OnDocumentBeforeClose');
    var l_fullName = doc.FullName;
    var l_bIsOADoc = false;
    l_bIsOADoc = CheckIfDocIsOADoc(doc); //判断是否OA文档要关闭
    if (l_bIsOADoc == false) { // 非OA文档不做处理
        return;
    }
    //判断是否只读的文档，或受保护的文档，对于只读的文档，不给予保存提示
    if (pISOADocReadOnly(doc) == false) {
        //if (doc.Saved == false) { //如果OA文档关闭前，有未保存的数据
            //if (wps.confirm("系统文件有改动，是否提交后关闭？" + "\n" + "确认后请按上传按钮执行上传操作。取消则继续关闭文档。")) {
                //wps.ApiEvent.Cancel = true;
               // return;
           // }
        //}
    }
    // 有未保存的数据，确认无需保存直接关闭
    doc.Close(wps.Enum&&wps.Enum.wdDoNotSaveChanges||0); // 不保存待定的更改。枚举值兼容性写法
    closeWpsIfNoDocument(); // 判断WPS中的文件个数是否为0，若为0则关闭WPS函数
    wps.FileSystem.Remove(l_fullName);
}


//文档保存后关闭事件
function OnDocumentAfterClose(doc) {
    console.log("OnDocumentAfterClose");
    var l_NofityURL = GetDocParamsValue(doc, constStrEnum.notifyUrl);
    if (l_NofityURL) {
        l_NofityURL = l_NofityURL.replace("{?}", "3"); //约定：参数为3则文档关闭
        console.log("" + l_NofityURL);
        NotifyToServer(l_NofityURL);
    }

    pRemoveDocParam(doc); // 关闭文档时，移除PluginStorage对象的参数
    pSetWPSAppUserName(); // 判断文档关闭后，如果系统已经没有打开的文档了，则设置回初始用户名
}

//文档打开事件
function OnDocumentOpen(doc) {
    //设置当前新增文档是否来自OA的文档
    // if (wps.PluginStorage.getItem(constStrEnum.IsInCurrOADocOpen) == false) {
    //     //如果是用户自己在WPS环境打开文档，则设置非OA文档标识
    //     console.log(wps.PluginStorage.getItem(wps.WpsApplication().ActiveDocument.DocID))
    //     pSetNoneOADocFlag(doc);
    //     console.log(wps.PluginStorage.getItem(wps.WpsApplication().ActiveDocument.DocID))
    // }
    console.log(testFuncs);
    OnWindowActivate();
    ChangeOATabOnDocOpen(); //打开文档后，默认打开Tab页
    setTimeout(activeTab,2000); // 激活OA助手菜单
}

//新建文档事件
function OnDocumentNew(doc) {
    //设置当前新增文档是否来自OA的文档
    // if (wps.PluginStorage.getItem(constStrEnum.IsInCurrOADocOpen) == false) {
    //     //如果是用户自己在WPS环境打开文档，则设置非OA文档标识
    //     pSetNoneOADocFlag(doc);
    // }
    ChangeOATabOnDocOpen(); // 打开OA助手Tab菜单页
    wps.ribbonUI.Invalidate(); // 刷新Ribbon按钮的状态
}

/**
 *  作用：判断当前文档是否是只读文档
 *  返回值：布尔
 */
function pISOADocReadOnly(doc) {
    if (!doc) {
        return false;
    }
    var l_openType = GetDocParamsValue(doc, constStrEnum.openType); // 获取OA传入的参数 openType
    if (l_openType == "") {
        return false;
    }
    try {
        if (l_openType.protectType != -1) { // -1 为未保护
            return true;
        }
    } catch (err) {
        return false;
    }
}


/**
 *  作用：根据当前活动文档的情况判断，当前文档适用的系统参数，例如：当前文档对应的用户名称等
 */
function SetCurrDocEnvProp(doc) {
    if (!doc) return;
    var l_bIsOADoc = false;
    l_bIsOADoc = pCheckIfOADoc(doc);

    //如果是OA文件，则按OA传来的用户名设置WPS   OA助手WPS用户名设置按钮冲突
    if (l_bIsOADoc == true) {
        var l_userName = GetDocParamsValue(doc, constStrEnum.userName);
        if (l_userName != "") {
            wps.WpsApplication().UserName = l_userName;
            return;
        }
    }
    //如果是非OA文件或者参数的值是空值，则按WPS安装默认用户名设置
    wps.WpsApplication().UserName = wps.PluginStorage.getItem(constStrEnum.WPSInitUserName);
}



/*
    入口参数：doc
    功能说明：判断当前文档是否能另存为本地文件
    返回值：布尔值true or false
*/
function pCheckCurrOADocCanSaveAs(doc) {
    //如果是非OA文档，则允许另存为
    if (CheckIfDocIsOADoc(doc) == false) return true;

    //对于来自OA系统的文档，则获取该文档对应的属性参数
    var l_CanSaveAs = GetDocParamsValue(doc, constStrEnum.CanSaveAs);

    //判断OA传入的参数
    if (typeof (l_CanSaveAs) == "boolean") {
        return l_CanSaveAs;
    }
    return false;
}

/**
 * 作用：文档关闭后，删除对应的PluginStorage内的参数信息
 * 返回值：没有返回值
 * @param {*} doc 
 */
function pRemoveDocParam(doc) {
    if (!doc) return;
    wps.PluginStorage.removeItem(doc.DocID);
    return;
}

/**
 * 作用：判断当前文档是否从OA来的文档，如果非OA文档（就是本地新建或打开的文档，则设置EnumOAFlag 标识）
 * 作用：设置非OA文档的标识状态
 * @param {*} doc 
 * 返回值：无
 */
function pSetNoneOADocFlag(doc) {
    if (!doc) return;
    var l_param = wps.PluginStorage.getItem(doc.DocID); //定义JSON文档参数
    var l_objParams = new Object();
    if (l_param) {
        l_objParams = JSON.parse(l_param);
    }
    l_objParams.isOA = EnumOAFlag.DocFromNoOA; // 新增非OA打开文档属性
    wps.PluginStorage.setItem(doc.DocID, JSON.stringify(l_objParams)); // 存入内存中
}

/**
 * 作用：根据设置判断打开文件是否默认激活OA助手工具Tab菜单
 * 返回值：无
 */
function ChangeOATabOnDocOpen() {
    var l_ShowOATab = true; //默认打开
    l_ShowOATab = wps.PluginStorage.getItem(constStrEnum.ShowOATabDocActive);
    if (l_ShowOATab == true) {
        setTimeout(activeTab,500);
        // wps.ribbonUI.ActivateTab("WPSWorkExtTab"); //新建文档时，自动切换到OA助手状态
    }
}