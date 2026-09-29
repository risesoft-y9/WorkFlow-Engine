/*
 * @Author: your name
 * @Date: 2021-12-22 15:41:55
 * @LastEditTime: 2024-01-02 09:22:25
 * @LastEditors: zhangchongjie
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\router\checkRole.js
 */
import { getItemList, getPositionList } from '@/api/flowableUI/index';
import router, { asyncRoutes } from '@/router';
import { useRouterStore } from '@/store/modules/routerStore';
import { useFlowableStore } from '@/store/modules/flowableStore';

/**
 * 根据 meta.role 判断当前用户是否有权限
 * @param roles 用户的权限
 * @param route 路由
 */
function hasPermission(roles, route) {
    if (route.meta && route.meta.roles) {
        return roles.some((role) => route.meta.roles.includes(role));
    } else {
        return true;
    }
}

/**
 * 根据权限 - 递归过滤异步路由 - 深度优先遍历 - 留下有权限的路由
 * @param routes asyncRoutes
 * @param roles
 */
function filterAsyncRoutes(routes, roles) {
    const res = [];

    routes.forEach((route) => {
        const tmp = { ...route };
        if (hasPermission(roles, tmp)) {
            if (tmp.children) {
                tmp.children = filterAsyncRoutes(tmp.children, roles);
            }
            res.push(tmp);
        }
    });
    return res;
}

/**
 * 获取对应权限路由
 * @param routes asyncRoutes
 * @param roles
 */
export function getPermissionRoutes(rolesArr = ['systemAdmin']) {
    const routerStore = useRouterStore();
    const roles = rolesArr;
    const permissionRoutes = filterAsyncRoutes(asyncRoutes, roles);
    // 项目存储中心 pinia - routerStore模块 存储有权限的所有路由源数据，permissionRoutes即包含项目所有可跳转的路由
    routerStore.$patch({
        PermissionRoutes: permissionRoutes
    });
    return permissionRoutes;
}

const lazy = (path) => {
    // return import.meta.glob(`./${path}`);
    return () => import(`@/views/${path}.vue`);
};

