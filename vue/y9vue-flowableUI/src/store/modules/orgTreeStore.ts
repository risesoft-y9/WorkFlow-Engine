/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-07-14 17:39:53
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-15 14:34:42
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\store\modules\orgTreeStore.ts
 */

import { defineStore } from 'pinia';
import { useCssModule } from 'vue';
import { $dataType } from '@/utils/object.ts';

export const useOrgTreeStore = defineStore('orgTreeStore', {
    state: () => {
        return {
            orgTreeSetting: {},
            initOrgTreeSetting: {
                treeId: 'orgTree', // 树id
                itemInterface: {
                    // 二（三）级节点接口传到组件内
                    api: '',
                    params: { parentId: '' },
                    callback: async (resData) => {
                        for (let i = 0; i < resData.length; i++) {
                            const item = resData[i];
                            switch (item.orgType) {
                                case 'Department': //部门
                                    item.title_icon = 'ri-slack-line';
                                    item.hasChild = true;
                                    let treeType = useOrgTreeStore().orgTreeSetting.itemInterface.params.treeType;
                                    if (treeType == 'Department') {
                                        item.checkbox = true;
                                    }
                                    break;
                                case 'Group': //组
                                    item.title_icon = 'ri-shield-star-line';
                                    break;
                                case 'Position': //岗位
                                    item.title_icon = 'ri-shield-user-line';
                                    item.checkbox = true;
                                    break;
                                case 'Person': //人员
                                    item.title_icon = 'ri-women-line';
                                    if (item.sex == 1) {
                                        item.title_icon = 'ri-men-line';
                                    }
                                    if (!item.original) {
                                        if (item.sex == 1) {
                                            item.title_icon = 'ri-men-line';
                                        } else {
                                            item.title_icon = 'ri-women-line';
                                        }
                                    }
                                    item.checkbox = true;
                                    break;
                            }

                            // 角色
                            switch (item.orgType) {
                                case 'role': //角色 人员
                                    item.title_icon = 'ri-group-line';
                                    item.checkbox = true;
                                    item.hasChild = false;
                                    break;
                                case 'folder': // 文件夹
                                    item.title_icon = 'ri-folder-2-line';
                                    item.hasChild = true;
                                    break;
                            }
                        }

                        return resData; // 如果有回调，则需要返回data - [Array类型]
                    }
                },
                itemGroupPrefix: 'itemGroup-', // 有 children节点的 ID 前缀（ 完整示例：'itemGroup-' + item.id ）
                data: [], // 一级接口接口返回的源数据
                itemInfo: {
                    keys: {
                        // 数据字段映射
                        id: 'id', // id
                        parentId: 'parentId', // parentId
                        name: 'name', // name
                        children: 'children', // children
                        hasChild: 'hasChild', // haschild
                        title: 'name', // title 映射接口数据的 name 示例
                        subTitle: 'name', // subtitle 映射接口数据的 name 示例
                        // 以下字段 如果有，则显示映射的值，否则不显示
                        checkbox: 'checkbox', // 复选框 实现每个 item 个性化显示复选框
                        title_icon: 'title_icon' // 实现每个 item 个性化显示 icon
                    },
                    render: {
                        subTitle: {
                            // subTitle
                            show: false // true 渲染
                        },
                        checkbox: {
                            // 复选框
                            func: (data) => {
                                // 复选框事件
                                // console.log('点击了复选框', data, data.checked ? '勾选状态' : '取消勾选状态');
                            }
                        },
                        mouse_over: {
                            // 鼠标悬停事件
                            func: (e) => {
                                if (e && e.originalTarget) {
                                    const li = e.originalTarget;
                                    if (li.className === 'treeItem') {
                                        li.style.backgroundColor = '#E7ECED';
                                    }
                                }
                            }
                        },
                        mouse_leave: {
                            // 鼠标离开事件
                            func: (e) => {
                                if (e && e.originalTarget) {
                                    const li = e.originalTarget;
                                    li.style.backgroundColor = '';
                                }
                            }
                        },
                        click: {
                            // 单击事件
                            func: (data) => {
                                // console.log('单击事件', data);
                            }
                        },
                        dbl_click: {
                            // 双击事件
                            func: (data) => {
                                // console.log('双击事件', data);
                            }
                        },
                        click_title_event: {
                            // 点击 title 事件
                            func: (data) => {
                                // 点击 title 事件
                                // console.log('点击了 title', data);
                            }
                        }
                    }
                },
                events: {
                    search: {
                        // 搜索功能
                        api: '',
                        params: {
                            // 搜索接口的参数对象
                            key: '' // 至少有这个属性，且必须为key
                        },
                        callback: (data) => {
                            /*
                                设置搜索结果的数据 注意保存原有数据
                            */
                            console.log('search - ', data);
                        }
                    }
                },
                style: {
                    li: {
                        // li activeClass
                        activeClassName: useCssModule('classes').active
                    },
                    animation: {
                        in: 'fadeInLeftBig',
                        out: 'fadeOutRight'
                    }
                }
            }
        };
    },
    getters: {
        getOrgTreeSetting: (state) => {
            return state.orgTreeSetting;
        }
    },
    actions: {
        // 树组件 - 一级节点接口数据
        // axios API 使用一级节点渲染树组件
        async getTree(interfacePromise) {
            try {
                let tree = [];
                if (interfacePromise && $dataType(interfacePromise == 'function')) {
                    tree = await interfacePromise();
                }
                const treeData = tree.data;
                for (let i = 0; i < treeData.length; i++) {
                    let item = treeData[i];
                    item.title_icon = 'ri-contacts-line';
                    if (item.orgType == undefined) {
                        //item.checkbox = true;
                    }
                    // 组织架构
                    switch (item.orgType) {
                        case 'Organization': //组织
                            item.title_icon = 'ri-stackshare-line';
                            item.hasChild = true;
                            let treeType = useOrgTreeStore().orgTreeSetting.itemInterface.params.treeType;
                            // if(treeType == 'Department'){
                            //     item.checkbox = true;
                            // }
                            break;
                        case 'Department': //组织
                            item.title_icon = 'ri-stackshare-line';
                            item.hasChild = true;
                            //let treeType = useOrgTreeStore().orgTreeSetting.itemInterface.params.treeType;
                            // if(treeType == 'Department'){
                            //     item.checkbox = true;
                            // }
                            break;
                        case 'App': //组织
                            item.title_icon = 'ri-stackshare-line';
                            item.hasChild = true;
                            let treeType1 = useOrgTreeStore().orgTreeSetting.itemInterface.params.treeType;
                            if (treeType1 == 'Role') {
                                //item.checkbox = true;
                            }
                            break;
                        case 'dynamicRole': //动态角色
                            item.title_icon = 'ri-group-line';
                            item.checkbox = true;
                            break;
                        default:
                            item.title_icon = 'ri-contacts-line';
                    }
                }
                this.orgTreeSetting.data = treeData;
            } catch (error) {
                console.log('publicTreeStore-error', error);
            }
        }
    }
});
