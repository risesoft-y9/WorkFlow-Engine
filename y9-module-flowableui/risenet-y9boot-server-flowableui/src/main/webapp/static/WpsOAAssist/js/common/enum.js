/**
 * WPS常用的API枚举值，具体参与API文档
 */
var WPS_Enum = {
    wdDoNotSaveChanges: 0,
    wdFormatPDF: 17,
    wdFormatOpenDocumentText: 23,
    wdFieldFormTextInput: 70,
    wdAlertsNone: 0,
    wdDialogFilePageSetup: 178,
    wdDialogFilePrint: 88,
    wdRelativeHorizontalPositionPage: 1,
    wdGoToPage: 1,
    wdPropertyPages: 14,
    wdRDIComments: 1,
    wdDialogInsertDateTime: 165,
    msoCTPDockPositionLeft: 0,
    msoCTPDockPositionRight: 2,
    /**
     * 将形状嵌入到文字中。
     */
    wdWrapInline: 7,
    /**
     * 将形状放在文字前面。 请参阅 wdWrapFront。
     */
    wdWrapNone: 3,
    /**
     * 使文字环绕形状。 行在形状的另一侧延续。
     */
    wdWrapSquare: 0,
    /**
     * 使文字环绕形状。
     */
    wdWrapThrough: 2,
    /**
     * 使文字紧密地环绕形状。
     */
    wdWrapTight: 1,
    /**
     * 将文字放在形状的上方和下方。
     */
    wdWrapTopBottom: 4,
    /**
     * 将形状放在文字后面。
     */
    wdWrapBehind: 5,
    /**
     * 将形状放在文字前面。
     */
    wdWrapFront: 6
}

/**
 * WPS加载项自定义的枚举值
 */
var constStrEnum = {
    AllowOADocReOpen: "AllowOADocReOpen",
    AutoSaveToServerTime: "AutoSaveToServerTime",
    bkInsertFile: "bkInsertFile",
    buttonGroups: "buttonGroups",
    CanSaveAs: "CanSaveAs",
    copyUrl: "copyUrl",
    DefaultUploadFieldName: "DefaultUploadFieldName",
    disableBtns: "disableBtns",
    insertFileUrl: "insertFileUrl",
    IsInCurrOADocOpen: "IsInCurrOADocOpen",
    IsInCurrOADocSaveAs: "IsInCurrOADocSaveAs",
    isOA: "isOA",
    notifyUrl: "notifyUrl",
    OADocCanSaveAs: "OADocCanSaveAs",
    OADocLandMode: "OADocLandMode",
    OADocUserSave: "OADocUserSave",
    openType: "openType",
    picPath: "picPath",
    picHeight: "picHeight",
    picWidth: "picWidth",
    redFileElement: "redFileElement",
    revisionCtrl: "revisionCtrl",
    ShowOATabDocActive: "ShowOATabDocActive",
    SourcePath: "SourcePath",
    /**
     * 保存文档到业务系统服务端时，另存一份其他格式到服务端，其他格式支持：.pdf .ofd .uot .uof
     */
    suffix: "suffix",
    templateDataUrl: "templateDataUrl",
    TempTimerID: "TempTimerID",
    /**
     * 文档上传到业务系统的保存地址：服务端接收文件流的地址
     */
    uploadPath: "uploadPath",
    /**
     * 文档上传到服务端后的名称
     */
    uploadFieldName: "uploadFieldName",
    /**
     * 文档上传时的名称，默认取当前活动文档的名称
     */
    uploadFileName: "uploadFileName",
    uploadAppendPath: "uploadAppendPath",
    /**
     * 标志位： 1 在保存到业务系统时再保存一份suffix格式的文档， 需要和suffix参数配合使用
     */
    uploadWithAppendPath: "uploadWithAppendPath",
    userName: "userName",
    WPSInitUserName: "WPSInitUserName",
    taskpaneid: "taskpaneid",
    /**
     * 是否弹出上传前确认和成功后的确认信息：true|弹出，false|不弹出
     */
    Save2OAShowConfirm: "Save2OAShowConfirm",
    /**
     * 修订状态标志位
     */
    RevisionEnableFlag: "RevisionEnableFlag"
}