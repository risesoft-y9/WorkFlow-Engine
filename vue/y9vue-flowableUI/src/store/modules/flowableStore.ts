/*
 * @Author: mengjuhua
 * @Date: 2022-10-25 16:45:31
 * @LastEditors: mengjuhua
 * @Description:
 */
import { defineStore } from 'pinia';

export const useFlowableStore = defineStore('flowableStore', {
    state: () => {
        return {
            itemId: '', //事项Id
            itemName: '', //事项名称
            itemInfo: {}, //事项信息
            draftCount: 0, //草稿箱数量
            wtodoCount: 0, //系统工单未处理数量
            wdoneCount: 0, //系统工单已处理数量
            todoCount: 0, //待办件数量
            doingCount: 0, //在办件数量
            doneCount: 0, //办结件数量
            draftRecycleCount: 0, //回收站数量
            monitorDoing: 0, //监控在办数量
            monitorDone: 0, //监控办结数量
            documentTitle: '', //文件标题
            customItem: false, //是否定制流程
            deptManage: false, //部门管理员
            monitorManage: false, //监控管理员
            itemList: [], //事项应用列表
            appType: 'item', //应用类型，item,chaosong,follow,search,monitorCenter
            currentPage: '1', //记录打开件的当前页，用于返回列表时打开当前页，返回时格式为1_back
            notReadCount: 0, //未阅件数量
            youjianCount: 0, //邮件未读数量
            followCount: 0, //我的关注数量
            isReload: true, //右侧路由刷新
            positionList: [], //岗位列表，用于切换岗位
            currentPositionId: '', //当前岗位id
            allCount: 0, //所有岗位待办数量
            currentCount: 0, //当前代办数
            currentPositionName: '', //当前岗位名称
            leaveManage: false //人事统计角色
        };
    },
    getters: {
        getItemId(state) {
            return state.itemId;
        },
        getItemName(state) {
            return state.itemName;
        },
        getItemInfo(state) {
            return state.itemInfo;
        },
        getItemList(state) {
            return state.itemList;
        },
        getDraftCount(state) {
            return state.draftCount;
        },
        getWtodoCount(state) {
            return state.wtodoCount;
        },
        getWdoneCount(state) {
            return state.wdoneCount;
        },
        getTodoCount(state) {
            return state.todoCount;
        },
        getDoingCount(state) {
            return state.doingCount;
        },
        getDoneCount(state) {
            return state.doneCount;
        },
        getDraftRecycleCount(state) {
            return state.draftRecycleCount;
        },
        getMonitorDoing(state) {
            return state.monitorDoing;
        },
        getMonitorDone(state) {
            return state.monitorDone;
        },
        getDocumentTitle(state) {
            return state.documentTitle;
        },
        getCustomItem(state) {
            return state.customItem;
        },

        getPositionList(state) {
            return state.positionList;
        },
        getCurrentPositionId(state) {
            return state.currentPositionId;
        },
        getCurrentCount(state) {
            return state.currentCount;
        },
        getCurrentPositionName(state) {
            return state.currentPositionName;
        },
        getAllCount(state) {
            return state.allCount;
        }
    },
    actions: {
        // documentTitle(state, { documentTitle }) {
        //     state.documentTitle = documentTitle
        // },
        // customItem(state, { customItem }) {
        //     state.customItem = customItem
        // },
    }
});
