<script lang="ts" setup>
    import { computed, nextTick, onBeforeMount, onMounted, onUnmounted, ref, unref, watch } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useRouterStore } from '@/store/modules/routerStore';
    import { useRoute } from 'vue-router';
    import { ElMessage } from 'element-plus';
    import type { BreadcrumbType, RoutesDataItem } from '@/utils/routes';
    import {
        formatRoutePathTheParents,
        getBreadcrumbRoutes,
        getRouteBelongTopMenu,
        getRouteItem,
        getSelectLeftMenuPath
    } from '@/utils/routes';

    import Y9Default from '@/layouts/Y9-default/index.vue';
    import Y9Horizontal from '@/layouts/Y9-horizontal/index.vue';
    import Y9Mobile from '@/layouts/Y9-mobile/index.vue';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { getAllCountItems } from '@/api/flowableUI/workList';
    import { getItem, getItemList, getPositionList } from '@/api/flowableUI/index';
    import y9_storage from '@/utils/storage';
    import { useI18n } from 'vue-i18n';

    defineOptions({ name: 'indexLayout' });

    // 全局状态初始化
    const settingStore = useSettingStore();
    const routerStore = useRouterStore();
    const route = useRoute();
    const flowableStore = useFlowableStore();
    const { locale, t } = useI18n();

    // 布局配置
    const indexLayoutRef = ref<InstanceType<typeof Y9Default | typeof Y9Horizontal | typeof Y9Mobile> | null>(null);
    const layoutSubName = ref('');
    const layoutName = computed<string>(() => {
        // 水平布局强制禁用菜单折叠
        if (settingStore.getLayout === 'Y9Horizontal') {
            settingStore.$patch({ menuCollapsed: false });
        }
        const [name, subName = ''] = settingStore.getLayout.split(' ');
        layoutSubName.value = subName;
        return name;
    });

    // 动态组件安全映射，避免直接字符串渲染组件
    const layoutComponent = computed(() => {
        const componentMap: Record<string, typeof Y9Default> = {
            Y9Default,
            Y9Horizontal,
            Y9Mobile
        };
        return componentMap[layoutName.value] || Y9Default;
    });

    // 主题切换逻辑
    const theme = computed(() => settingStore.getThemeName);
    const updateThemeCss = (newTheme: string) => {
        // 安全更新根节点主题类
        document.documentElement.className = newTheme;

        // 安全更新主题样式链接，避免路径拼接错误
        const themeLink = document.getElementById('head') as HTMLLinkElement | null;
        if (!themeLink?.href) return;

        try {
            const url = new URL(themeLink.href, window.location.origin);
            const pathSegments = url.pathname.split('/');
            pathSegments[pathSegments.length - 1] = `${newTheme}.css`;
            url.pathname = pathSegments.join('/');

            // 仅在路径变化时更新，避免不必要的资源重载
            if (themeLink.href !== url.toString()) {
                themeLink.href = url.toString();
            }
        } catch (err) {
            console.error('主题样式更新失败:', err);
        }
    };

    watch(theme, (newTheme) => updateThemeCss(newTheme));

    // 移动端适配逻辑
    if (settingStore.getDevice === 'mobile') {
        settingStore.$patch({
            layout: 'Y9Mobile',
            settingWidth: '100%'
        });
    }

    const { toggleDevice } = settingStore;
    let resizeTimer: ReturnType<typeof setTimeout> | null = null;
    const handleScreenResize = () => {
        if (resizeTimer) clearTimeout(resizeTimer);
        resizeTimer = setTimeout(() => toggleDevice(), 250);
    };
    const unsubscribeSetting = settingStore.$subscribe(handleScreenResize);

    // 菜单与路由联动逻辑
    const menuCollapsed = computed(() => settingStore.getMenuCollapsed);

    // 根据路由条件动态构建响应式 menuData 和 routesData
    const menuData = ref<RoutesDataItem[]>([]);
    const routesData = ref<RoutesDataItem[]>([]);
    if (route.path.indexOf('/workIndex') > -1) {
        const newmenuData: any = routerStore.getPermissionRoutes;
        for (const obj of newmenuData) {
            if (obj.path.indexOf('/workIndex') > -1 && !obj.hidden) {
                menuData.value.push(obj);
            }
            if (obj.path === '/workIndex') {
                routesData.value.push(obj);
            }
        }
        document.title = t('工作台');
    } else if (route.path.indexOf('/index') > -1) {
        const newmenuData: any = routerStore.getPermissionRoutes;
        for (const obj of newmenuData) {
            if (obj.path.indexOf('/index') > -1) {
                menuData.value.push(obj);
            }
            if (obj.path === '/index') {
                routesData.value.push(obj);
            }
        }
    }

    const routeItem = computed<RoutesDataItem>(() => getRouteItem(route.path, routesData.value));
    const routeParentPaths = computed<string[]>(() => formatRoutePathTheParents(routeItem.value.path));
    const belongTopMenu = computed<string>(() => getRouteBelongTopMenu(routeItem.value));
    const defaultActive = ref<string>(getSelectLeftMenuPath(routeItem.value));
    const defaultOpened = ref('');
    const { addTab } = routerStore;

    watch(
        routeItem,
        async () => {
            addTab(unref(routeItem));
            await nextTick();
            const active = getSelectLeftMenuPath(routeItem.value);
            if (!active.includes('/edit')) {
                defaultActive.value = getSelectLeftMenuPath(routeItem.value);
            }
            if (active.includes('/searchList')) {
                defaultActive.value = '/workIndex/monitorBanjian';
            }
        },
        { immediate: true }
    );

    // 面包屑导航
    const breadCrumbs = computed<BreadcrumbType[]>(() =>
        getBreadcrumbRoutes(routeItem.value, routeParentPaths.value, routesData.value)
    );

    // 国际化切换
    const webLanguage = computed(() => settingStore.getWebLanguage);
    watch(
        webLanguage,
        (newLang) => {
            locale.value = newLang;
        },
        { immediate: true }
    );

    // 初始化事项信息
    const getItemInfo = () => {
        if (flowableStore.getItemId === '' && route.path.indexOf('/index') > -1) {
            const itemId = y9_storage.getObjectItem('query', 'itemId');
            flowableStore.$patch({ itemId });
            if (itemId && itemId !== undefined) {
                getItem(itemId)
                    .then((res) => {
                        flowableStore.$patch({
                            itemInfo: res.data.itemModel,
                            itemId: res.data.itemModel.id,
                            itemName: res.data.itemModel.name,
                            deptManage: res.data.deptManage,
                            monitorManage: res.data.monitorManage
                        });
                        document.title = res.data.itemModel.name;
                        const positionId = sessionStorage.getItem('positionId') ?? '';
                        const manager = res.data.itemModel.nature ?? '';
                        const monitorManage = manager.indexOf(positionId) > -1;

                        menuData.value.forEach((menu) => {
                            if (!monitorManage) {
                                menu.children = menu.children?.filter((item) => !item.name?.includes('monitor'));
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
        const itemId = route.path.indexOf('/index') > -1 ? y9_storage.getObjectItem('query', 'itemId') : '';
        getPositionList('count', itemId)
            .then((res) => {
                if (res.success) {
                    const positionId = sessionStorage.getItem('positionId')!;
                    let currenttodoCount = 0;
                    let currentName = '';
                    res.data.positionList.forEach((item) => {
                        if (item.id === positionId) {
                            currenttodoCount = item.todoCount;
                            currentName = item.name;
                        }
                    });
                    flowableStore.$patch({
                        positionList: res.data.positionList,
                        currentPositionId: positionId,
                        allCount: res.data.allCount,
                        currentCount: currenttodoCount,
                        currentPositionName: currentName
                    });
                    sessionStorage.setItem('positionId', positionId);
                    sessionStorage.setItem('positionName', currentName);
                }
            })
            .catch(() => {
                ElMessage({ type: 'info', message: t('数据加载失败') });
            });
    };

    const getAllMenuCount = () => {
        if (flowableStore.getItemId && route.path.indexOf('/index') > -1) {
            getAllCountItems(flowableStore.getItemId)
                .then((res) => {
                    const countItems = res.data;
                    flowableStore.$patch({
                        draftCount: countItems.draftCount,
                        wtodoCount: countItems.wtodoCount,
                        wdoneCount: countItems.wdoneCount,
                        todoCount: countItems.todoCount,
                        doingCount: countItems.doingCount,
                        doneCount: countItems.doneCount,
                        draftRecycleCount: countItems.draftRecycleCount,
                        monitorDoing: countItems.monitorDoing,
                        monitorDone: countItems.monitorDone
                    });
                })
                .catch(() => {
                    ElMessage({ type: 'info', message: t('数据加载失败') });
                });
        }
    };

    const refreshMenuCount = () => {
        if (flowableStore.getItemId && route.path.indexOf('/index') > -1) {
            getAllCountItems(flowableStore.getItemId)
                .then((res) => {
                    const countItems = res.data;
                    flowableStore.$patch({
                        draftCount: countItems.draftCount,
                        wtodoCount: countItems.wtodoCount,
                        wdoneCount: countItems.wdoneCount,
                        todoCount: countItems.todoCount,
                        doingCount: countItems.doingCount,
                        doneCount: countItems.doneCount,
                        draftRecycleCount: countItems.draftRecycleCount,
                        monitorDoing: countItems.monitorDoing,
                        monitorDone: countItems.monitorDone
                    });
                })
                .catch(() => {
                    ElMessage({ type: 'info', message: t('数据加载失败') });
                });
        }
        if (route.path.indexOf('/workIndex') > -1) {
            getItemList().then((res) => {
                if (res.success) {
                    menuData.value.forEach((menu) => {
                        if (menu.name === 'yuejian') {
                            menu.todoCount = res.data.notReadCount;
                        } else if (menu.name === 'email') {
                            menu.todoCount = res.data.youjianCount;
                        } else if (menu.name === 'follow') {
                            menu.todoCount = res.data.followCount;
                        } else {
                            res.data.itemMap.forEach((item) => {
                                if (item.url === menu.name) {
                                    menu.todoCount = item.todoCount;
                                    menu.children?.forEach((children) => {
                                        if (children.name === 'workIndex_todo') {
                                            children.todoCount = item.todoCount;
                                        }
                                    });
                                }
                            });
                        }
                    });
                    flowableStore.$patch({
                        itemList: res.data.itemMap,
                        notReadCount: res.data.notReadCount,
                        youjianCount: res.data.youjianCount
                    });
                }
            });
        }
        getPositionListAndTodoCount();
    };

    // 生命周期管理
    onBeforeMount(() => {
        getItemInfo();
        getAllMenuCount();
        getPositionListAndTodoCount();

        // 仅当现有逻辑没有匹配到有效激活路径时，才用这段兜底逻辑
        // if (!defaultActive.value) {
        //     if (route.path.indexOf('/workIndex') > -1) {
        //         defaultActive.value =
        //             '/workIndex/todo' + (flowableStore.itemList.length > 0 ? flowableStore.itemList[0].url : '');
        //         defaultOpened.value = flowableStore.itemList.length > 0 ? flowableStore.itemList[0].url : '';
        //     } else if (route.path.indexOf('/index') > -1) {
        //         defaultActive.value = '/index/todo';
        //     }
        // }
    });

    onMounted(() => updateThemeCss(theme.value));

    onUnmounted(() => {
        // 清理所有副作用，避免内存泄漏
        unsubscribeSetting();
        if (resizeTimer) clearTimeout(resizeTimer);
    });
</script>

<template>
    <component
        :is="layoutComponent"
        :key="layoutName"
        ref="indexLayoutRef"
        :belongTopMenu="belongTopMenu"
        :breadCrumbs="breadCrumbs"
        :defaultActive="defaultActive"
        :defaultOpened="defaultOpened"
        :layoutName="layoutName"
        :layoutSubName="layoutSubName"
        :menuCollapsed="menuCollapsed"
        :menuData="menuData"
        :routeItem="routeItem"
        @indexRefreshCount="refreshMenuCount($event)"
    ></component>
</template>
