/*
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 16:33:29
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-06-29 14:45:29
 * @Descripttion: 委托设置 
 * @FilePath: \y9-flowable\vue\y9vue-flowableUI\src\api\flowableUI\entrustManage.ts
 */
import Request from '@/api/lib/request';

var flowableRequest = new Request();

/**
 * 获取委托列表
 */
export function getEntrustList() {
    const params = {};
    return flowableRequest({
        url: '/vue/entrust/getEntrustList',
        method: 'get',
        params: params
    });
}

/**
 * 删除委托
 * @param id
 * @returns
 */
export function deleteEntrust(id) {
    const params = {
        id
    };
    return flowableRequest({
        url: '/vue/entrust/deleteEntrust',
        method: 'POST',
        params: params
    });
}

/**
 * 保存委托
 * @param jsonData
 * @returns
 */
export function saveOrUpdate(jsonData) {
    const params = {
        jsonData: jsonData
    };
    return flowableRequest({
        url: '/vue/entrust/saveOrUpdate',
        method: 'post',
        params: params
    });
}

//获取组织机构
export function getOrgList(param) {
    const params = {};
    return flowableRequest({
        url: '/vue/entrust/getOrgList',
        method: 'get',
        params: params
    });
}

//获取不同类型的组织树
export function getOrgTree(param) {
    const params = {
        id: param.parentId,
        treeType: param.treeType
    };
    return flowableRequest({
        url: '/vue/entrust/getOrgTree',
        method: 'get',
        params: params
    });
}

//查询不同类型的组织树
export function treeSearch(param) {
    const params = {
        name: param.key,
        treeType: param.treeType
    };
    return flowableRequest({
        url: '/vue/entrust/treeSearch',
        method: 'get',
        params: params
    });
}
