var varNtkoGUID = Math.random().toString(36);
var ntkoGloablValue = document.createElement("ntko-element");
ntkoGloablValue.setAttribute("id", "ntko-extensions");
document.documentElement.appendChild(ntkoGloablValue);
var userAgent = navigator.userAgent,
    rMsie = /(msie\s|trident.*rv:)([\w.]+)/,
    rFirefox = /(firefox)\/([\w.]+)/,
    rOpera = /(opera).+versi1on\/([\w.]+)/,
    rChrome = /(chrome)\/([\w.]+)/,
    rSafari = /version\/([\w.]+).*(safari)/;
var browser;
var version;
var ntkoWinOpen;
var timer;
var ua = userAgent.toLowerCase();

function uaMatch(ua) {
    var match = rMsie.exec(ua);
    if (match != null) {
        return {browser: "IE", version: match[2] || "0"};
    }
    var match = rFirefox.exec(ua);
    if (match != null) {
        return {browser: match[1] || "", version: match[2] || "0"};
    }
    var match = rOpera.exec(ua);
    if (match != null) {
        return {browser: match[1] || "", version: match[2] || "0"};
    }
    var match = rChrome.exec(ua);
    if (match != null) {
        return {browser: match[1] || "", version: match[2] || "0"};
    }
    var match = rSafari.exec(ua);
    if (match != null) {
        return {browser: match[2] || "", version: match[1] || "0"};
    }
    if (match != null) {
        return {browser: "", version: "0"};
    }
}

var browserMatch = uaMatch(userAgent.toLowerCase());
if (browserMatch.browser) {
    browser = browserMatch.browser;
    version = browserMatch.version;
}
var is360 = "";
var mimeTypes = navigator.mimeTypes;
version = parseInt(version);
for (var mt in mimeTypes) {
    if (mimeTypes[mt]["type"] == "application/vnd.chromium.remoting-viewer") {
        is360 = "360";
    }
}

function check360() {
    //application/vnd.chromium.remoting-viewer 可能为360特有
    var is360 = false;
    try {
        is360 = _mime("type", "application/vnd.chromium.remoting-viewer");
    } catch (e) {

    }

    if (isChrome() && is360) {
        return true;
    }
}

//检测是否是谷歌内核(可排除360及谷歌以外的浏览器)
function isChrome() {
    var ua = navigator.userAgent.toLowerCase();
    return ua.indexOf("chrome") > 1;
}

var bChromeExtensionInstalled = false;
if ("chrome" === browserMatch.browser) {
    var img;
    img = new Image();
    img.src = "chrome-extension://lppkeogbkjlmmbjenbogdndlgmpiddda/icons/ntko.png";
    img.onload = function () {
        bChromeExtensionInstalled = true;
    };
    img.onerror = function () {
        bChromeExtensionInstalled = false;
    };
}

function init() {
    if (browser == "IE") {
        return true;
    }
}

function installed() {
    if (browser == "IE") {
        return true;
    } else if ("firefox" === browserMatch.browser) {
        var bInstalled = false;
        if ("undefined" === typeof FirefoxInstalled) {
            return (bInstalled);
        } else {
            return (window.FirefoxInstalled());
        }
    } else if ("chrome" === browserMatch.browser) {
        /*if("360"===is360){
            console.log("360");
            return( bChromeExtensionInstalled );
            //return true;
        }else{*/
        return (bChromeExtensionInstalled);
        //return true;
        //}
    } else if (check360()) {
        return true;
    }
}

