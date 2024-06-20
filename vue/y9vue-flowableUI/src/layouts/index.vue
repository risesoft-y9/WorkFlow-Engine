<script lang="ts">
    import {
        defineEmits,
        defineComponent,
        watch,
        ref,
        Ref,
        unref,
        reactive,
        nextTick,
        computed,
        ComputedRef,
        onBeforeMount,
        onMounted,
    } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useRouterStore } from '@/store/modules/routerStore';
    import { useRoute } from 'vue-router';
    import {
        getSelectLeftMenuPath,
        getRouteBelongTopMenu,
        getRouteItem,
        RoutesDataItem,
        formatRoutePathTheParents,
        getBreadcrumbRoutes,
        BreadcrumbType,
    } from '@/utils/routes';
    import Y9Default from '@/layouts/Y9-default/index.vue';
    import Y9Horizontal from '@/layouts/Y9-horizontal/index.vue';
    import Y9Mobile from '@/layouts/Y9-mobile/index.vue';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { getAllCountItems } from '@/api/flowableUI/workList';
    import { getItem, getItemList, getPositionList } from '@/api/flowableUI/index';
    import y9_storage from '@/utils/storage';
    import settings from '@/settings';
    import { useI18n } from "vue-i18n";
    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    interface IndexLayoutSetupData {
        menuCollapsed: computed<Boolean>;
        tabNavEnable: boolean;
        belongTopMenu: ComputedRef<string>;
        menuData: RoutesDataItem[];
        defaultActive: Ref<string>;
        defaultOpened: Ref<string>;
        breadCrumbs: ComputedRef<BreadcrumbType[]>;
        routeItem: ComputedRef<RoutesDataItem>;
        layoutSubName?: Ref<string>;
    }

    export default {
        name: 'indexLayout',
        components: {
            Y9Default,
            Y9Horizontal,
            Y9Mobile,
        },
        setup() {
            // PC网站配置
            const { locale, t } = useI18n();
            const settingStore = useSettingStore();
            const layoutSubName = ref('');
            const layoutName = computed<string>(() => {
                // horizontal布局时，menuCollapsed 必须为false，禁止折叠
                if (settingStore.getLayout === 'Y9Horizontal') {
                    settingStore.$patch({
                        menuCollapsed: false,
                    });
                }
                const nameArray = settingStore.getLayout.split(' ');
                nameArray[1] ? (layoutSubName.value = nameArray[1]) : (layoutSubName.value = '');
                return nameArray[0];
            });

            // 主题切换
            const theme = computed(() => settingStore.getThemeName);

            watch(theme, () => {
                document.getElementsByTagName('html')[0].className = theme.value;
                // 修复打包后的主题切换问题
                if (document.getElementById("head")) {
                    let themeDom = document.getElementById("head")
                    let pathArray = themeDom.href.split('/')
                    pathArray[pathArray.length-1] = theme.value + '.css'
                    let newPath = pathArray.join('/')
                    themeDom.href = newPath
                }
            });

            // 移动端
            if (settingStore.getDevice === 'mobile') {
                settingStore.$patch({
                    layout: 'Y9Mobile',
                    settingWidth: '100%',
                });
            }
            const { toggleDevice } = settingStore;
            let changeDeviceTimeOut;
            const onScreenFunc = () => {
                changeDeviceTimeOut ? clearTimeout(changeDeviceTimeOut) : '';
                changeDeviceTimeOut = setTimeout(() => {
                    toggleDevice();
                }, 250);
            };
            settingStore.$subscribe(onScreenFunc);

            // 收缩左侧
            const menuCollapsed = computed<Boolean>(() => settingStore.getMenuCollapsed);

            // 所有菜单路由
            const routerStore = useRouterStore();
            let route = useRoute();
            let menuData: RoutesDataItem[] = [];
            let routesData: RoutesDataItem[] = [];
            if (route.path.indexOf('/workIndex') > -1) {
                let newmenuData = routerStore.getPermissionRoutes;
                for (let obj of newmenuData) {
                    if (obj.path.indexOf('/workIndex') > -1 && !obj.hidden) {
                        menuData.push(obj);
                    }
                    if (obj.path == '/workIndex') {
                        routesData.push(obj);
                    }
                }
                document.title = t('工作台');
            } else if (route.path.indexOf('/index') > -1) {
                let newmenuData = routerStore.getPermissionRoutes;
                for (let obj of newmenuData) {
                    if (obj.path.indexOf('/index') > -1) {
                        menuData.push(obj);
                    }
                    if (obj.path == '/index') {
                        routesData.push(obj);
                    }
                }
            }else if (route.path.indexOf('/cplane') > -1) {
                let newmenuData = routerStore.getPermissionRoutes;
                for (let obj of newmenuData) {
                    if (obj.path.indexOf('/cplane') > -1) {
                        menuData.push(obj);
                    }
                    flowableStore.itemName = t("我的协作");
                    if (obj.path == '/cplane') {
                        routesData.push(obj);
                    }
                }
            }
            
            // 当前路由 item
            const routeItem = computed<RoutesDataItem>(() => getRouteItem(route.path, routesData));

            // 当前路由的父路由path[]
            const routeParentPaths = computed<string[]>(() => formatRoutePathTheParents(routeItem.value.path));

            // 当前路由的顶部菜单path
            const belongTopMenu = computed<string>(() => getRouteBelongTopMenu(routeItem.value));

            // 左侧选择的菜单
            const defaultActive = ref<string>(getSelectLeftMenuPath(routeItem.value));
            const { addTab } = routerStore;
            watch([routeItem], async () => {
                addTab(unref(routeItem));
                await nextTick();
                let active = getSelectLeftMenuPath(routeItem.value);
                if(!active.includes('/edit')){
                    defaultActive.value = getSelectLeftMenuPath(routeItem.value);
                }
                if(active.includes('/searchList')){
                    defaultActive.value = '/workIndex/monitorBanjian';
                }
            });
            const defaultOpened = ref('');
            // if(route.path.indexOf('/workIndex') > -1){
            //     defaultActive.value = "/workIndex/todo" + (flowableStore.itemList.length > 0 ? flowableStore.itemList[0].url : "");
            //     defaultOpened.value = flowableStore.itemList.length > 0 ? flowableStore.itemList[0].url : "";
            // }else if(route.path.indexOf('/index') > -1){
            //     defaultActive.value = '/index/todo';
            // }

            // 面包屑导航
            const breadCrumbs = computed<BreadcrumbType[]>(() =>
                getBreadcrumbRoutes(routeItem.value, routeParentPaths.value, routesData)
            );

            onBeforeMount(() => {
                getItemInfo(); //初始化事项信息
                getAllMenuCount();
                getPositionListAndTodoCount();
            });
            // 挂载组件后初始化网站设置
            onMounted(() => {
                // 初始化主题
                document.getElementsByTagName('html')[0].className = theme.value;
            });

            const getItemInfo = () => {
                if(route.path.indexOf('/cplane') > -1){
                    return;
                }
                // 获取当前路由
                if (flowableStore.getItemId == '' && route.path.indexOf('/index') > -1) {
                    //let itemId = route.query?.itemId?route.query?.itemId: '';
                    let itemId = y9_storage.getObjectItem('query', 'itemId');
                    flowableStore.$patch({
                        itemId: itemId,
                    });
                    if (itemId != '' && itemId != undefined) {
                        getItem(itemId)
                            .then((res) => {
                                flowableStore.$patch({
                                    itemInfo: res.data.itemModel,
                                    itemId: res.data.itemModel.id,
                                    itemName: res.data.itemModel.name,
                                    deptManage: res.data.deptManage,
                                    monitorManage: res.data.monitorManage,
                                });
                                document.title = res.data.itemModel.name;
                                menuData.forEach(menu => {
                                    // let y9UserInfo =JSON.parse(sessionStorage.getItem('ssoUserInfo'));
                                    let positionId = sessionStorage.getItem('positionId');
                                    //人事办件，曲金凤账号可查看监控在办，特殊办结
                                    let manager = res.data.itemModel.nature == null ? '' : res.data.itemModel.nature;//事项管理员
                                    let monitorManage = manager.indexOf(positionId) > -1 ? true : false;
                                    if(!monitorManage){//不是事项管理员，删除监控列表
                                        menu.children = menu.children?.filter(item => item.name?.indexOf('monitor') == -1);
                                    }
                                });
                            })
                            .catch(() => {
                                ElMessage({ type: 'info', message: t('数据加载失败') });
                            });
                    }
                }
            };

            const getPositionListAndTodoCount = () => {
                if(route.path.indexOf('/cplane') > -1){
                    return;
                }
                let itemId = route.path.indexOf('/index') > -1 ? y9_storage.getObjectItem('query', 'itemId') : '';
                getPositionList('count', itemId)
                    .then((res) => {
                        if (res.success) {
                            let positionId = sessionStorage.getItem('positionId')!;
                            let currenttodoCount = 0;
                            let currentName = '';
                            res.data.positionList.forEach((item, index) => {
                                if (item.id == positionId) {
                                    currenttodoCount = item.todoCount;
                                    currentName = item.name;
                                }
                            });
                            flowableStore.$patch({
                                positionList: res.data.positionList,
                                currentPositionId: positionId,
                                allCount: res.data.allCount,
                                currentCount: currenttodoCount,
                                currentPositionName: currentName,
                            });
                            sessionStorage.setItem('positionId', positionId);
                            sessionStorage.setItem('positionName', currentName);
                            //sessionStorage.setItem('currentCount', currenttodoCount + '');
                        }
                    })
                    .catch(() => {
                        ElMessage({ type: 'info', message: t('数据加载失败') });
                    });
            };

            const getAllMenuCount = () => {
                if(route.path.indexOf('/cplane') > -1){
                    return;
                }
                if (flowableStore.getItemId != '' && route.path.indexOf('/index') > -1) {
                    getAllCountItems(flowableStore.getItemId)
                        .then((res) => {
                            let countItems = res.data;
                            // if(this.workOrderType != ""){
                            //     this.$refs.elmenu.activeIndex = this.workOrderType;
                            // }
                            flowableStore.$patch({
                                draftCount: countItems.draftCount, //草稿箱数量
                                wtodoCount: countItems.wtodoCount, //系统工单未处理数量
                                wdoneCount: countItems.wdoneCount, //系统工单已处理数量
                                todoCount: countItems.todoCount, //待办件数量
                                doingCount: countItems.doingCount, //在办件数量
                                doneCount: countItems.doneCount, //办结件数量
                                draftRecycleCount: countItems.draftRecycleCount, //回收站数量
                                monitorDoing: countItems.monitorDoing, //监控在办数量
                                monitorDone: countItems.monitorDone, //监控办结数量
                            });
                        })
                        .catch(() => {
                            ElMessage({ type: 'info', message: t('数据加载失败') });
                        });
                }
            };

            const refreshMenuCount = () => {
                if (flowableStore.getItemId != '' && route.path.indexOf('/index') > -1) {
                    getAllCountItems(flowableStore.getItemId)
                        .then((res) => {
                            let countItems = res.data;
                            flowableStore.$patch({
                                draftCount: countItems.draftCount, //草稿箱数量
                                wtodoCount: countItems.wtodoCount, //系统工单未处理数量
                                wdoneCount: countItems.wdoneCount, //系统工单已处理数量
                                todoCount: countItems.todoCount, //待办件数量
                                doingCount: countItems.doingCount, //在办件数量
                                doneCount: countItems.doneCount, //办结件数量
                                draftRecycleCount: countItems.draftRecycleCount, //回收站数量
                                monitorDoing: countItems.monitorDoing, //监控在办数量
                                monitorDone: countItems.monitorDone, //监控办结数量
                            });
                        })
                        .catch(() => {
                            ElMessage({ type: 'info', message: t('数据加载失败') });
                        });
                }
                if (route.path.indexOf('/workIndex') > -1) {
                    getItemList().then((res) => {
                        if (res.success) {
                            menuData.forEach((menu) => {
                                if (menu.name == 'yuejian') {
                                    menu.todoCount = res.data.notReadCount;
                                } else if (menu.name == 'email') {
                                    menu.todoCount = res.data.youjianCount;
                                } else if (menu.name == 'follow') {
                                    menu.todoCount = res.data.followCount;
                                } else {
                                    res.data.itemMap.forEach((item) => {
                                        if (item.url == menu.name) {
                                            menu.todoCount = item.todoCount;
                                            if(menu.children.length > 0){//子菜单待办件数量
                                                menu.children.forEach((children) => {
                                                    if(children.name == 'workIndex_todo'){
                                                        children.todoCount = item.todoCount;
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                            flowableStore.$patch({
                                itemList: res.data.itemMap,
                                notReadCount: res.data.notReadCount,
                                youjianCount: res.data.youjianCount,
                                // followCount:res.data.followCount
                            });
                        }
                    });
                }
                getPositionListAndTodoCount();
            };

            // 国际语言切换 仍有bug
            const webLanguage = computed(() => settingStore.getWebLanguage)
            watch(webLanguage, () => {
                // 修复打包后的国际语言切换和主题切换 的问题
                locale.value = webLanguage.value
            })

            return {
                layoutName,
                layoutSubName,
                menuData,
                menuCollapsed,
                belongTopMenu,
                defaultActive,
                defaultOpened,
                breadCrumbs,
                routeItem,
                flowableStore,
                getItemInfo,
                getAllMenuCount,
                refreshMenuCount,
            };
        },
    };
</script>
<template>
    <component
        :is="layoutName"
        :key="layoutName"
        :layoutName="layoutName"
        :layoutSubName="layoutSubName"
        :menuCollapsed="menuCollapsed"
        :belongTopMenu="belongTopMenu"
        :defaultActive="defaultActive"
        :defaultOpened="defaultOpened"
        :menuData="menuData"
        :breadCrumbs="breadCrumbs"
        :routeItem="routeItem"
        ref="indexLayoutRef"
        v-on:indexRefreshCount="refreshMenuCount($event)"
    ></component>
</template>
