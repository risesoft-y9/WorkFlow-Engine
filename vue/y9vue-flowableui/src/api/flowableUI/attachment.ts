/*
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 16:33:29
 * @LastEditors: mengjuhua
 * @LastEditTime: 2026-06-29 14:44:35
 * @Descripttion: 附件列表 
 * @FilePath: \y9-flowable\vue\y9vue-flowableUI\src\api\flowableUI\attachment.ts
 */
import Request from '@/api/lib/request';
import { IdsParam } from './dto';

var flowableRequest = new Request();

//获取附件列表
export function getAttachmentList(processSerialNumber, page, rows) {
    const params = {
        processSerialNumber: processSerialNumber,
        fileSource: '',
        page: page,
        rows: rows
    };
    return flowableRequest({
        url: '/vue/attachment/getAttachmentList',
        method: 'get',
        params: params
    });
}

//保存附件
export function saveAttachment(processSerialNumber, processInstanceId, taskId, fileSource, file) {
    let formData = new FormData();
    formData.append('file', file);
    formData.append('processSerialNumber', processSerialNumber);
    formData.append('processInstanceId', processInstanceId);
    formData.append('taskId', taskId);
    formData.append('fileSource', fileSource);
    formData.append('describes', '1');
    return flowableRequest({
        url: '/vue/attachment/upload',
        method: 'post',
        data: formData
    });
}

//下载附件
export function download(id) {
    const params = {
        id: id
    };
    return flowableRequest({
        url: '/vue/attachment/download',
        method: 'get',
        params: params
    });
}

//删除附件
export function delAttachment(params: IdsParam) {
    return flowableRequest({
        url: '/vue/attachment/delFile',
        method: 'post',
        data: params,
        cType: true
    });
}

/**
 * 获取附件配置
 * @param attachmentType
 * @returns
 */
export function getAttachmentConfig(attachmentType) {
    const params = {
        attachmentType: attachmentType
    };
    return flowableRequest({
        url: '/vue/attachment/getAttachmentConfig',
        method: 'get',
        params: params
    });
}

//保存排序
export function saveOrder(param: IdsParam) {
    return flowableRequest({
        url: '/vue/attachment/saveOrder',
        method: 'post',
        data: param,
        cType: true
    });
}