var ProductCaption = '北京有生博大软件股份有限公司'; //控件标题名称
var ntkoBrowserProductKey = 'B53A67F890B9E2E1F24C45B5179AC9098D293E7D';//控件标题秘钥
//var ntkoBrowserNoExpireKey = "南海区政府内部专用";//跨浏览器插件标题名称
//var ntkoBrowserOptions = "BFEB1527A7C41CFF0737DA53F0D82FC5A49E5A8E";//跨浏览器插件标题秘钥
function openWindow(strURL) {
    var testurl = window.location.search;
    var docUrl = document.location.toString();
    var wpathname = window.document.location.pathname;
    var pos = docUrl.indexOf(wpathname);
    var relUrl = docUrl.substring(0, pos);
    if ((null === strURL) || ("" === strURL)) {
        alert(" The Param strURL of openWindow() is NULL or empty. ");
        return;
    }

    var strLowser = strURL.toLowerCase();
    if ('/' != strURL.charAt(0)) {

        if (("http://" === strLowser.substr(0, 7)) || ("https://" === strLowser.substr(0, 8))) {
        } else {
            if (strURL.indexOf("../") == -1) {
                var url = strURL;
                var pathName = window.location.href;
                var varLength = pathName.lastIndexOf("/");
                strURL = pathName.substr(0, varLength + 1) + strURL;
                console.log("ifif:" + pathName);
                console.log("ifif+strURL:" + pathName + "===" + strURL);
                var index = find(pathName, "/", 3);
                var text = pathName.substr(0, index);
                strURL = text + url;
                console.log("indexstrURL:" + text);
            } else {
                var strURLlen = strURL.length;
                strURL = strURL.substring(2, strURLlen);
                strURL = relUrl + strURL;
                console.log("elseelse:" + strURLlen);
            }
        }
    } else {
        var pathName = window.location.href;
        console.log("else:" + pathName);
        var index = find(pathName, "/", 3);
        console.log("index:" + index);
        var text = pathName.substr(0, index);
        console.log("text:" + text);
        strURL = text + strURL;
        //var varLength = pathName.lastIndexOf("/");
        //strURL = pathName.substr( 0, varLength + 1 ) + strURL;
    }

    /*if(strURL.indexOf(testurl)==-1){
          if(strURL.indexOf("?")==-1){
              strURL=strURL+testurl;
          }else{
              var docof= strURL.indexOf("?");
                var docend=strURL.length;
                var docs=strURL.substring(0,docof);
                var docsurl=strURL.substring(docof+1,docend);
                strURL=docs+testurl+"&"+docsurl;
          }
      }*/
    var winTopHref;
    if (window.top != window.self) {
        //console.log("href="+ window.top.location.href);
        // winTopHref= window.top.location.href;
    } else {
        console.log("else=href=" + window.location.href);
        winTopHref = window.location.href;
    }

    var jsonValue = '{"OpenWindow":1,"URL":"';
    jsonValue += strURL;
    jsonValue += '","GUID":"';
    jsonValue += varNtkoGUID;
    jsonValue += '","URLMD5":"';
    jsonValue += b64_md5(strURL);
    jsonValue += '","SessionURL":"';
    jsonValue += winTopHref;
    jsonValue += '"';
    if ((typeof ProductCaption != 'undefined') && ((null != ProductCaption) && ("" != ProductCaption))) {
        jsonValue += ',"ProductCaption":"';
        jsonValue += ProductCaption;
        jsonValue += '"';
    }

    if ((typeof ntkoBrowserProductKey != 'undefined') && ((null != ntkoBrowserProductKey) && ("" != ntkoBrowserProductKey))) {
        jsonValue += ',"ProductKey":"';
        jsonValue += ntkoBrowserProductKey;
        jsonValue += '"';
    }

    if ((typeof ntkoBrowserNoExpireKey != 'undefined') && ((null != ntkoBrowserNoExpireKey) && ("" != ntkoBrowserNoExpireKey))) {
        jsonValue += ',"NoExpireKey":"';
        jsonValue += ntkoBrowserNoExpireKey;
        jsonValue += '"';
    }

    if (typeof ntkoBrowserOptions != 'undefined' && ((null != ntkoBrowserOptions) && ("" != ntkoBrowserOptions))) {
        jsonValue += ',"Options":"';
        jsonValue += ntkoBrowserOptions;
        jsonValue += '"';
    }
    jsonValue += '}';
    console.log(strURL);
    console.log(browser);
    if (browser == "IE") {
        var ntkowin = b64_md5(strURL);
        var ntkowidth = window.screen.width;
        var ntkoheight = window.screen.height;
        //ntkoWinOpen=window.open(strURL,"","height="+ntkowidth+", width="+ntkowidth+", titlebar=no,toolbar =no, menubar=no, scrollbars=no, resizable=yes, location=no, status=no");
        //ntkoWinOpen.focus();//子窗口获取焦点
    }
    if (browser == "firefox") {
        if (version >= 50) {
            console.log(jsonValue);
            console.log(version);
            window.postMessage({type: "FROM_NTKO_PAGE", text: jsonValue}, "*");
        } else {
            console.log("40:" + version);
            window.open(strURL, "", "height=759, width=1440, titlebar=no,toolbar =no, menubar=no, scrollbars=no, resizable=yes, location=no, status=no,channelmode=yes");
        }
    }
    if (browser == "chrome") {
        //console.log("8888888888"+jsonValue);
        /* if("360"===is360){
             console.log("is360");
             window.postMessage({ type: "FROM_NTKO_PAGE", text: jsonValue }, "*");
         }else{*/
        if (version >= 50) {
            //console.log(jsonValue);
            console.log("xxx");
            window.postMessage({type: "FROM_NTKO_PAGE", text: jsonValue}, "*");
        }/*else{
						window.open(strURL,"","height=759, width=1440, titlebar=no,toolbar =no, menubar=no, scrollbars=no, resizable=yes, location=no, status=no,channelmode=yes");	 
				 }	*/
        /* }*/
    }
}

