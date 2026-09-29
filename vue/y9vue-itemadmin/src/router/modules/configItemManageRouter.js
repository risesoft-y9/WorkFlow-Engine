/*
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-05-07 16:44:57
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-06-04 16:56:35
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-flowable\vue\y9vue-itemAdmin\src\router\modules\configItemManageRouter.js
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
                roles: ['systemAdmin']
            }
        },
        {
            path: '/dynamicRole',
            component: () => import('@/views/dynamicRole/index.vue'),
            name: 'dynamicRoleIndex',
            meta: {
                title: '动态角色',
                icon: 'ri-user-add-line',
                roles: ['systemAdmin']
            }
        },
        {
            path: '/opinionFrame',
            component: () => import('@/views/opinionFrame/index.vue'),
            name: 'opinionFrameIndex',
            meta: {
                title: '意见框管理',
                icon: 'ri-archive-drawer-line',
                roles: ['systemAdmin']
            }
        },
        {
            path: '/organWord',
            component: () => import('@/views/organWord/index.vue'),
            name: 'organWordIndex',
            meta: {
                title: '编号管理',
                icon: 'ri-numbers-line',
                roles: ['systemAdmin']
            }
        },
        {
            path: '/buttonManage',
            component: () => import('@/views/buttonManage/index.vue'),
            name: 'buttonManageIndex',
            meta: {
                title: '按钮管理',
                icon: 'ri-device-line',
                roles: ['systemAdmin']
            }
        },
        {
            path: '/optionClass',
            component: () => import('@/views/optionClass/index.vue'),
            name: 'optionClassIndex',
            meta: {
                title: '数据字典',
                icon: 'ri-price-tag-2-line',
                roles: ['systemAdmin']
            }
        },
        {
            path: '/viewType',
            component: () => import('@/views/viewType/index.vue'),
            name: 'viewTypeIndex',
            meta: {
                title: '视图类型',
                icon: 'ri-eye-line',
                roles: ['systemAdmin']
            }
        }
    ]
};

export default configItemManageRouter;
