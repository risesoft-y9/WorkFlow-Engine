/*
 * @Author: your name
 * @Date: 2021-05-14 09:26:23
 * @LastEditTime: 2024-06-03 17:12:14
 * @LastEditors: zhangchongjie
 * @Description: In User Settings Edit
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\router\index.ts
 */

import {routerBeforeEach} from '@/router/checkRouter';
import NProgress from 'nprogress';
import {createRouter, createWebHistory} from 'vue-router';
import itemListRouter from './modules/itemListRouter';
import y9formRouter from './modules/y9formRouter';
//import entrustRouter from './modules/entrustRouter';
import docIntroRouter from './modules/docIntroRouter';

import configItemManageRouter from './modules/configItemManageRouter';

//流程相关
import processManageRouter from './modules/processManageRouter';

//constantRoutes为不需要动态判断权限的路由，如登录、404、500等
export const constantRoutes: Array<any> = [
    {
        path: '/',
        name: 'index',
        hidden: true,
        redirect: '/modelNew'
    },
    {
        path: '/401',
        hidden: true,
        meta: {
            title: 'Not Permission'
        },
        component: () => import('@/views/401/index.vue')
    },
    {
        path: '/404',
        hidden: true,
        meta: {
            title: 'Not Found'
        },
        component: () => import('@/views/404/index.vue')
    }
];

//asyncRoutes需求动态判断权限并动态添加的页面  这里的路由模块顺序也是菜单显示的顺序（位置：src->router->modules）
export const asyncRoutes = [
    //homeRouter,
    docIntroRouter,
    processManageRouter,
    itemListRouter,
    y9formRouter,
    configItemManageRouter
    // printTemplateRouter,
    //entrustRouter,
    // sendReceiveRouter,

    // 引入其他模块路由
];

//创建路由模式，采用history模式没有“#”
const router = createRouter({
    history: createWebHistory(import.meta.env.VUE_APP_PUBLIC_PATH),
    routes: constantRoutes
});

//在用户点击前，进入routerBeforeEach去判断用户是否有权限
//全部判断逻辑请查看checkRouter.js
router.beforeEach(routerBeforeEach);
router.afterEach(() => {
    NProgress.done();
});
export default router;