function find(str, cha, num) {
    var x = str.indexOf(cha);
    for (var i = 0; i < num; i++) {
        x = str.indexOf(cha, x + 1);
    }
    return x;
}

function ntkoClose(strURL) {
    var testurl = window.location.search;
    var docUrl = document.location.toString();
    var wpathname = window.document.location.pathname;
    var pos = docUrl.indexOf(wpathname);
    var relUrl = docUrl.substring(0, pos);
    if ((null === strURL) || ("" === strURL)) {
        alert(" The Param strURL of openWindow() is NULL or empty. ");
        return;
    }

    var strLowser = strURL.toLowerCase();
    if ('/' != strURL.charAt(0)) {

        if (("http://" === strLowser.substr(0, 7)) || ("https://" === strLowser.substr(0, 8))) {
        } else {
            if (strURL.indexOf("../") == -1) {
                var pathName = window.location.href;
                var varLength = pathName.lastIndexOf("/");
                strURL = pathName.substr(0, varLength + 1) + strURL;
            } else {
                var strURLlen = strURL.length;
                strURL = strURL.substring(3, strURLlen);
                strURL = relUrl + strURL;
            }
        }
    } else {
        var pathName = window.location.href;
        var varLength = pathName.lastIndexOf("/");
        strURL = pathName.substr(0, varLength + 1) + strURL;
    }

    if (strURL.indexOf(testurl) == -1) {
        if (strURL.indexOf("?") == -1) {
            strURL = strURL + testurl;
        } else {
            var docof = strURL.indexOf("?");
            var docend = strURL.length;
            var docs = strURL.substring(0, docof);
            var docsurl = strURL.substring(docof + 1, docend);
            strURL = docs + testurl + "&" + docsurl;
        }
    }


    var jsonValue = '{"Close":1,"GUID":"';
    jsonValue += varNtkoGUID;
    jsonValue += '","URLMD5":"';
    jsonValue += b64_md5(strURL);
    jsonValue += '"}';
    if (browser == "IE") {
        window.close();
    }
    if (browser == "firefox") {
        if (version >= 50) {
            window.postMessage({type: "FROM_NTKO_PAGE", text: jsonValue}, "*");
        }/*else{
				ntkoWinOpen.close();			
			}	*/
    }
    if (browser == "chrome") {
        /*if("360"===is360){
            console.log("is360");
            window.postMessage({ type: "FROM_NTKO_PAGE", text: jsonValue }, "*");
        }else{*/
        if (version >= 50) {
            window.postMessage({type: "FROM_NTKO_PAGE", text: jsonValue}, "*");
        }/*else{
						ntkoWinOpen.close();			
					}	*/
        /* }*/

    }
}

