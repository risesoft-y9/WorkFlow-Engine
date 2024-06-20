/*
 * @Author: your name
 * @Date: 2022-01-13 17:37:14
 * @LastEditTime: 2024-01-30 09:41:24
 * @LastEditors: zhangchongjie
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\settings.ts
 */
import { RoutesDataItem } from "@/utils/routes";
/**
 * 站点配置
 * @author LiQingSong
 */
export interface SettingsType {
    /**
     * 站点名称
     */
    siteTitle: string;
  
    /**
     * 顶部菜单开启
     */
    topNavEnable: boolean;
  
    /**
     * 头部固定开启
     */
    headFixed: boolean;

    /**
     * tab菜单开启
     */
     tabNavEnable: boolean;

     /**
     * 站点首页路由
     */
    homeRouteItem: RoutesDataItem;
  
    /**
     * 站点本地存储Token 的 Key值
     */
    siteTokenKey: string;
  
    /**
     * Ajax请求头发送Token 的 Key值
     */
    ajaxHeadersTokenKey: string;
  
    /**
     * Ajax返回值不参加统一验证的api地址
     */
    ajaxResponseNoVerifyUrl: string[];

    /**
     * iconfont.cn 项目在线生成的 js 地址
     */
    iconfontUrl: string[];

    //是否显示意见留痕
    opinion_History: boolean;

    //公务邮件地址
    emailURL:string; 

    //曲金凤人员id
    qjfUserId:string;

    //人事办件事项id
    qingxiujiaItemId: string;

    //有生租户id
    risesoftTenantId: string;

    //法人岗位id,shoutong使用
    legalPersonId: string;

    //自动抄送法人事项id,shoutong使用
    chaoSongItemId: string;

    huifudaiban: boolean;//恢复待办按钮是否显示
}
  
const settings: SettingsType = {
    siteTitle: 'Y9-ADMIN-VUE',
    topNavEnable: true,
    headFixed: true,
    tabNavEnable: true,
    homeRouteItem: {
        icon: 'control',
        title: 'index-layout.menu.home.workplace',
        path: '/home/workplace',
        component: ()=> import('@/App.vue')
    },
    siteTokenKey: 'y9AT',
    ajaxHeadersTokenKey: 'x-token',
    ajaxResponseNoVerifyUrl: [
        '/user/login', // 用户登录
        '/user/info', // 获取用户信息
    ],
    iconfontUrl: [],
    opinion_History: false,
    emailURL: 'https://vue.youshengyun.com/email',
    qjfUserId: '1451993930890481664',//曲金凤人力资源岗位id
    qingxiujiaItemId: '8d5532c11bb84ff1ab6634598a915067',//人事办件事项id
    risesoftTenantId: 'c425281829dc4d4496ddddf7fc0198d0',//有生租户id
    legalPersonId: '',//法人岗位id,shoutong使用
    chaoSongItemId: '',//自动抄送法人事项id,shoutong使用
    huifudaiban: false,//恢复待办按钮是否显示
};

export default settings;
  