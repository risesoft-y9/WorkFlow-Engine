/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-05-07 16:44:57
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-12 09:22:32
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\router\modules\printTemplateRouter.js
 */

const printTemplateRouter = {
    path: '/printTemplate',
    component: () => import('@/layouts/index.vue'),
    redirect: '/printTemplate',
    name: 'printTemplate',
    meta: {
        title: '打印模板',
        roles: ['systemAdmin'],
    },
    children: [
        {
            path: '/printTemplate',
            component: () => import('@/views/printTemplate/index.vue'),
            name: 'printTemplateIndex',
            meta: {
                title: '打印模板',
                icon: 'ri-printer-line',
                roles: ['systemAdmin'],
            }
        },
    ],
};

export default printTemplateRouter;
