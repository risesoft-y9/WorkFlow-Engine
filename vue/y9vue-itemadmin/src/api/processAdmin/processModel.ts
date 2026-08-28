/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-01-08 14:19:08
 * @FilePath: \vue\y9vue-itemAdmin\src\api\processAdmin\processModel.ts
 */

import Request from '@/api/lib/request4Process';

var processAdminRequest = new Request();

//获取部署列表
export const getModelList = async () => {
    const params = {
        resourceId: ''
    };
    return await processAdminRequest({
        url: '/vue/processModel/getModelList',
        method: 'get',
        params: params
    });
};

/**
 * 获取流程设计XML
 */
export const getModelXml = async (modelId) => {
    const params = {
        modelId
    };
    return await processAdminRequest({
        url: '/vue/processModel/getModelXml',
        method: 'get',
        params: params
    });
};

//删除流程定义
export const deleteModel = async (modelId) => {
    const params = {
        modelId: modelId
    };
    return await processAdminRequest({
        url: '/vue/processModel/deleteModel',
        method: 'post',
        params: params
    });
};

//创建流程设计
export const createModel = async (name, key, description) => {
    const params = {
        name: name,
        key: key,
        description: description
    };
    return await processAdminRequest({
        url: '/vue/processModel/create',
        method: 'post',
        params: params
    });
};

//部署流程设计
export const deployModel = async (modelId) => {
    const params = {
        modelId: modelId
    };
    return await processAdminRequest({
        url: '/vue/processModel/deployModel',
        method: 'post',
        params: params
    });
};

export const importModel = async (params) => {
    var data = new FormData();
    data.append('file', params.file);
    return await processAdminRequest({
        url: '/vue/processModel/import',
        method: 'POST',
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        cType: false,
        data: data
    });
};