if (browser != "IE") {
    window.addEventListener("message", function (event) {
        if (event.source != window)
            return;
        if (event.data.type && (event.data.type == "FROM_NTKO_CONTEXT_PAGE")) {
            var vJsonData = JSON.parse(event.data.text);
            var jsonData = vJsonData["ntkoData"];
            if (typeof jsonData !== "undefined") {
                if (varNtkoGUID === jsonData["ntkoGUID"]) {
                    if ("ntkoClose" === jsonData["functionName"]) {
                        // 调用关闭事件
                        //eval('ntkoCloseEvent()');
                        //ntkoCloseEvent();
                    } else if ("ntkoReturnValueToParentPage" === jsonData["functionName"]) {
                        // 调用返回值事件,解析值
                        var varFunctionName = jsonData["parentExecutionFunction"];		// 要执行的函数名

                        if (typeof varFunctionName != 'undefined' && ((null != varFunctionName) && ("" != varFunctionName))) {
                            var varFunctionAgrvs = jsonData["FunctionArgs"];			// 传递过来的参数值


                            if (typeof varFunctionAgrvs != 'undefined' && ((null != varFunctionAgrvs) && ("" != varFunctionAgrvs))) {
                                // 调用eval执行函数
                                eval(varFunctionName + "( varFunctionAgrvs[0], varFunctionAgrvs[1], varFunctionAgrvs[2], varFunctionAgrvs[3]," +
                                    "varFunctionAgrvs[4],varFunctionAgrvs[5],varFunctionAgrvs[6],varFunctionAgrvs[7],varFunctionAgrvs[8]);");
                            }
                        }

                    }
                }

            }
        }
    }, false);

}
if (browser == "IE") {
    if (version <= 8) {
    } else {
        window.addEventListener("message", function (event) {
            if (event.data.type == "ntkowordie") {
                var str = event.data.text;
                var Jsondata = JSON.parse(str);
                var jsonData = Jsondata["ntkodata"];
                var varFunctionAgrvsArray = Jsondata["ntkotext"];
                var varFunctionAgrvs = varFunctionAgrvsArray.split(",");
                if (typeof jsonData !== "undefined") {
                    if (typeof varFunctionAgrvs != 'undefined' && ((null != varFunctionAgrvs) && ("" != varFunctionAgrvs))) {
                        eval(jsonData + "( varFunctionAgrvs[0], varFunctionAgrvs[1], varFunctionAgrvs[2], varFunctionAgrvs[3]," +
                            "varFunctionAgrvs[4],varFunctionAgrvs[5],varFunctionAgrvs[6],varFunctionAgrvs[7],varFunctionAgrvs[8]);");
                    }
                }
            }
        }, false);
    }
}

//ie子页面往父页面传值
function ieattachEventntko(data, varFunctionAgrvs) {
    if (typeof data !== "undefined") {
        if (typeof varFunctionAgrvs != 'undefined' && ((null != varFunctionAgrvs) && ("" != varFunctionAgrvs))) {
            eval(data + "( varFunctionAgrvs[0], varFunctionAgrvs[1], varFunctionAgrvs[2], varFunctionAgrvs[3]," +
                "varFunctionAgrvs[4],varFunctionAgrvs[5],varFunctionAgrvs[6],varFunctionAgrvs[7],varFunctionAgrvs[8]);");
        }
    }

}

var hexcase = 0;  /* hex output format. 0 - lowercase; 1 - uppercase        */
var b64pad = ""; /* base-64 pad character. "=" for strict RFC compliance   */
var chrsz = 8;  /* bits per input character. 8 - ASCII; 16 - Unicode      */

function hex_md5(s) {
    return binl2hex(core_md5(str2binl(s), s.length * chrsz));
}

function b64_md5(s) {
    return binl2b64(core_md5(str2binl(s), s.length * chrsz));
}

function str_md5(s) {
    return binl2str(core_md5(str2binl(s), s.length * chrsz));
}

function hex_hmac_md5(key, data) {
    return binl2hex(core_hmac_md5(key, data));
}

function b64_hmac_md5(key, data) {
    return binl2b64(core_hmac_md5(key, data));
}

function str_hmac_md5(key, data) {
    return binl2str(core_hmac_md5(key, data));
}

function md5_vm_test() {
    return hex_md5("abc") == "900150983cd24fb0d6963f7d28e17f72";
}

/*
 * Calculate the MD5 of an array of little-endian words, and a bit length
 */
