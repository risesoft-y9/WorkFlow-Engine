// --------------------------  通用常量  ---------------------------

//OA门户网站用接口,配置默认服务器接口
var OA_DOOR = {
    templateDataUrl: undefined, //正文模板列表接口
    templateBaseURL: undefined, //指定正文模板基础接口
    redHeadsPath: undefined, //默认红头模板列表获取路径
    getRedHeadPath: undefined, //默认获取红头文件路径
    bookmarkPath: undefined, //书签列表接口
    redHeadsPath: undefined, //默认红头模板列表获取路径
}

// --------------------------  通用方法  ---------------------------
//去除字符串左边空格
String.prototype.ltrim = function () {
    return this.replace(/(^\s*)/g, "");
}

//去除字符串右边空格
String.prototype.rtrim = function () {
    return this.replace(/(\s*$)/g, "");
}

//扩展js string endwith,startwith方法
String.prototype.endWith = function (str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length)
        return false;
    if (this.substring(this.length - str.length) == str)
        return true;
    else
        return false;
}

String.prototype.startWith = function (str) {
    if (str == null || str == "" || this.length == 0 || str.length > this.length)
        return false;
    if (this.substr(0, str.length) == str)
        return true;
    else
        return false;
}

//UTF-16转UTF-8
function utf16ToUtf8(s) {
    if (!s) {
        return;
    }
    var i, code, ret = [],
        len = s.length;
    for (i = 0; i < len; i++) {
        code = s.charCodeAt(i);
        if (code > 0x0 && code <= 0x7f) {
            //单字节
            //UTF-16 0000 - 007F
            //UTF-8  0xxxxxxx
            ret.push(s.charAt(i));
        } else if (code >= 0x80 && code <= 0x7ff) {
            //双字节
            //UTF-16 0080 - 07FF
            //UTF-8  110xxxxx 10xxxxxx
            ret.push(
                //110xxxxx
                String.fromCharCode(0xc0 | ((code >> 6) & 0x1f)),
                //10xxxxxx
                String.fromCharCode(0x80 | (code & 0x3f))
            );
        } else if (code >= 0x800 && code <= 0xffff) {
            //三字节
            //UTF-16 0800 - FFFF
            //UTF-8  1110xxxx 10xxxxxx 10xxxxxx
            ret.push(
                //1110xxxx
                String.fromCharCode(0xe0 | ((code >> 12) & 0xf)),
                //10xxxxxx
                String.fromCharCode(0x80 | ((code >> 6) & 0x3f)),
                //10xxxxxx
                String.fromCharCode(0x80 | (code & 0x3f))
            );
        }
    }

    return ret.join('');

}
var Base64 = {
	_keyStr: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
	encode: function(e) {
		var t = "";
		var n, r, i, s, o, u, a;
		var f = 0;
		e = Base64._utf8_encode(e);
		while (f < e.length) {
			n = e.charCodeAt(f++);
			r = e.charCodeAt(f++);
			i = e.charCodeAt(f++);
			s = n >> 2;
			o = (n & 3) << 4 | r >> 4;
			u = (r & 15) << 2 | i >> 6;
			a = i & 63;
			if (isNaN(r)) {
				u = a = 64
			} else if (isNaN(i)) {
				a = 64
			}
			t = t + this._keyStr.charAt(s) + this._keyStr.charAt(o) + this._keyStr.charAt(u) + this._keyStr.charAt(a)
		}
		return t
	},
	decode: function(e) {
		var t = "";
		var n, r, i;
		var s, o, u, a;
		var f = 0;
		e = e.replace(/[^A-Za-z0-9+/=]/g, "");
		while (f < e.length) {
			s = this._keyStr.indexOf(e.charAt(f++));
			o = this._keyStr.indexOf(e.charAt(f++));
			u = this._keyStr.indexOf(e.charAt(f++));
			a = this._keyStr.indexOf(e.charAt(f++));
			n = s << 2 | o >> 4;
			r = (o & 15) << 4 | u >> 2;
			i = (u & 3) << 6 | a;
			t = t + String.fromCharCode(n);
			if (u != 64) {
				t = t + String.fromCharCode(r)
			}
			if (a != 64) {
				t = t + String.fromCharCode(i)
			}
		}
		t = Base64._utf8_decode(t);
		return t
	},
	_utf8_encode: function(e) {
		e = e.replace(/rn/g, "n");
		var t = "";
		for (var n = 0; n < e.length; n++) {
			var r = e.charCodeAt(n);
			if (r < 128) {
				t += String.fromCharCode(r)
			} else if (r > 127 && r < 2048) {
				t += String.fromCharCode(r >> 6 | 192);
				t += String.fromCharCode(r & 63 | 128)
			} else {
				t += String.fromCharCode(r >> 12 | 224);
				t += String.fromCharCode(r >> 6 & 63 | 128);
				t += String.fromCharCode(r & 63 | 128)
			}
		}
		return t
	},
	_utf8_decode: function(e) {
		var t = "";
		var n = 0;
		var r = c1 = c2 = 0;
		while (n < e.length) {
			r = e.charCodeAt(n);
			if (r < 128) {
				t += String.fromCharCode(r);
				n++
			} else if (r > 191 && r < 224) {
				c2 = e.charCodeAt(n + 1);
				t += String.fromCharCode((r & 31) << 6 | c2 & 63);
				n += 2
			} else {
				c2 = e.charCodeAt(n + 1);
				c3 = e.charCodeAt(n + 2);
				t += String.fromCharCode((r & 15) << 12 | (c2 & 63) << 6 | c3 & 63);
				n += 3
			}
		}
		return t
	}
}
//UTF-8转UTF-16
function utf8ToUtf16(s) {
    if (!s) {
        return;
    }

    var i, codes, bytes, ret = [],
        len = s.length;
    for (i = 0; i < len; i++) {
        codes = [];
        codes.push(s.charCodeAt(i));
        if (((codes[0] >> 7) & 0xff) == 0x0) {
            //单字节  0xxxxxxx
            ret.push(s.charAt(i));
        } else if (((codes[0] >> 5) & 0xff) == 0x6) {
            //双字节  110xxxxx 10xxxxxx
            codes.push(s.charCodeAt(++i));
            bytes = [];
            bytes.push(codes[0] & 0x1f);
            bytes.push(codes[1] & 0x3f);
            ret.push(String.fromCharCode((bytes[0] << 6) | bytes[1]));
        } else if (((codes[0] >> 4) & 0xff) == 0xe) {
            //三字节  1110xxxx 10xxxxxx 10xxxxxx
            codes.push(s.charCodeAt(++i));
            codes.push(s.charCodeAt(++i));
            bytes = [];
            bytes.push((codes[0] << 4) | ((codes[1] >> 2) & 0xf));
            bytes.push(((codes[1] & 0x3) << 6) | (codes[2] & 0x3f));
            ret.push(String.fromCharCode((bytes[0] << 8) | bytes[1]));
        }
    }
    return ret.join('');

}

