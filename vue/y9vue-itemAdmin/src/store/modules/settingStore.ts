import { isMobile } from '@/utils/index';
import { useWindowSize } from '@vueuse/core';
import { defineStore } from 'pinia';

export const useSettingStore = defineStore('settingStore', {
    state: () => {
        return {
            webName: '有生软件', // 网站名称
            logoSvgName: '', // 网站logo
            webLanguage: 'zh', // 语言
            themeName: 'theme-default', // 主题
            isDark: false, // 是否暗黑主题
            menuAnimation: 'rtl', // 菜单动画方向——仅mobile
            menuStyle: 'Light', // 菜单样式
            menuWidth: '100%', // 菜单宽度（高度）
            menuBg: '', // 菜单背景 new URL('../../assets/images/menu-bg1.png', import.meta.url).href
            showLabel: false, // 显示标签
            showLabelIcon: false, // 显示标签icon
            labelStyle: 'top', // 标签风格
            separateStyle: '分栏风格1', // 分栏风格
            fixedHeader: true, // 固定
            progress: true, // 进度条
            refresh: true, // 刷新
            search: false, // 搜索
            notify: false, // 提醒
            fullScreeen: true, // 全屏
            lock: true, // 锁屏功能
            lockScreen: false, // 锁屏状态
            lockScreenImage: new URL('../../assets/images/menu-bg1.png', import.meta.url).href, // 锁屏背景
            unlockScreenPwd: '123456', // 默认锁屏密码
            pageAnimation: true, // 页面动画
            pcLayout: 'Y9Default', // pc布局   Y9Horizontal, Y9Default, Y9Default sidebar-separate
            layout: 'Y9Default', // pc or mobile
            allPcLayout: 'globalModule', //globalModule 全局 singleModule 单一
            allLayoutList: [],
            menuCollapsed: false, // 菜单伸缩
            device: !isMobile() ? 'pc' : 'mobile', // 设备类型
            windowWidth: useWindowSize().width, // 窗口实时宽度
            windowHeight: useWindowSize().height, // 窗口实时高度
            direction: 'ltr', // 菜单——仅mobile
            settingPageStyle: 'Admin-plus', // 设置页面的风格 Dcat Admin-plus
            settingAnimation: 'rtl', // 设置页面的移入方向
            settingWidth: '20%' // 设置页面的宽度（高度）
        };
    },
    getters: {
        getWebName: (state) => {
            return state.webName;
        },
        getLogoSvgName: (state) => {
            return state.logoSvgName;
        },
        getWebLanguage: (state) => {
            return state.webLanguage;
        },
        getThemeName: (state) => {
            return state.themeName;
        },
        getIsDark: (state) => {
            return state.isDark;
        },
        getMenuAnimation: (state) => {
            return state.menuAnimation;
        },
        getMenuStyle: (state) => {
            return state.menuStyle;
        },
        getLayout: (state) => {
            let layout: any = '';
            if (state.allPcLayout == 'globalModule') {
                layout = state.layout;
            } else {
                let name = import.meta.env.VUE_APP_NAME;
                let allLayoutList: any = state.allLayoutList;
                let found = false;
                for (let i = 0; i < allLayoutList.length; i++) {
                    let item = allLayoutList[i];
                    if (item.name == name) {
                        found = true;
                        layout = item.value;
                        break;
                    }
                }
                if (!found) {
                    layout = state.layout;
                }
            }
            return layout;
        },
        getLayoutList: () => {},
        //查询全局还是单一
        getAllPcLayout: (state) => {
            return state.allPcLayout;
        },
        getAllPcLayoutList: (state) => {
            return state.allLayoutList;
        },
        getPcLayout: (state) => {
            return state.pcLayout;
        },
        getMenuCollapsed: (state) => {
            return state.menuCollapsed;
        },
        getMenuWidth: (state) => {
            return state.menuWidth;
        },
        getMenuBg: (state) => {
            return state.menuBg;
        },
        getShowLabel: (state) => {
            return state.showLabel;
        },
        getShowLabelIcon: (state) => {
            return state.showLabelIcon;
        },
        getLabelStyle: (state) => {
            return state.labelStyle;
        },
        getSeparateStyle: (state) => {
            return state.separateStyle;
        },
        getFixedHeader: (state) => {
            return state.fixedHeader;
        },
        getProgress: (state) => {
            return state.progress;
        },
        getRefresh: (state) => {
            return state.refresh;
        },
        getSearch: (state) => {
            return state.search;
        },
        getNotify: (state) => {
            return state.notify;
        },
        getFullScreeen: (state) => {
            return state.fullScreeen;
        },
        getLock: (state) => {
            return state.lock;
        },
        getLockScreen: (state) => {
            return state.lockScreen;
        },
        getLockScreenImage: (state) => {
            return state.lockScreenImage;
        },
        getUnlockScreenPwd: (state) => {
            return state.unlockScreenPwd;
        },
        getPageAnimation: (state) => {
            return state.pageAnimation;
        },
        getDirection: (state) => {
            return state.direction;
        },
        getDevice: (state) => {
            return state.device;
        },
        getWindowWidth: (state) => {
            return state.windowWidth;
        },
        getWindowHeight: (state) => {
            return state.windowHeight;
        },
        getSettingPageStyle: (state) => {
            return state.settingPageStyle;
        },
        getSettingAnimation: (state) => {
            return state.settingAnimation;
        },
        getSettingWidth: (state) => {
            return state.settingWidth;
        }
    },
    actions: {
        toggleDevice() {
            if (isMobile()) {
                this.device = 'mobile';
                this.layout = 'Y9Mobile';
                this.settingWidth = '100%';
            } else {
                this.device = 'pc';
                this.layout = this.pcLayout;
            }
        },
        toggleCollapsed() {
            this.menuCollapsed = !this.menuCollapsed;
        }
    }
});
