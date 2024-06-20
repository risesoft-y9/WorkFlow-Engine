/*
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-27 16:46:41
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-06-03 17:04:55
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\router\modules\docIntroRouter.js
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

const docIntroRouter = {
    path: '/docIntro',
    component: () => import('@/layouts/index.vue'),
    redirect: '/docIntro',
    name: 'docIntro',
    meta: {
        title: '操作说明',
        roles: ['systemAdmin'],
    },
    children: [
        {
            path: '/docIntro',
            component: () => import('@/views/docIntro/index.vue'),
            name: 'docIntroIndex',
            meta: {
                title: '操作说明',
                icon: 'ri-train-line',
                roles: ['systemAdmin'],
            }
        },
    ],
};

export default docIntroRouter;
