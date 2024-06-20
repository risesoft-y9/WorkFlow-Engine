/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-05-07 16:44:57
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-06-27 15:14:46
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\router\modules\itemListRouter.js
 */

const itemListRouter = {
    path: '/item',
    component: () => import('@/layouts/index.vue'),
    redirect: '/itemList',
    name: 'item',
    meta: {
        title: '事项配置',
        roles: ['systemAdmin'],
    },
    children: [
        {
            path: '/itemList',
            component: () => import('@/views/item/itemList.vue'),
            name: 'itemList',
            meta: {
                title: '事项配置',
                icon: 'ri-database-2-line',
                roles: ['systemAdmin'],
            },
        },
    ],
};

export default itemListRouter;
