
/**
 * 判断是否是移动端
 * @returns {Boolean}
 * @author Yehaifeng
 */
export const isMobile = ():Boolean => {
    return /Android|webOS| iPhone | iPad | iPod |BlackBerry|opera mini|opera mobile|appleWebkit.*mobile|mobile/i.test(
        navigator.userAgent)
}

/**
 * 根据key（必须为数字）值排序  
 * 使用示例 array.sort(compare("key"))
 * @returns {Array}
 * @author Yehaifeng
 */
 export const compare = function (key) {
    return function (obj1,obj2) {
        let val1 = obj1[key];
        let val2 = obj2[key];
        if (!isNaN(Number(val1)) && !isNaN(Number(val2))) {
            val1 = Number(val1);
            val2 = Number(val2);
        }
        if (val1 < val2) {
            return -1;
        }else if(val1 > val2){
            return 1;
        }else{
            return 0;
        }
    }
  }

  /**
 * 随机生成字符串  
 * 使用示例 randomString(6) 生成6位的字符串
 * @returns {String}
 * @author Yehaifeng
 */
 export const randomString = (e) => {
    var e = e || 32,
        t = "ABCDEFGHJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789",
        a = t.length,
        n = "";
    for (let i = 0; i < e; i++) n += t.charAt(Math.floor(Math.random() * a));
    return n
    
}

 /**
 * 防抖函数
 * 使用示例 debounce(fun,wait)  fun:事件处理函数， wait:延迟时间
 * @returns {String}
 * @author Yehaifeng
 */
export const debounce = (fun: Function,wait: number):Function => { 
    var timer; //维护全局纯净，借助闭包来实现
    return function () {
        if (timer) {  //timer有值为真，这个事件已经触发了一次，重新开始计数
            clearTimeout(timer); 
        }
        timer = setTimeout(function () {
            fun();
        }, wait);
    }
}

 /**
 * 获取url中对应参数的值
 * @param  url        链接
 * @param  queryName  参数名
 * @returns String
 * @author Y9
 */
  export function getQueryValue(url, queryName) {
    if (url.indexOf("?") === -1) {
        return null;
    }
    let reg = new RegExp("(^|&)" + queryName + "=([^&]*)(&|$)", "i");
    let r = url.split("?")[1].match(reg);
    if ( r != null ){
       return decodeURI(r[2]);
    }else{
       return null;
    }
}

/***字体的像素设置函数 */
export function getConcreteSize(fontSize: string, actualValue: number) {
    switch (fontSize) {
        case 'large':
            return Math.floor(actualValue * 1.3);
        case 'small':
            if(Math.floor(actualValue * 0.9) > 12){
                return Math.floor(actualValue * 0.9);
            }else {
                return 12;
            }
        case 'default':
        case  'medium':
            return Math.floor(actualValue * 1);
        default:
            break;
    }
}
