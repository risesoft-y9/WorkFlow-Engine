/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-02 16:44:07
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-06-08 14:41:17
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-processAdmin\src\router\modules\processControlRouter.js
 */
const processManageRouter = {
    path: '/modelNew',
    component: () => import('@/layouts/index.vue'),
    redirect: '/processModelNew',
    name: 'processManage',
    meta: {
        title: '流程管理',
        roles: ['systemAdmin'],
        icon: 'ri-file-hwp-line'
    },
    children: [
        {
            path: '/processModelNew',
            component: () => import('@/views/processModelNew/index.vue'),
            name: 'processModelNew',
            meta: {
                title: '流程设计',
                icon: 'ri-list-settings-fill',
                roles: ['systemAdmin'] 
            },
        },
        {
            path: '/processDeploy',
            component: () => import('@/views/processDeploy/index.vue'),
            name: 'processDeploy',
            meta: {
                title: '流程部署',
                icon: 'ri-install-line',
                roles: ['systemAdmin'],
            },
        },
        {
            path: '/processControl',
            component: () => import('@/views/processControl/index.vue'),
            name: 'processControl',
            meta: {
                title: '流程监控',
                icon: 'ri-sound-module-line',
                roles: ['systemAdmin'] 
            },
        }
    ]
};

export default processManageRouter;
