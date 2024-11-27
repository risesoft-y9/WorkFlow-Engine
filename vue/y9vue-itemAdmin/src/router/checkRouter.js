/*
 * @Author: your name
 * @Date: 2021-12-22 14:38:02
 * @LastEditTime: 2023-11-28 09:32:09
 * @LastEditors: mengjuhua
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\router\checkRouter.js
 */
import '@/assets/css/nprogress.css'; // progress bar style
import router from '@/router';
import {checkRole} from '@/router/checkRole';
import {constantRoutes} from '@/router/index';
import {useSettingStore} from '@/store/modules/settingStore';
import NProgress from 'nprogress'; // progress bar
import {$y9_SSO} from '../main';
import {getLoginInfo} from './getInitData';

NProgress.configure({ showSpinner: false, easing: 'ease', speed: 1000 });

// 路由白名单过滤
function routerWriteList(array, path) {
    let find = false;
    for (let index = 0; index < array.length; index++) {
        const item = array[index];
        if (item.path === path) {
            return true;
        }
        if (item.children) {
            find = routerWriteList(item.children, path);
        }
    }
    if (find) {
        return true;
    } else {
        return false;
    }
}

// 路由白名单
export async function checkWriteList(to, from) {
    // 白名单
    let isWriteList = routerWriteList(constantRoutes, to.path);
    if (isWriteList) {
        if (to.path === '/' || to.path === '/login') {
            // 登陆过 导航直接跳过login页面进入系统
            const isRight = await $y9_SSO.checkToken();
            if (isRight) {
                window.location = import.meta.env.VUE_APP_HOST_INDEX;
            }
        }
        return true;
    } else {
        return false;
    }
}

let userRole = ['user'];

async function check() {
    let isTokenValid, isRoleValid;

    // access_token 是否过期
    isTokenValid = await $y9_SSO.checkToken();
    // console.log(`isTokenValid=${isTokenValid}`);
    if (!isTokenValid) {
        return false;
    }

    const y9UserInfo = JSON.parse(sessionStorage.getItem('ssoUserInfo'));

    if (y9UserInfo.managerLevel === 1) {
        userRole = ['systemAdmin'];
    } else {
        // 抱歉，该登录账号非管理员用户账号，没有权限！！！
        window.location = window.location.origin + import.meta.env.VUE_APP_PUBLIC_PATH + `401`;
    }

    isRoleValid = (await checkRole(userRole)) ? true : false;
    // 根据角色权限获取路由
    // let isLoadRouter = sessionStorage.getItem('isLoadRouter');
    // 是否加载过数据
    // if (import.meta.env.VUE_APP_APPFEATURES === '1' && isLoadRouter !== '1') {
    //     // 获取应用初始化数据，可选
    //     let initInfo = await getLoginInfo();
    //     y9_storage.setObjectItem('initInfo', initInfo.data);
    // } else {
    //     isRoleValid = true;
    // }
    // 每个工程都请求这个接口，当错误时，不再请求
    if (sessionStorage.getItem('getLoginInfo') != 'true') {
        // let initInfo = await getLoginInfo();
        // y9_storage.setObjectItem('initInfo', initInfo.data);
    }

    if (!isRoleValid) {
        return false;
    }
    // token在有效期且角色已获取路由
    if (isTokenValid && isRoleValid) {
        return true;
    } else {
        if (!isTokenValid || !isRoleValid) {
            await $y9_SSO.ssoLogout({
                logoutUrl: import.meta.env.VUE_APP_SSO_LOGOUT_URL + import.meta.env.VUE_APP_NAME + '/'
            });
        }

        return false;
    }
}

// 修复刷新bug
function isFresh(role, path) {
    let fresh = true;
    // authRouter.map((item) => {
    //     if (item.meta.roles[0] === role && item.redirect === path) {
    //         fresh = false;
    //     }
    // });
    return fresh;
}

let flag = 0;
export const routerBeforeEach = async (to, from) => {
    const settingStore = useSettingStore();
    // 进度条
    if (settingStore.getProgress) {
        NProgress.start();
    }
    console.log(to.path, from.path);
    // 检查路由白名单
    let isWriteRoute = await checkWriteList(to, from);
    // console.log(`isWriteList = ${isWriteRoute}`);
    if (isWriteRoute) {
        return true;
    }

    let CHECK = await check();
    // console.log(`CHECK = ${CHECK}`);
    if (CHECK) {
        // console.log(await router.getRoutes(),router,from,to, router.hasRoute(to.name));
        if (!to.name) {
            let array = await router.getRoutes();
            // console.log(to, array)
            array.forEach((item) => {
                if (item.path === to.path && item.name) {
                    // console.log("=====", router.hasRoute(item.name));
                    router.push({ name: item.name });
                }
            });
        } else {
            return true;
        }
    } else {
        await $y9_SSO.checkLogin();
    }

    return false;
};