function currentTime() {
    var now = new Date();

    var year = now.getFullYear(); //年
    var month = now.getMonth() + 1; //月
    var day = now.getDate(); //日

    var hh = now.getHours(); //时
    var mm = now.getMinutes(); //分

    var clock = year + "年";

    if (month < 10)
        clock += "0";

    clock += month + "月";

    if (day < 10)
        clock += "0";

    clock += day + "日";

    if (hh < 10)
        clock += "0";

    clock += hh + ":";
    if (mm < 10) clock += '0';
    clock += mm;
    return (clock);
}

/**
 * 获取文件路径
 * @param {*} html 文件全称
 */
function getHtmlURL(html) {
    //弹出辅助窗格框
    var GetUrlPath = ()=> {
        var e = document.location.toString();
        return -1 != (e = decodeURI(e)).indexOf("/") && (e = e.substring(0, e.lastIndexOf("/"))), e
    }

    var url = GetUrlPath();

    if (url.length != 0) {
        url = url.concat("/" + html);
    } else {
        url = url.concat("./" + html);
    }
    return url;
}

/**
 * wps内弹出web页面
 * @param {*} html 文件名
 * @param {*} title 窗口标题
 * @param {*} hight 窗口高
 * @param {*} width 窗口宽
 */
function OnShowDialog(html, title, height, width, bModal) {
    var l_ActiveDoc = wps.WpsApplication().ActiveDocument;
    if (!l_ActiveDoc) {
        alert("WPS当前没有可操作文档！")
        return;
    }
    if (typeof bModal == "undefined" || bModal == null) {
        bModal = true;
    }
    width *= window.devicePixelRatio;
    height *= window.devicePixelRatio;
    var url = getHtmlURL(html);
    wps.ShowDialog(url, title, height, width, bModal);
}

