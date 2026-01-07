/*
 * @Author: chensiwen cikl777@163.com
 * @Date: 2024-07-29 09:13:55
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2025-06-05 14:41:21
 * @Description: 存储共同数据
 */
import {ref} from 'vue';
import {commonSentencesList,getBindOpinionFrame} from '@/api/flowableUI/opinion';
import {getFormJson,getOptionValueList} from '@/api/flowableUI/form';


//常用语数据
export const commonSentencesData = ref<any>([]);

//意见框绑定数据
export const bindOpinionFrameData = ref<any>([]);

//表单json数据[{formId:formId,formJson:formJson}]格式，避免每次切换表单都重新获取
export const formJsonDataList = ref<any>([]);

//数据字典数据[{type:type,optionValue:optionValue}]格式，避免每次切换表单都重新获取
export const optionValueDataList = ref<any>([]);


export async function initCommonSentences() {
    if(commonSentencesData.value.length > 0) return;
    commonSentencesList().then(res => {
        if (res.success) {
            commonSentencesData.value = res.data;
        }
    });
}

export async function initBindOpinionFrame(itemId,processDefinitionId) {
    if(bindOpinionFrameData.value.length > 0) return;
    getBindOpinionFrame(itemId,processDefinitionId).then(res => {
        if (res.success) {
            bindOpinionFrameData.value = res.data;
        }
    });
}

export async function initBindOpinionFrame4await(itemId,processDefinitionId) {
    if(bindOpinionFrameData.value.length > 0) return;
    let res = await getBindOpinionFrame(itemId,processDefinitionId)
    if (res.success) {
        bindOpinionFrameData.value = res.data;
    }
}

export async function initFormJsonData(formId) {
    let formJsonData = formJsonDataList.value.find(item => item.formId === formId);
    if(formJsonData != undefined) return;
    let res = await getFormJson(formId);
    if (res.success && res.data != null) {
        let formJson = JSON.parse(res.data);
        let item = {formId:formId,formJson:formJson};
        formJsonDataList.value.push(item);
    }
    console.log("表单json数据",formJsonDataList.value);
}

export async function initOptionValueData(type) {
    let optionValueData = optionValueDataList.value.find(item => item.type === type);
    if(optionValueData != undefined) return;
    let res = await getOptionValueList(type);
    if (res.success) {
        let item = {type:type,optionValue:res.data};
        optionValueDataList.value.push(item);
    }
    console.log("数据字典数据",optionValueDataList.value);
}