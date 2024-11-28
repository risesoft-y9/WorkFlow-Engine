/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2021-05-27 10:54:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-15 11:52:27
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\api\itemAdmin\item\permConfig.ts
 */

import Request from '@/api/lib/request';

var itemAdminRequest = new Request();

//获取任务配置信息
export function getBpmList(processDefinitionId, itemId) {
    const params = {
        processDefinitionId: processDefinitionId,
        itemId: itemId
    };
    return itemAdminRequest({
        url: '/vue/itemPerm/getBpmList',
        method: 'get',
        params: params
    });
}

//获取绑定的权限列表
export function getBindList(itemId, processDefinitionId, taskDefKey) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey
    };
    return itemAdminRequest({
        url: '/vue/itemPerm/getBindList',
        method: 'get',
        params: params
    });
}

//删除权限
export function deleteBind(id) {
    const params = {
        id: id
    };
    return itemAdminRequest({
        url: '/vue/itemPerm/delete',
        method: 'post',
        params: params
    });
}

//获取动态角色
export function dynamicRole() {
    const params = {};
    return itemAdminRequest({
        url: '/vue/dynamicRole/list',
        method: 'get',
        params: params
    });
}

//获取角色
export function getRole() {
    const params = {};
    return itemAdminRequest({
        url: '/vue/role/findRole',
        method: 'get',
        params: params
    });
}

export function getRoleById(param) {
    const params = {
        id: param.parentId
    };
    return itemAdminRequest({
        url: '/vue/role/findRole',
        method: 'get',
        params: params
    });
}

//获取部门树
export function getDept(param) {
    const params = {
        id: param == undefined ? '' : param.parentId
    };
    return itemAdminRequest({
        url: '/vue/department/findDeptById',
        method: 'get',
        params: params
    });
}

/**
 *
 * @param param 搜索岗位树
 * @returns
 */
export function searchDeptAndPosition(param) {
    const params = {
        name: param == undefined ? '' : param.key
    };
    return itemAdminRequest({
        url: '/vue/department/searchDeptAndPosition',
        method: 'get',
        params: params
    });
}

//获取组织机构
export function getOrgList(param) {
    const params = {};
    return itemAdminRequest({
        url: '/vue/department/getOrgList',
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
    return itemAdminRequest({
        url: '/vue/department/getOrgTree',
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
    return itemAdminRequest({
        url: '/vue/department/treeSearch',
        method: 'get',
        params: params
    });
}

//获取人员树
export function getDeptPerson(param) {
    const params = {
        id: param == undefined ? '' : param.parentId
    };
    return itemAdminRequest({
        url: '/vue/department/findDeptAndUserById',
        method: 'get',
        params: params
    });
}

/**
 *
 * @param param 搜索部门树
 * @returns
 */
export function searchDept(param) {
    const params = {
        name: param == undefined ? '' : param.key
    };
    return itemAdminRequest({
        url: '/vue/department/searchDept',
        method: 'get',
        params: params
    });
}

//保存权限角色
export function saveBind(itemId, processDefinitionId, taskDefKey, roleId, roleType) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId,
        taskDefKey: taskDefKey,
        roleId: roleId,
        roleType: roleType
    };
    return itemAdminRequest({
        url: '/vue/itemPerm/saveBind',
        method: 'post',
        params: params
    });
}

//复制上一版本授权
export function copyPerm(itemId, processDefinitionId) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId
    };
    return itemAdminRequest({
        url: '/vue/itemPerm/copyPerm',
        method: 'post',
        params: params
    });
}

//清空授权
export function removePerm(itemId, processDefinitionId) {
    const params = {
        itemId: itemId,
        processDefinitionId: processDefinitionId
    };
    return itemAdminRequest({
        url: '/vue/itemPerm/removePerm',
        method: 'post',
        params: params
    });
}
