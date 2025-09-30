/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-27 16:33:29
 * @LastEditors: yihong Yh599598!@#
 * @LastEditTime: 2025-08-28 14:28:26 
 */
import Request from '@/api/lib/request';
import qs from "qs";

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
    formData.append("file", file);
    formData.append("processSerialNumber", processSerialNumber);
    formData.append("processInstanceId", processInstanceId);
    formData.append("taskId", taskId);
    formData.append("fileSource", fileSource);
    formData.append("describes", "1");
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
export function delAttachment(ids) {
    const params = {
        ids: ids
    };
    return flowableRequest({
        url: '/vue/attachment/delFile',
        method: 'post',
        params: params
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
export function saveOrder(idAndTabIndexs) {
    const params = {
        idAndTabIndexs: idAndTabIndexs
    };
    const data = qs.stringify(params);
    return flowableRequest({
        url: "/vue/attachment/saveOrder",
        method: 'post',
        data: data
    });
}
