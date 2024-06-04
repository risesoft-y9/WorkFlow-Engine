/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-05-07 16:44:57
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-12 09:24:40
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\router\modules\calendarRouter.js
 */

const configItemManageRouter = {
    path: '/',
    component: () => import('@/layouts/index.vue'),
    redirect: '/',
    name: 'configItemManage',
    meta: {
        title: '配置项管理',
        roles: ['systemAdmin'],
        icon: 'ri-tools-line'
    },
    children: [
        {
            path: '/calendar',
            component: () => import('@/views/calendar/index.vue'),
            name: 'calendarIndex',
            meta: {
                title: '日历配置',
                icon: 'ri-calendar-2-line',
                roles: ['systemAdmin'],
            }
        },
        {
            path: '/dynamicRole',
            component: () => import('@/views/dynamicRole/index.vue'),
            name: 'dynamicRoleIndex',
            meta: {
                title: '动态角色',
                icon: 'ri-user-add-line',
                roles: ['systemAdmin'],
            }
        },
        {
            path: '/opinionFrame',
            component: () => import('@/views/opinionFrame/index.vue'),
            name: 'opinionFrameIndex',
            meta: {
                title: '意见框管理',
                icon: 'ri-archive-drawer-line',
                roles: ['systemAdmin'],
            }
        },
        {
            path: '/wordTemplate',
            component: () => import('@/views/wordTemplate/index.vue'),
            name: 'wordTemplateIndex',
            meta: {
                title: '正文模板',
                icon: 'ri-file-word-line',
                roles: ['systemAdmin'],
            }
        },
        {
            path: '/taoHongTemplate',
            component: () => import('@/views/taoHongTemplate/index.vue'),
            name: 'taoHongTemplateIndex',
            meta: {
                title: '套红模板',
                icon: 'ri-file-word-2-line',
                roles: ['systemAdmin'],
            }
        },
        {
            path: '/organWord',
            component: () => import('@/views/organWord/index.vue'),
            name: 'organWordIndex',
            meta: {
                title: '编号管理',
                icon: 'ri-numbers-line',
                roles: ['systemAdmin'],
            }
        },
        {
            path: '/interface',
            component: () => import('@/views/interface/index.vue'),
            name: 'interfaceIndex',
            meta: {
                title: '接口管理',
                icon: 'ri-git-pull-request-line',
                roles: ['systemAdmin'],
            }
        },
        {
            path: '/linkInfo',
            component: () => import('@/views/linkInfo/index.vue'),
            name: 'linkInfoIndex',
            meta: {
                title: '链接管理',
                icon: 'ri-link',
                roles: ['systemAdmin'],
            }
        },
        {
            path: '/buttonManage',
            component: () => import('@/views/buttonManage/index.vue'),
            name: 'buttonManageIndex',
            meta: {
                title: '按钮管理',
                icon: 'ri-device-line',
                roles: ['systemAdmin'],
            }
        },
        {
            path: '/optionClass',
            component: () => import('@/views/optionClass/index.vue'),
            name: 'optionClassIndex',
            meta: {
                title: '数据字典',
                icon: 'ri-price-tag-2-line',
                roles: ['systemAdmin'],
            }
        },
        {
            path: '/viewType',
            component: () => import('@/views/viewType/index.vue'),
            name: 'viewTypeIndex',
            meta: {
                title: '视图类型',
                icon: 'ri-eye-line',
                roles: ['systemAdmin'],
            }
        },
    ],
};

export default configItemManageRouter;
