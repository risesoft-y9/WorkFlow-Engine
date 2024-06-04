/**
 * 判断是否是外链
 * @param {string} path
 * @returns {Boolean}
 * @author LiQingSong
 */
export const isExternal = (path: string): boolean => {
  return /^(https?:|mailto:|tel:)/.test(path);
};

/**
 * @description 判断是否为空
 * @param value
 * @returns {boolean}
 */
 export function isBlank(value) {
  return (
    value === null ||
    false ||
    value === '' 
    //value.trim() === '' ||
    //value.toLocaleLowerCase().trim() === 'null'
  )
}

/**
 * 校验正则
 * @param {string} type
 * @param {string} value
 * @returns {Boolean}
 * @author Fuyu
 */
export function $validCheck(type:string,value:string) :boolean{
	
	let regular = "";//正则
	
	switch(type){
		case 'email'://邮箱
			regular = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]w+)*$/;
			break;
		case 'phone'://11位手机号
			regular = /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/;
			break;
	}
	
	return value ? regular.test(value) : regular;
}
