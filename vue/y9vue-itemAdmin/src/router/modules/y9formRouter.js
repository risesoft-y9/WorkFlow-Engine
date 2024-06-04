/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-14 10:06:53
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-12 09:27:19
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\router\modules\y9formRouter.js
 */

const y9formRouter = {
    path: '/y9form',
    component: () => import('@/layouts/index.vue'),
    redirect: '/y9form/index',
    name: 'y9form',
    meta: {
        title: '表单管理',
        roles: ['systemAdmin'],
    },
    children: [
        {
            path: '/y9form/index',
            component: () => import('@/views/y9form/index.vue'),
            name: 'y9formIndex',
            meta: {
                title: '表单管理',
                icon: 'ri-table-line',
                roles: ['systemAdmin'],
            },
        },
    ],
};

export default y9formRouter;
