/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-05-07 16:44:57
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-06-27 15:14:46
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\router\modules\itemListRouter.js
 */

const sendReceiveRouter = {
    path: '/sendReceive',
    component: () => import('@/layouts/index.vue'),
    redirect: '/sendReceive',
    name: 'sendReceive',
    meta: {
        title: '收发管理',
        roles: ['systemAdmin'],
    },
    children: [
        {
            path: '/sendReceive',
            component: () => import('@/views/sendReceive/index.vue'),
            name: 'sendReceiveIndex',
            meta: {
                title: '收发管理',
                icon: 'ri-user-voice-line',
                roles: ['systemAdmin'],
            }
        },
    ],
};

export default sendReceiveRouter;
