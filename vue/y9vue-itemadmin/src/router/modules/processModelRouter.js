/*
 * @Author: hongzhew
 * @Date: 2022-03-31 20:14:43
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-06-08 14:41:29
 * @Description:
 */
const processModelRouter = {
    path: '/model',
    component: () => import('@/layouts/index.vue'),
    redirect: '/processModel',
    name: 'model',
    meta: {
        title: '流程设计',
        roles: ['systemAdmin']
    },
    children: [
        {
            path: '/processModel',
            component: () => import('@/views/processModel/index.vue'),
            name: 'processModel',
            meta: {
                title: '流程设计',
                icon: 'ri-list-settings-fill',
                roles: ['systemAdmin']
            }
        }
    ]
};

export default processModelRouter;
