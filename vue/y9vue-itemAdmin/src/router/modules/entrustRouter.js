/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-05-07 16:44:57
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-12 09:23:09
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\router\modules\entrustRouter.js
 */

const entrustRouter = {
    path: '/entrust',
    component: () => import('@/layouts/index.vue'),
    redirect: '/entrust',
    name: 'entrust',
    meta: {
        title: '出差委托',
        roles: ['systemAdmin'],
    },
    children: [
        {
            path: '/entrust',
            component: () => import('@/views/entrust/index.vue'),
            name: 'entrustIndex',
            meta: {
                title: '出差委托',
                icon: 'ri-user-shared-line',
                roles: ['systemAdmin'],
            }
        },
    ],
};

export default entrustRouter;