function core_md5(x, len) {
    /* append padding */
    x[len >> 5] |= 0x80 << ((len) % 32);
    x[(((len + 64) >>> 9) << 4) + 14] = len;

    var a = 1732584193;
    var b = -271733879;
    var c = -1732584194;
    var d = 271733878;

    for (var i = 0; i < x.length; i += 16) {
        var olda = a;
        var oldb = b;
        var oldc = c;
        var oldd = d;

        a = md5_ff(a, b, c, d, x[i + 0], 7, -680876936);
        d = md5_ff(d, a, b, c, x[i + 1], 12, -389564586);
        c = md5_ff(c, d, a, b, x[i + 2], 17, 606105819);
        b = md5_ff(b, c, d, a, x[i + 3], 22, -1044525330);
        a = md5_ff(a, b, c, d, x[i + 4], 7, -176418897);
        d = md5_ff(d, a, b, c, x[i + 5], 12, 1200080426);
        c = md5_ff(c, d, a, b, x[i + 6], 17, -1473231341);
        b = md5_ff(b, c, d, a, x[i + 7], 22, -45705983);
        a = md5_ff(a, b, c, d, x[i + 8], 7, 1770035416);
        d = md5_ff(d, a, b, c, x[i + 9], 12, -1958414417);
        c = md5_ff(c, d, a, b, x[i + 10], 17, -42063);
        b = md5_ff(b, c, d, a, x[i + 11], 22, -1990404162);
        a = md5_ff(a, b, c, d, x[i + 12], 7, 1804603682);
        d = md5_ff(d, a, b, c, x[i + 13], 12, -40341101);
        c = md5_ff(c, d, a, b, x[i + 14], 17, -1502002290);
        b = md5_ff(b, c, d, a, x[i + 15], 22, 1236535329);

        a = md5_gg(a, b, c, d, x[i + 1], 5, -165796510);
        d = md5_gg(d, a, b, c, x[i + 6], 9, -1069501632);
        c = md5_gg(c, d, a, b, x[i + 11], 14, 643717713);
        b = md5_gg(b, c, d, a, x[i + 0], 20, -373897302);
        a = md5_gg(a, b, c, d, x[i + 5], 5, -701558691);
        d = md5_gg(d, a, b, c, x[i + 10], 9, 38016083);
        c = md5_gg(c, d, a, b, x[i + 15], 14, -660478335);
        b = md5_gg(b, c, d, a, x[i + 4], 20, -405537848);
        a = md5_gg(a, b, c, d, x[i + 9], 5, 568446438);
        d = md5_gg(d, a, b, c, x[i + 14], 9, -1019803690);
        c = md5_gg(c, d, a, b, x[i + 3], 14, -187363961);
        b = md5_gg(b, c, d, a, x[i + 8], 20, 1163531501);
        a = md5_gg(a, b, c, d, x[i + 13], 5, -1444681467);
        d = md5_gg(d, a, b, c, x[i + 2], 9, -51403784);
        c = md5_gg(c, d, a, b, x[i + 7], 14, 1735328473);
        b = md5_gg(b, c, d, a, x[i + 12], 20, -1926607734);

        a = md5_hh(a, b, c, d, x[i + 5], 4, -378558);
        d = md5_hh(d, a, b, c, x[i + 8], 11, -2022574463);
        c = md5_hh(c, d, a, b, x[i + 11], 16, 1839030562);
        b = md5_hh(b, c, d, a, x[i + 14], 23, -35309556);
        a = md5_hh(a, b, c, d, x[i + 1], 4, -1530992060);
        d = md5_hh(d, a, b, c, x[i + 4], 11, 1272893353);
        c = md5_hh(c, d, a, b, x[i + 7], 16, -155497632);
        b = md5_hh(b, c, d, a, x[i + 10], 23, -1094730640);
        a = md5_hh(a, b, c, d, x[i + 13], 4, 681279174);
        d = md5_hh(d, a, b, c, x[i + 0], 11, -358537222);
        c = md5_hh(c, d, a, b, x[i + 3], 16, -722521979);
        b = md5_hh(b, c, d, a, x[i + 6], 23, 76029189);
        a = md5_hh(a, b, c, d, x[i + 9], 4, -640364487);
        d = md5_hh(d, a, b, c, x[i + 12], 11, -421815835);
        c = md5_hh(c, d, a, b, x[i + 15], 16, 530742520);
        b = md5_hh(b, c, d, a, x[i + 2], 23, -995338651);

        a = md5_ii(a, b, c, d, x[i + 0], 6, -198630844);
        d = md5_ii(d, a, b, c, x[i + 7], 10, 1126891415);
        c = md5_ii(c, d, a, b, x[i + 14], 15, -1416354905);
        b = md5_ii(b, c, d, a, x[i + 5], 21, -57434055);
        a = md5_ii(a, b, c, d, x[i + 12], 6, 1700485571);
        d = md5_ii(d, a, b, c, x[i + 3], 10, -1894986606);
        c = md5_ii(c, d, a, b, x[i + 10], 15, -1051523);
        b = md5_ii(b, c, d, a, x[i + 1], 21, -2054922799);
        a = md5_ii(a, b, c, d, x[i + 8], 6, 1873313359);
        d = md5_ii(d, a, b, c, x[i + 15], 10, -30611744);
        c = md5_ii(c, d, a, b, x[i + 6], 15, -1560198380);
        b = md5_ii(b, c, d, a, x[i + 13], 21, 1309151649);
        a = md5_ii(a, b, c, d, x[i + 4], 6, -145523070);
        d = md5_ii(d, a, b, c, x[i + 11], 10, -1120210379);
        c = md5_ii(c, d, a, b, x[i + 2], 15, 718787259);
        b = md5_ii(b, c, d, a, x[i + 9], 21, -343485551);

        a = safe_add(a, olda);
        b = safe_add(b, oldb);
        c = safe_add(c, oldc);
        d = safe_add(d, oldd);
    }
    return Array(a, b, c, d);

}