/**
 * 解析返回response的参数
 * @param {*} resp
 * @return {*} body
 */
function handleResultBody(resp) {
    var result = "";
    if (resp.Body) {
        // 解析返回response的参数
    }
    return result;
}


/**
 * 判断WPS中的文件个数是否为0，若为0则关闭WPS函数
 * @param {*} name
 */
function closeWpsIfNoDocument() {
    var wpsApp = wps.WpsApplication();
    var docs = wpsApp.Documents;
    if (!docs || docs.Count == 0) {
        wps.ApiEvent.Cancel = true;
        //根据业务可以选择是否退出进程 wpsApp.Quit();
    }
}

function activeTab() {
    //启动WPS程序后，默认显示的工具栏选项卡为ribbon.xml中某一tab
    if (wps.ribbonUI)
        wps.ribbonUI.ActivateTab('WPSWorkExtTab');
}

function showOATab() {
    wps.PluginStorage.setItem("ShowOATabDocActive", pCheckIfOADoc()); //根据文件是否为OA文件来显示OA菜单
    wps.ribbonUI.Invalidate(); // 刷新Ribbon自定义按钮的状态
}

function getDemoTemplatePath() {
    var url = document.location.toString();
    url = decodeURI(url);
    if (url.indexOf("/") != -1) {
        url = url.substring(0, url.lastIndexOf("/"));
    }
    if (url.length !== 0)
        url = url.concat("/template/红头文件.docx");

    if (url.startsWith("file:///"))
        url = url.substr("file:///".length);
    return url;
}

function getDemoSealPath() {
    var url = document.location.toString();
    url = decodeURI(url);
    if (url.indexOf("/") != -1) {
        url = url.substring(0, url.lastIndexOf("/"));
    }
    if (url.length !== 0)
        url = url.concat("/template/OA模板：公章.png");

    if (url.startsWith("file:///"))
        url = url.substr("file:///".length);
    return url;
}

function pGetParamName(data, attr) {
    var start = data.indexOf(attr);
    data = data.substring(start + attr.length);
    return data;
}
/**
 * 从requst中获取文件名（确保请求中有filename这个参数）
 * @param {*} request 
 * @param {*} url 
 */
function pGetFileName(request, url) {
    var disposition = request.getResponseHeader("Content-Disposition");
    var filename = "";
    if (disposition) {
        var matchs = pGetParamName(disposition, "filename=");
        if (matchs) {
            filename = decodeURIComponent(matchs);
        } else {
            filename = "petro" + Date.getTime();
        }
    } else {
        var filename = url.substring(url.lastIndexOf("/") + 1);
    }
    return filename;
}

function StringToUint8Array(string) {
    var binLen, buffer, chars, i, _i;
    binLen = string.length;
    buffer = new ArrayBuffer(binLen);
    chars = new Uint8Array(buffer);
    for (var i = 0; i < binLen; ++i) {
        chars[i] = String.prototype.charCodeAt.call(string, i);
    }
    return buffer;
}
/**
 * WPS下载文件到本地打开（业务系统可根据实际情况进行修改）
 * @param {*} url 文件流的下载路径
 * @param {*} callback 下载后的回调
 */
