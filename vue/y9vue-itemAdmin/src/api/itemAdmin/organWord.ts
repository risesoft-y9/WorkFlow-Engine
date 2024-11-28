/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-08-03 18:10:09
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\api\itemAdmin\organWord.js
 */

import Request from '@/api/lib/request';
import qs from 'qs';

var itemAdminRequest = new Request();

export const organWordApi = {
    //获取编号列表
    organWordList() {
        const params = {};
        return itemAdminRequest({
            url: '/vue/organWord/organWordList',
            method: 'get',
            params: params
        });
    },

    //获取编号信息
    getOrganWord(id) {
        const params = {
            id: id
        };
        return itemAdminRequest({
            url: '/vue/organWord/getOrganWord',
            method: 'get',
            params: params
        });
    },

    //判断标识是否可用，true为可用
    checkCustom(id, custom) {
        const params = {
            id: id,
            custom: custom
        };
        return itemAdminRequest({
            url: '/vue/organWord/checkCustom',
            method: 'get',
            params: params
        });
    },

    //保存编号
    saveOrganWord(organWord) {
        const data = qs.stringify(organWord);
        return itemAdminRequest({
            url: '/vue/organWord/saveOrUpdate',
            method: 'post',
            data: data
        });
    },

    //删除编号
    removeOrganWord(organWordId) {
        const params = {
            organWordId: organWordId
        };
        return itemAdminRequest({
            url: '/vue/organWord/removeOrganWords',
            method: 'post',
            params: params
        });
    },

    //获取机关代字列表
    propertyList(organWordId) {
        const params = {
            organWordId: organWordId
        };
        return itemAdminRequest({
            url: '/vue/organWordProperty/propertyList',
            method: 'get',
            params: params
        });
    },

    //获取机关代字信息
    getOrganWordProperty(id) {
        const params = {
            id: id
        };
        return itemAdminRequest({
            url: '/vue/organWordProperty/getOrganWordProperty',
            method: 'get',
            params: params
        });
    },

    //保存机关代字
    saveProperty(organWordProperty) {
        const data = qs.stringify(organWordProperty);
        return itemAdminRequest({
            url: '/vue/organWordProperty/save',
            method: 'post',
            data: data
        });
    },

    //删除机关代字
    removeProperty(propertyIds) {
        const params = {
            propertyIds: propertyIds
        };
        return itemAdminRequest({
            url: '/vue/organWordProperty/removeProperty',
            method: 'post',
            params: params
        });
    }
};
