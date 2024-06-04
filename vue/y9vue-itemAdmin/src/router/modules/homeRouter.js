/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-27 16:46:41
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-06-27 16:46:41
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\router\modules\homeRouter.js
 */
/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-02 16:24:51
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-06-27 15:14:42
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\router\modules\homeRouter.js
 */

const homeRouter = {
    path: '/index',
    component: () => import('@/layouts/index.vue'),
    redirect: '/homeIndex',
    name: 'home',
    meta: {
        title: '首页',
        roles: ['systemAdmin']
    },
    children: [
        {
            path: '/homeIndex',
            component: () => import('@/views/home/index3.vue'),
            name: 'homeIndex',
            meta: {
                title: '首页',
                icon: 'ri-home-3-line',
                roles: ['systemAdmin']
            }
        }
    ]
};

export default homeRouter;
