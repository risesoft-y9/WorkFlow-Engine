/*
 * @version:
 * @Author: zhangchongjie
 * @Date: 2024-05-11 16:39:47
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-06-29 14:46:58
 * @Descripttion:  个人常用语 
 * @FilePath: \y9-flowable\vue\y9vue-flowableUI\src\api\flowableUI\opinion.ts
 */
import Request from '@/api/lib/request';
import { OpinionFrameParam, OpinionParam } from './dto';

var flowableRequest = new Request();
//获取个人常用语
export function commonSentencesList() {
    const params = {};
    return flowableRequest({
        url: '/vue/commonSentences/list',
        method: 'get',
        params: params
    });
}

//保存个人常用语
export function saveCommonSentences(content) {
    const params = {
        content: content
    };
    return flowableRequest({
        url: '/vue/commonSentences/save',
        method: 'POST',
        params: params
    });
}

/**
 * 更新常用语使用次数
 * @param id
 * @returns
 */
export function updateUseNumber(id) {
    const params = {
        id
    };
    return flowableRequest({
        url: '/vue/commonSentences/updateUseNumber',
        method: 'POST',
        params: params
    });
}

//修改个人常用语
export function editCommonSentences(content, tabIndex) {
    const params = {
        content: content,
        tabIndex: tabIndex
    };
    return flowableRequest({
        url: '/vue/commonSentences/saveEdit',
        method: 'POST',
        params: params
    });
}

//删除个人常用语
export function delCommonSentences(tabIndex) {
    const params = {
        tabIndex: tabIndex
    };
    return flowableRequest({
        url: '/vue/commonSentences/remove',
        method: 'POST',
        params: params
    });
}

//获取新增或编辑意见前数据
export function personalComment(id) {
    const params = {
        id: id
    };
    return flowableRequest({
        url: '/vue/opinion/newOrModify/personalComment',
        method: 'get',
        params: params
    });
}

//获取意见框绑定列表
export function getBindOpinionFrame(itemId, processDefinitionId) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId
    };
    return flowableRequest({
        url: '/vue/opinion/getBindOpinionFrame',
        method: 'get',
        params: params
    });
}

//获取意见列表
export function getOpinionList(param: OpinionFrameParam) {
    return flowableRequest({
        url: '/vue/opinion/personCommentList',
        method: 'post',
        data: param,
        cType: true
    });
}

//保存意见
export function saveOpinion(param: OpinionParam) {
    return flowableRequest({
        url: '/vue/opinion/saveOrUpdate',
        method: 'post',
        data: param,
        cType: true
    });
}

//删除意见
export function delOpinion(id) {
    const params = {
        id: id
    };
    return flowableRequest({
        url: '/vue/opinion/delete',
        method: 'post',
        params: params
    });
}