// 查询路由权限
export async function checkRole(rolesArr = ['user']) {
    // 获取权限路由
    const permissionRoutes = getPermissionRoutes(rolesArr);
    const flowableStore = useFlowableStore();
    if (permissionRoutes.length !== 0) {
        await permissionRoutes.map((route) => {
            router.addRoute(route);
        });

        //岗位处理
        let positionId = sessionStorage.getItem('positionId');
        console.log('***********************positionId:' + positionId);
        if (positionId == null || positionId == 'null') {
            let pres = await getPositionList('count');
            if (pres.success) {
                flowableStore.$patch({
                    positionList: pres.data.positionList,
                    currentPositionId: pres.data.positionList.length > 0 ? pres.data.positionList[0].id : '',
                    currentCount: flowableStore.positionList.length > 0 ? flowableStore.positionList[0].todoCount : 0,
                    currentPositionName: flowableStore.positionList.length > 0 ? flowableStore.positionList[0].name : 0
                });
                sessionStorage.setItem(
                    'positionId',
                    flowableStore.positionList.length > 0 ? flowableStore.positionList[0].id : ''
                );
                sessionStorage.setItem(
                    'positionName',
                    flowableStore.positionList.length > 0 ? flowableStore.positionList[0].name : ''
                );
            }
        } else if (flowableStore.positionList.length == 0) {
            let pres = await getPositionList('count');
            if (pres.success) {
                flowableStore.$patch({
                    positionList: pres.data.positionList,
                    currentPositionId: positionId
                });
            }
        }
        //岗位处理

        let itemList = ref([]);
        let notReadCount = 0;
        let youjianCount = 0;
        let monitorManage = false;
        // let followCount = 0;
        if (flowableStore.itemList.length == 0) {
            let data = await getLeftMenu();
            itemList.value = data.itemMap;
            notReadCount = data.notReadCount;
            youjianCount = data.youjianCount;
            monitorManage = data.monitorManage;
            // followCount = data.followCount;
            flowableStore.$patch({
                itemList: data.itemMap,
                notReadCount: notReadCount,
                youjianCount: youjianCount,
                monitorManage: monitorManage,
                leaveManage: data.leaveManage,
                appType: itemList.value.length > 0 ? itemList.value[0].url : 'yuejian'
                //itemName:itemList.value.length > 0 ? itemList.value[0].name : '阅件'
                // followCount:followCount
            });
        } else {
            itemList.value = flowableStore.itemList;
            notReadCount = flowableStore.notReadCount;
            youjianCount = flowableStore.youjianCount;
            // followCount = flowableStore.followCount;
        }

        //每个流程所包含的菜单项
        let children = [
            {
                path: '/workIndex/add',
                name: 'workIndex_add',
                meta: { title: '新建', icon: 'ri-file-add-line', roles: ['user'] }
            },
            {
                path: '/workIndex/draft',
                name: 'workIndex_draft',
                meta: { title: '草稿箱', icon: 'ri-draft-line', roles: ['user'] }
            },
            {
                path: '/workIndex/todo',
                name: 'workIndex_todo',
                meta: { title: '待办件', icon: 'ri-article-line', roles: ['user'] }
            },
            {
                path: '/workIndex/doing',
                name: 'workIndex_doing',
                meta: { title: '在办件', icon: 'ri-repeat-fill', roles: ['user'] }
            },
            {
                path: '/workIndex/done',
                name: 'workIndex_done',
                meta: { title: '办结件', icon: 'ri-record-circle-line', roles: ['user'] }
            },
            {
                path: '/workIndex/draftRecycle',
                name: 'workIndex_draftRecycle',
                meta: { title: '回收站', icon: 'ri-delete-bin-line', roles: ['user'] }
            }
        ];

        //事项菜单项集成
        itemList.value.forEach((item) => {
            let obj = {
                meta: { title: '', icon: 'ri-function-line', roles: ['user'] },
                path: '',
                name: '',
                todoCount: 0,
                children: []
            };
            if (item.name == '报销办件') {
                obj.meta.icon = 'ri-money-cny-box-line';
            } else if (item.name == '对公支付') {
                obj.meta.icon = 'ri-exchange-cny-fill';
            } else if (item.name == '发票申请') {
                obj.meta.icon = 'ri-currency-line';
            } else if (item.name == '人事办件') {
                obj.meta.icon = 'ri-account-pin-box-line';
            } else if (item.name == '公文办件') {
                obj.meta.icon = 'ri-function-line';
            } else if (item.name == '项目备案') {
                obj.meta.icon = 'ri-projector-line';
            }
            obj.meta.title = item.name;
            // obj.meta.icon = item.iconData;
            obj.path = '/workIndex/work';
            children[2].todoCount = item.todoCount;
            obj.children = JSON.parse(JSON.stringify(children));
            for (let c of obj.children) {
                c.parentTitle = item.name;
                c.itemId = item.url;
            }
            obj.name = item.url;
            obj.todoCount = item.todoCount;
            // obj.component = lazy(obj.component);
            permissionRoutes.push(obj);
            router.addRoute(obj);
        });

        let flowableUI_Async_Routes = [
            {
                meta: { title: '阅件', icon: 'ri-message-2-line', roles: ['user'] },
                path: '/workIndex/csTodo',
                todoCount: notReadCount,
                name: 'yuejian',
                component: () => import('@/layouts/index.vue'),
                children: [
                    {
                        path: '/workIndex/csTodo',
                        parentTitle: '阅件',
                        component: () => import('@/views/chaoSong/csTodo.vue'),
                        name: 'workIndex_csTodo',
                        meta: { title: '未阅件', icon: 'ri-message-2-line', roles: ['user'] }
                    },
                    {
                        path: '/workIndex/csDone',
                        parentTitle: '阅件',
                        component: () => import('@/views/chaoSong/csDone.vue'),
                        name: 'workIndex_csDone',
                        meta: { title: '已阅件', icon: 'ri-chat-check-line', roles: ['user'] }
                    }
                    // {
                    //     path: '/workIndex/csPiyue',
                    //     parentTitle: '阅件',
                    //     component: () => import('@/views/chaoSong/csPiyue.vue'),
                    //     name: 'workIndex_csPiyue',
                    //     meta: { title: '批阅件', icon: 'ri-chat-heart-line', roles: ['user'] },
                    // },
                ]
            },
            // {meta:{title:'公务邮件',icon:'ri-mail-volume-line',roles:['user']},todoCount:youjianCount,path:'/workIndex/email',name:'email',children:[]},
            {
                meta: { title: '我的关注', icon: 'ri-star-line', roles: ['user'] },
                parentTitle: '我的关注',
                path: '/workIndex/follow',
                name: 'follow',
                children: [],
                component: () => import('@/views/follow/followList.vue')
            },
            {
                meta: { title: '综合搜索', icon: 'ri-search-line', roles: ['user'] },
                path: '/workIndex/searchList',
                name: 'search',
                component: () => import('@/layouts/index.vue'),
                children: [
                    {
                        path: '/workIndex/searchList',
                        parentTitle: '综合搜索',
                        component: () => import('@/views/search/searchList.vue'),
                        name: 'workIndex_searchList',
                        meta: { title: '个人所有件', icon: 'ri-file-list-line', roles: ['user'] }
                    },
                    {
                        path: '/workIndex/yuejianList',
                        parentTitle: '综合搜索',
                        component: () => import('@/views/search/yuejianList.vue'),
                        name: 'workIndex_yuejianList',
                        meta: { title: '阅件', icon: 'ri-chat-poll-line', roles: ['user'] }
                    }
                    // {path: '/workIndex/emailList',parentTitle:'综合搜索',component: () => import('@/views/search/emailList.vue'),name: 'workIndex_emailList',meta: {title: '公务邮件',icon: 'ri-mail-line',roles: ['user']}}
                ]
            }
        ];
        if (flowableStore.leaveManage) {
            //人事统计
            flowableUI_Async_Routes.push({
                meta: { title: '人事统计', icon: 'ri-bar-chart-2-line', roles: ['user'] },
                parentTitle: '人事统计',
                path: '/workIndex/leaveCount',
                name: 'leaveCount',
                children: [],
                component: () => import('@/views/leaveCount/index.vue')
            });
        }
        if (flowableStore.monitorManage) {
            //监控管理员
            flowableUI_Async_Routes.push({
                meta: { title: '监控中心', icon: 'ri-vidicon-2-line', roles: ['user'] },
                path: '/workIndex/monitorBanjian',
                name: 'monitorCenter',
                component: () => import('@/layouts/index.vue'),
                children: [
                    {
                        path: '/workIndex/monitorBanjian',
                        parentTitle: '监控中心',
                        component: () => import('@/views/monitor/monitorBanjian.vue'),
                        name: 'workIndex_monitorBanjian',
                        meta: { title: '监控办件', icon: 'ri-file-search-line', roles: ['user'] }
                    },
                    {
                        path: '/workIndex/monitorChaosong',
                        parentTitle: '监控中心',
                        component: () => import('@/views/monitor/monitorChaosong.vue'),
                        name: 'workIndex_monitorChaosong',
                        meta: { title: '监控阅件', icon: 'ri-video-chat-line', roles: ['user'] }
                    }
                ]
            });
        }
        flowableUI_Async_Routes.forEach((item) => {
            permissionRoutes.push(item);
            router.addRoute(item);
        });

        return permissionRoutes;
    } else {
        // console.log("没有权限");
        return false;
    }
}

//获取工作台左侧菜单数据
async function getLeftMenu() {
    const res = await getItemList(); //获取菜单列表
    return res.data;
}