/*
 * These functions implement the four basic operations the algorithm uses.
 */
function md5_cmn(q, a, b, x, s, t) {
    return safe_add(bit_rol(safe_add(safe_add(a, q), safe_add(x, t)), s), b);
}

function md5_ff(a, b, c, d, x, s, t) {
    return md5_cmn((b & c) | ((~b) & d), a, b, x, s, t);
}

function md5_gg(a, b, c, d, x, s, t) {
    return md5_cmn((b & d) | (c & (~d)), a, b, x, s, t);
}

function md5_hh(a, b, c, d, x, s, t) {
    return md5_cmn(b ^ c ^ d, a, b, x, s, t);
}

function md5_ii(a, b, c, d, x, s, t) {
    return md5_cmn(c ^ (b | (~d)), a, b, x, s, t);
}

/*
 * Calculate the HMAC-MD5, of a key and some data
 */
function core_hmac_md5(key, data) {
    var bkey = str2binl(key);
    if (bkey.length > 16) bkey = core_md5(bkey, key.length * chrsz);

    var ipad = Array(16), opad = Array(16);
    for (var i = 0; i < 16; i++) {
        ipad[i] = bkey[i] ^ 0x36363636;
        opad[i] = bkey[i] ^ 0x5C5C5C5C;
    }

    var hash = core_md5(ipad.concat(str2binl(data)), 512 + data.length * chrsz);
    return core_md5(opad.concat(hash), 512 + 128);
}

/*
 * Add integers, wrapping at 2^32. This uses 16-bit operations internally
 * to work around bugs in some JS interpreters.
 */
function safe_add(x, y) {
    var lsw = (x & 0xFFFF) + (y & 0xFFFF);
    var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
    return (msw << 16) | (lsw & 0xFFFF);
}

/*
 * Bitwise rotate a 32-bit number to the left.
 */
function bit_rol(num, cnt) {
    return (num << cnt) | (num >>> (32 - cnt));
}

/*
 * Convert a string to an array of little-endian words
 * If chrsz is ASCII, characters >255 have their hi-byte silently ignored.
 */
function str2binl(str) {
    var bin = Array();
    var mask = (1 << chrsz) - 1;
    for (var i = 0; i < str.length * chrsz; i += chrsz)
        bin[i >> 5] |= (str.charCodeAt(i / chrsz) & mask) << (i % 32);
    return bin;
}

/*
 * Convert an array of little-endian words to a string
 */
function binl2str(bin) {
    var str = "";
    var mask = (1 << chrsz) - 1;
    for (var i = 0; i < bin.length * 32; i += chrsz)
        str += String.fromCharCode((bin[i >> 5] >>> (i % 32)) & mask);
    return str;
}

/*
 * Convert an array of little-endian words to a hex string.
 */
function binl2hex(binarray) {
    var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
    var str = "";
    for (var i = 0; i < binarray.length * 4; i++) {
        str += hex_tab.charAt((binarray[i >> 2] >> ((i % 4) * 8 + 4)) & 0xF) +
            hex_tab.charAt((binarray[i >> 2] >> ((i % 4) * 8)) & 0xF);
    }
    return str;
}

/*
 * Convert an array of little-endian words to a base-64 string
 */
function binl2b64(binarray) {
    var tab = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    var str = "";
    for (var i = 0; i < binarray.length * 4; i += 3) {
        var triplet = (((binarray[i >> 2] >> 8 * (i % 4)) & 0xFF) << 16)
            | (((binarray[i + 1 >> 2] >> 8 * ((i + 1) % 4)) & 0xFF) << 8)
            | ((binarray[i + 2 >> 2] >> 8 * ((i + 2) % 4)) & 0xFF);
        for (var j = 0; j < 4; j++) {
            if (i * 8 + j * 6 > binarray.length * 32) str += b64pad;
            else str += tab.charAt((triplet >> 6 * (3 - j)) & 0x3F);
        }
    }
    return str;
}