function DownloadFile(url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            //需要业务系统的服务端在传递文件流时，确保请求中的参数有filename
            var fileName = pGetFileName(xhr, url)
            //落地打开模式下，WPS会将文件下载到本地的临时目录，在关闭后会进行清理
            var path = wps.Env.GetTempPath() + "/" + fileName
            var reader = new FileReader();
            reader.onload = function () {
                wps.FileSystem.writeAsBinaryString(path, reader.result);
                callback(path);
            };
            reader.readAsBinaryString(xhr.response);
        }
    }
    xhr.open('GET', url);
    xhr.responseType = 'blob';
    xhr.send();
}
/**
 * WPS上传文件到服务端（业务系统可根据实际情况进行修改，为了兼容中文，服务端约定用UTF-8编码格式）
 * @param {*} strFileName 上传到服务端的文件名称（包含文件后缀）
 * @param {*} strPath 上传文件的文件路径（文件在操作系统的绝对路径）
 * @param {*} uploadPath 上传文件的服务端地址
 * @param {*} strFieldName 业务调用方自定义的一些内容可通过此字段传递，默认赋值'file'
 * @param {*} OnSuccess 上传成功后的回调
 * @param {*} OnFail 上传失败后的回调
 */
function UploadFile(strFileName, strPath, uploadPath, strFieldName, OnSuccess, OnFail) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', uploadPath);

    var fileData = wps.FileSystem.readAsBinaryString(strPath);
    var data = new FakeFormData();
    if (strFieldName == "" || typeof strFieldName == "undefined"){//如果业务方没定义，默认设置为'file'
        strFieldName = 'file';
    }
    data.append(strFieldName, {
        name: utf16ToUtf8(strFileName), //主要是考虑中文名的情况，服务端约定用utf-8来解码。
        type: "application/octet-stream",
        getAsBinary: function () {
            return fileData;
        }
    });
    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4) {
            if (xhr.status == 200)
                OnSuccess(xhr.response)
            else
                OnFail(xhr.response);
        }
    };
    xhr.setRequestHeader("Cache-Control", "no-cache");
    xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
    if (data.fake) {
        xhr.setRequestHeader("Content-Type", "multipart/form-data; boundary=" + data.boundary);
        var arr = StringToUint8Array(data.toString());
        xhr.send(arr);
    } else {
        xhr.send(data);
    }
}

/**
 * 打开WPS后通知到业务系统，可根据需求扩展
 * @param {*} p_Url 业务方接受请求的地址
 */
function NotifyToServer(p_Url) {
    $.ajax({
        url: p_Url, //   URL + '/wps/wpsCanOpen',
        async: true,
        method: "post",
        dataType: 'json'
    });
}

/**
 * 更新编辑状态
 * @param {*} p_Url 要传入OA端，通知业务系统，当前文档所处的编辑状态的URL地址路径
 * @param {*} p_OpenUrl 当前文档从业务系统打开时的入口URL，这个URL包含业务系统开发者需要传入的ID等参数
 * @param {*} docId 文档id
 * @param {*} state 0-正在编辑中 1-文件保存 2-文件关闭  状态可根据需要进行自定义扩展
 */
function UpdateEditState(p_Url, p_OpenUrl, docId, state) {
    var formData = {
        "openUrl": p_OpenUrl,
        "docId": docId,
        "state": state
    };
    $.ajax({
        url: p_Url, //URL + '/document/stateMonitor',
        async: false,
        data: formData,
        method: "post",
        dataType: 'json',
        success: function (response) {
            if (response == "success") {
                console.log(response);
            }
        },
        error: function (response) {
            console.log(response);
        }
    });
}

/**
 * 作用：判断文档关闭后，如果系统已经没有打开的文档了，则设置回初始用户名
 */
function pSetWPSAppUserName() {
    //文档全部关闭的情况下，把WPS初始启动的用户名设置回去
    if (wps.WpsApplication().Documents.Count == 1) {
        var l_strUserName = wps.PluginStorage.getItem(constStrEnum.WPSInitUserName);
        wps.WpsApplication().UserName = l_strUserName;
    }
}

/**
 *  设置文档参数的属性值
 * @param {*} Doc 
 * @param {*} Key 
 * @param {*} Value 
 */
function SetDocParamsValue(Doc, Key, Value) {
    if (!Doc || !Key) {
        return;
    }

    var l_Params = wps.PluginStorage.getItem(Doc.DocID);
    if (!l_Params) {
        return;
    }

    var l_objParams = JSON.parse(l_Params);
    if (!(typeof(l_objParams) == "undefined")) {
        l_objParams[Key] = Value;
    }

    //把属性值整体再写回原来的文档ID中
    wps.PluginStorage.setItem(Doc.DocID, JSON.stringify(l_objParams));
}
