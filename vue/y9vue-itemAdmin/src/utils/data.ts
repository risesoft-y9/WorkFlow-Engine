/**
 * 全局数据
 */
import { useDictionaryStore } from "@/store/modules/dictionaryStore"

export const $dictionaryFunc = async (type) => {//请求字典表的方法
	await useDictionaryStore().getDictionaryList(type)

}

export const $dictionary = () => { //获取字典表
	return useDictionaryStore().$state.dictionary
 
}

