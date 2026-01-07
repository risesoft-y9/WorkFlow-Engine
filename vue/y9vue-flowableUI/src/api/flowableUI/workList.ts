import Request from '@/api/lib/request';
import qs from 'qs';

var flowableRequest = new Request();

export function getAllCountItems(itemId) {
    const params = {
        itemId
    };
    return flowableRequest({
        url: '/vue/main/getCount4Item',
        method: 'get',
        params
    })
}

//获取待办列表
export function getTodoList(itemId, searchTerm, page, rows) {
    const params = {
        itemId: itemId,
        searchTerm: searchTerm,
        page: page,
        rows: rows
    };
    return flowableRequest({
        url: "/vue/workList/todoList",
        method: 'get',
        params: params
    });
}

//获取在办列表
export function getDoingList(itemId, searchTerm, page, rows) {
    const params = {
        itemId: itemId,
        searchTerm: searchTerm,
        page: page,
        rows: rows
    };
    return flowableRequest({
        url: "/vue/workList/doingList",
        method: 'get',
        params: params
    });
}

//获取办结列表
export function getDoneList(itemId, searchTerm, page, rows) {
    const params = {
        itemId: itemId,
        searchTerm: searchTerm,
        page: page,
        rows: rows
    };
    return flowableRequest({
        url: "/vue/workList/doneList",
        method: 'get',
        params: params
    });
}

/**
 *
 * 获取视图
 * @param itemId 事项id
 * @param viewType 视图类型
 * @returns
 */
export function viewConf(itemId, viewType) {
    const params = {
        itemId,
        viewType
    };
    return flowableRequest({
        url: "/vue/viewConf/list",
        method: 'get',
        params: params
    });
}

/**
 *
 * @param itemId 获取综合查询列表
 * @param state
 * @param createDate
 * @param tableName
 * @param searchMapStr
 * @param page
 * @param rows
 * @returns
 */
export function getQueryList(itemId, state, createDate, tableName, searchMapStr, page, rows) {
    const params = {
        itemId: itemId,
        state: state,
        createDate: createDate,
        tableName: tableName,
        // searchMapStr: searchMapStr,
        page: page,
        rows: rows
    };
    const data = qs.stringify(params);
    let formData = new FormData();
    formData.append("itemId", itemId);
    formData.append("state", state);
    formData.append("createDate", createDate);
    formData.append("tableName", tableName);
    formData.append("searchMapStr", searchMapStr);
    formData.append("page", page);
    formData.append("rows", rows);
    return flowableRequest({
        url: "/vue/workList/queryList",
        method: 'post',
        cType: false,
        headers: {
            'Content-Type': 'application/json'
        },
        data: formData
    });
} 