(function (global, factory) {

    "use strict";

    if (typeof module === "object" && typeof module.exports === "object") {
        module.exports = factory(global, true);
    } else {
        factory(global);
    }

})(typeof window !== "undefined" ? window : this, function (window, noGlobal) {

    "use strict";

    var bFinished = true;

    function getHttpObj() {
        var httpobj = null;
        if (IEVersion() < 10) {
            try {
                httpobj = new XDomainRequest();
            } catch (e1) {
                httpobj = new createXHR();
            }
        } else {
            httpobj = new createXHR();
        }
        return httpobj;
    }
    //兼容IE低版本的创建xmlhttprequest对象的方法
    function createXHR() {
        if (typeof XMLHttpRequest != 'undefined') { //兼容高版本浏览器
            return new XMLHttpRequest();
        } else if (typeof ActiveXObject != 'undefined') { //IE6 采用 ActiveXObject， 兼容IE6
            var versions = [ //由于MSXML库有3个版本，因此都要考虑
                'MSXML2.XMLHttp.6.0',
                'MSXML2.XMLHttp.3.0',
                'MSXML2.XMLHttp'
            ];

            for (var i = 0; i < versions.length; i++) {
                try {
                    return new ActiveXObject(versions[i]);
                } catch (e) {
                    //跳过
                }
            }
        } else {
            throw new Error('您的浏览器不支持XHR对象');
        }
    }

    function startWps(options) {
        if (!bFinished && !options.concurrent) {
            if (options.callback)
                options.callback({
                    status: 1,
                    message: "上一次请求没有完成"
                });
            return;
        }
        bFinished = false;

        function startWpsInnder(tryCount) {
            if (tryCount <= 0) {
                if (bFinished)
                    return;
                bFinished = true;
                if (options.callback)
                    options.callback({
                        status: 2,
                        message: "请允许浏览器打开WPS Office"
                    });
                return;
            }
            var xmlReq = getHttpObj();
            //WPS客户端提供的接收参数的本地服务，HTTP服务端口为58890，HTTPS服务端口为58891
            //这俩配置，取一即可，不可同时启用
            xmlReq.open('POST', options.url);
            xmlReq.onload = function (res) {
                bFinished = true;
                if (options.callback) {
                    options.callback({
                        status: 0,
                        response: IEVersion() < 10 ? xmlReq.responseText : res.target.response
                    });
                }
            }
            xmlReq.ontimeout = xmlReq.onerror = function (res) {
                xmlReq.bTimeout = true;
                if (tryCount == options.tryCount && options.bPop) { //打开wps并传参
                    window.location.href = "ksoWPSCloudSvr://start=RelayHttpServer" //是否启动wps弹框
                }
                setTimeout(function () {
                    startWpsInnder(tryCount - 1)
                }, 1000);
            }
            if (IEVersion() < 10) {
                xmlReq.onreadystatechange = function () {
                    if (xmlReq.readyState != 4)
                        return;
                    if (xmlReq.bTimeout) {
                        return;
                    }
                    if (xmlReq.status === 200)
                        xmlReq.onload();
                    else
                        xmlReq.onerror();
                }
            }
            xmlReq.timeout = options.timeout;
            xmlReq.send(options.sendData)
        }
        startWpsInnder(options.tryCount);
        return;
    }

    var fromCharCode = String.fromCharCode;
    // encoder stuff
    var cb_utob = function (c) {
        if (c.length < 2) {
            var cc = c.charCodeAt(0);
            return cc < 0x80 ? c :
                cc < 0x800 ? (fromCharCode(0xc0 | (cc >>> 6)) +
                    fromCharCode(0x80 | (cc & 0x3f))) :
                    (fromCharCode(0xe0 | ((cc >>> 12) & 0x0f)) +
                        fromCharCode(0x80 | ((cc >>> 6) & 0x3f)) +
                        fromCharCode(0x80 | (cc & 0x3f)));
        } else {
            var cc = 0x10000 +
                (c.charCodeAt(0) - 0xD800) * 0x400 +
                (c.charCodeAt(1) - 0xDC00);
            return (fromCharCode(0xf0 | ((cc >>> 18) & 0x07)) +
                fromCharCode(0x80 | ((cc >>> 12) & 0x3f)) +
                fromCharCode(0x80 | ((cc >>> 6) & 0x3f)) +
                fromCharCode(0x80 | (cc & 0x3f)));
        }
    };
    var re_utob = /[\uD800-\uDBFF][\uDC00-\uDFFFF]|[^\x00-\x7F]/g;
    var utob = function (u) {
        return u.replace(re_utob, cb_utob);
    };
    var _encode = function (u) {
        var isUint8Array = Object.prototype.toString.call(u) === '[object Uint8Array]';
        if (isUint8Array)
            return u.toString('base64')
        else
            return btoa(utob(String(u)));
    }

    if (typeof window.btoa !== 'function') window.btoa = func_btoa;

    function func_btoa(input) {
        var str = String(input);
        var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
        for (
            // initialize result and counter
            var block, charCode, idx = 0, map = chars, output = '';
            // if the next str index does not exist:
            //   change the mapping table to "="
            //   check if d has no fractional digits
            str.charAt(idx | 0) || (map = '=', idx % 1);
            // "8 - idx % 1 * 8" generates the sequence 2, 4, 6, 8
            output += map.charAt(63 & block >> 8 - idx % 1 * 8)
        ) {
            charCode = str.charCodeAt(idx += 3 / 4);
            if (charCode > 0xFF) {
                throw new InvalidCharacterError("'btoa' failed: The string to be encoded contains characters outside of the Latin1 range.");
            }
            block = block << 8 | charCode;
        }
        return output;
    }

    var encode = function (u, urisafe) {
        return !urisafe ?
            _encode(u) :
            _encode(String(u)).replace(/[+\/]/g, function (m0) {
                return m0 == '+' ? '-' : '_';
            }).replace(/=/g, '');
    };

    function IEVersion() {
        var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串  
        var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; //判断是否IE<11浏览器  
        var isEdge = userAgent.indexOf("Edge") > -1 && !isIE; //判断是否IE的Edge浏览器  
        var isIE11 = userAgent.indexOf('Trident') > -1 && userAgent.indexOf("rv:11.0") > -1;
        if (isIE) {
            var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
            reIE.test(userAgent);
            var fIEVersion = parseFloat(RegExp["$1"]);
            if (fIEVersion == 7) {
                return 7;
            } else if (fIEVersion == 8) {
                return 8;
            } else if (fIEVersion == 9) {
                return 9;
            } else if (fIEVersion == 10) {
                return 10;
            } else {
                return 6; //IE版本<=7
            }
        } else if (isEdge) {
            return 20; //edge
        } else if (isIE11) {
            return 11; //IE11  
        } else {
            return 30; //不是ie浏览器
        }
    }

    function WpsStart(clientType, name, func, param, useHttps, callback, tryCount, bPop) {
        var startInfo = {
            "name": name,
            "function": func,
            "info": param.param,
            "jsPluginsXml": param.jsPluginsXml
        };
        var strData = JSON.stringify(startInfo);
        if (IEVersion() < 10) {
            try {
                eval("strData = '" + JSON.stringify(startInfo) + "';");
            } catch (err) {

            }
        }

        var baseData = encode(strData);
        var url = "http://127.0.0.1:58890/" + clientType + "/runParams";
        if (useHttps)
            url = "https://127.0.0.1:58891/" + clientType + "/runParams";
        var data = "ksowebstartup" + clientType + "://" + baseData;
        startWps({
            url: url,
            sendData: data,
            callback: callback,
            tryCount: tryCount,
            bPop: bPop,
            timeout: 5000,
            concurrent: false
        });
    }

    function WpsStartWrap(clientType, name, func, param, callback) {
        WpsStart(clientType, name, func, param, false, callback, 4, true)
    }

    function WpsStartWrapHttps(clientType, name, func, param, callback) {
        WpsStart(clientType, name, func, param, true, callback, 4, true)
    }

    var exId = 0;
    /**
     * 支持浏览器触发，WPS有返回值的启动
     *
     * @param {*} clientType	组件类型
     * @param {*} name			WPS加载项名称
     * @param {*} func			WPS加载项入口方法
     * @param {*} param			参数：包括WPS加载项内部定义的方法，参数等
     * @param {*} useHttps		是否使用https协议
     * @param {*} callback		回调函数
     * @param {*} tryCount		重试次数
     * @param {*} bPop			是否弹出浏览器提示对话框
     */
    function WpsStartWrapExInner(clientType, name, func, param, useHttps, callback, tryCount, bPop) {
        var rspUrl = "http://127.0.0.1:58890/transferEcho/runParams";
        if (useHttps)
            rspUrl = "https://127.0.0.1:58891/transferEcho/runParams";
        var time = new Date();
        var cmdId = "js" + time.getTime() + "_" + exId;
        var infocontent = JSON.stringify(param.param);
        var funcEx = "var res = " + func;
        var cbCode = "var xhr = new XMLHttpRequest();xhr.open('POST', '" + rspUrl + "');xhr.send(JSON.stringify({id: '" + cmdId + "', response: res}));" //res 为func执行返回值
        var infoEx = infocontent + ");" + cbCode + "void(0";
        //固定格式，无需修改
        var startInfo = {
            "name": name,
            "function": funcEx,
            "info": infoEx,
			"showToFront": param.showToFront,
            "jsPluginsXml": param.jsPluginsXml
        };
        var strData = JSON.stringify(startInfo);
        if (IEVersion() < 10) {
            try {
                eval("strData = '" + JSON.stringify(startInfo) + "';");
            } catch (err) {

            }
        }

        var baseData = encode(strData);
        var url = "http://127.0.0.1:58890/transfer/runParams";
        if (useHttps)
            url = "https://127.0.0.1:58891/transfer/runParams";
        var data = "ksowebstartup" + clientType + "://" + baseData;
        var wrapper = {
            id: cmdId,
            app: clientType,
            data: data
        };
        wrapper = JSON.stringify(wrapper);
        startWps({
            url: url,
            sendData: wrapper,
            callback: callback,
            tryCount: tryCount,
            bPop: bPop,
            timeout: 0,
            concurrent: true
        });
    }

    var serverVersion = "wait"

    function WpsStartWrapVersionInner(clientType, name, func, param, useHttps, callback) {
        if (serverVersion == "wait") {
            var url = "http://127.0.0.1:58890/version";
            if (useHttps)
                url = "https://127.0.0.1:58891/version";
            startWps({
                url: url,
                data: "",
                callback: function (res) {
                    if (res.status !== 0) {
                        callback(res)
                        return;
                    }
                    serverVersion = res.response;
                    if (serverVersion === "") {
                        WpsStart(clientType, name, func, param, useHttps, callback, 1, false);
                    } else {
                        WpsStartWrapExInner(clientType, name, func, param, useHttps, callback, 1, false);
                    }
                },
                tryCount: 4,
                bPop: true,
                timeout: 5000
            });
        } else {
            if (serverVersion === "") {
                WpsStartWrap(clientType, name, func, param, useHttps, callback);
            } else {
                WpsStartWrapExInner(clientType, name, func, param, useHttps, callback, 1, true);
            }
        }
    }

    var RegWebNotifyID = null
    /**
     * 注册一个前端页面接收WPS传来消息的方法
     * @param {*} clientType wps | et | wpp
     * @param {*} name WPS加载项的名称
     * @param {*} callback 回调函数
     */
    function RegWebNotify(clientType, name, callback) {
        if (RegWebNotifyID) {
            return
        }

        RegWebNotifyID = new Date().valueOf() + ''
        var paramStr = {
            id: RegWebNotifyID,
            name: name,
            type: clientType
        }
        var askItem = function () {
            var xhr = getHttpObj()
            xhr.onload = function (e) {
                callback(xhr.responseText)
                window.setTimeout(askItem, 300)
            }
            xhr.onerror = function (e) {
                window.setTimeout(askItem, 10000)
            }
            xhr.ontimeout = function (e) {
                window.setTimeout(askItem, 10000)
            }
            if (IEVersion() < 10) {
                xhr.onreadystatechange = function () {
                    if (xhr.readyState != 4)
                        return;
                    if (xhr.bTimeout) {
                        return;
                    }
                    if (xhr.status === 200)
                        xhr.onload();
                    else
                        xhr.onerror();
                }
            }
            xhr.open('POST', 'http://127.0.0.1:58890/askwebnotify', true)
            xhr.send(JSON.stringify(paramStr))
        }

        window.setTimeout(askItem, 2000)
    }

    function WpsStartWrapVersion(clientType, name, func, param, callback, showToFront, jsPluginsXml) {
        let paramEx = {
            jsPluginsXml: jsPluginsXml ? jsPluginsXml : "",
			showToFront: typeof(showToFront) == 'boolean' ? showToFront : true,
            param: (typeof (param) == 'object' ? param : JSON.parse(param))
        }
        WpsStartWrapVersionInner(clientType, name, func, paramEx, false, callback);
    }

    function WpsStartWrapHttpsVersion(clientType, name, func, param, callback, showToFront, jsPluginsXml) {
        let paramEx = {
            jsPluginsXml: jsPluginsXml ? jsPluginsXml : "",
			showToFront: typeof(showToFront) == 'boolean' ? showToFront : true,
            param: (typeof (param) == 'object' ? param : JSON.parse(param))
        }
        WpsStartWrapVersionInner(clientType, name, func, paramEx, true, callback);
    }

    //从外部浏览器远程调用 WPS 加载项中的方法
    var WpsInvoke = {
        InvokeAsHttp: WpsStartWrapVersion,
        InvokeAsHttps: WpsStartWrapHttpsVersion,
        RegWebNotify: RegWebNotify,
        ClientType: {
            wps: "wps",
            et: "et",
            wpp: "wpp"
        },
        CreateXHR: getHttpObj
    }

    if (typeof noGlobal === "undefined") {
        window.WpsInvoke = WpsInvoke;
    }

    function WpsAddonGetAllConfig(callBack) {
        var baseData;
        startWps({
            url: "http://127.0.0.1:58890/publishlist",
            type: "GET",
            sendData: baseData,
            callback: callBack,
            tryCount: 3,
            bPop: true,
            timeout: 5000,
            concurrent: true
        });
    }

    function WpsAddonVerifyStatus(element, callBack) {
        var xmlReq = getHttpObj();
        var offline = element.online === "false";
        var url = offline ? element.url : element.url + "ribbon.xml";
        xmlReq.open("POST", "http://localhost:58890/redirect/runParams");
        xmlReq.onload = function (res) {
            if (offline && !res.target.response.startsWith("7z")) {
                callBack({ status: 1, msg: "不是有效的7z格式" + url });
            } else if (!offline && !res.target.response.startsWith("<customUI")) {
                callBack({ status: 1, msg: "不是有效的ribbon.xml, " + url })
            } else {
                callBack({ status: 0, msg: "OK" })
            }
        }
        xmlReq.onerror = function (res) {
            xmlReq.bTimeout = true;
            callBack({ status: 2, msg: "网页路径不可访问，如果是跨域问题，不影响使用" + url })
        }
        xmlReq.ontimeout = function (res) {
            xmlReq.bTimeout = true;
            callBack({ status: 3, msg: "访问超时" + url })
        }
        if (IEVersion() < 10) {
            xmlReq.onreadystatechange = function () {
                if (xmlReq.readyState != 4)
                    return;
                if (xmlReq.bTimeout) {
                    return;
                }
                if (xmlReq.status === 200)
                    xmlReq.onload();
                else
                    xmlReq.onerror();
            }
        }
        xmlReq.timeout = 5000;
        var data = {
            method: "get",
            url: url,
            data: ""
        }
        var sendData = FormatSendData(data)
        xmlReq.send(sendData);
    }

    function WpsAddonHandleEx(element, cmd, callBack) {
        var data = FormatData(element, cmd);
        startWps({
            url: "http://localhost:58890/deployaddons/runParams",
            type: "POST",
            sendData: data,
            callback: callBack,
            tryCount: 3,
            bPop: true,
            timeout: 5000,
            concurrent: true
        });
    }

    function WpsAddonEnable(element, callBack) {
        WpsAddonHandleEx(element, "enable", callBack)
    }

    function WpsAddonDisable(element, callBack) {
        WpsAddonHandleEx(element, "disable", callBack)
    }

    function FormatData(element, cmd) {
        var data = {
            "cmd": cmd, //"enable", 启用， "disable", 禁用, "disableall", 禁用所有
            "name": element.name,
            "url": element.url,
            "addonType": element.addonType,
            "online": element.online,
            "version": element.version,
            "time":new Date().getTime()
        }
        return FormatSendData(data);
    }

    function FormatSendData(data) {
        var strData = JSON.stringify(data);
        if (IEVersion() < 10)
            eval("strData = '" + JSON.stringify(strData) + "';");
        return encode(strData);
    }

    //管理 WPS 加载项
    var WpsAddonMgr = {
        getAllConfig: WpsAddonGetAllConfig,
        verifyStatus: WpsAddonVerifyStatus,
        enable: WpsAddonEnable,
        disable: WpsAddonDisable,
    }

    if (typeof noGlobal === "undefined") {
        window.WpsAddonMgr = WpsAddonMgr;
    }

    return { WpsInvoke: WpsInvoke, WpsAddonMgr: WpsAddonMgr, version: "1.0.4" };
});