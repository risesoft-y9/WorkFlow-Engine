<template>
    <div
        id="indexlayout"
        :class="{
            fixedHeader: !settingStore.getFixedHeader,
            'fixedHeader-menuCollapsed': settingStore.getMenuCollapsed && !settingStore.getFixedHeader,
            [layout]: true
        }"
    >
        <div id="indexlayout-left" ref="layoutLeftRef">
            <Left
                :belongTopMenu="belongTopMenu"
                :defaultActive="defaultActive"
                :defaultOpened="defaultOpened"
                :layoutSubName="layoutSubName"
                :menuCollapsed="menuCollapsed"
                :menuData="menuData"
            />
        </div>
        <div id="indexlayout-right" ref="layoutRightRef" class="right">
            <RightTop :menuCollapsed="menuCollapsed" @refresh="refreshFunc" />
            <BreadCrumbs :layoutSubName="layoutSubName" :list="breadCrumbs" :menuCollapsed="menuCollapsed" />

            <div
                :key="refreshContent"
                :class="{
                    'indexlayout-right-main': true,
                    'sidebar-separate': layoutSubName === 'sidebar-separate',
                    'sidebar-separate-menuCollapsed': menuCollapsed && layoutSubName === 'sidebar-separate',
                    'tabs-position-left': routerStore.getTabs.length && settingStore.getLabelStyle === 'left',
                    'tabs-position-right': routerStore.getTabs.length && settingStore.getLabelStyle === 'right'
                }"
            >
                <router-view v-if="flowableStore.isReload" @refreshCount="indexRefreshCount"></router-view>
            </div>
        </div>
    </div>
    <Settings v-if="settingPageStyle === 'Admin-plus'"></Settings>
    <Lock v-show="settingStore.getLockScreen" />
</template>

<script lang="ts" setup>
    import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useRouterStore } from '@/store/modules/routerStore';
    import type { BreadcrumbType, RoutesDataItem } from '@/utils/routes';
    import { debounce } from 'lodash-es';
    import Lock from '@/layouts/components/Lock/index.vue';
    import Left from './Left.vue';
    import RightTop from './RightTop.vue';
    import Settings from '@/layouts/components/SettingsMobile.vue';
    import BreadCrumbs from '@/layouts/components/BreadCrumbs/index.vue';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useRoute } from 'vue-router';

    // 全局Store初始化
    const flowableStore = useFlowableStore();
    const settingStore = useSettingStore();
    const routerStore = useRouterStore();
    const currentRoute = useRoute();

    // DOM引用与状态标记
    const layoutLeftRef = ref<HTMLElement | null>(null);
    const layoutRightRef = ref<HTMLElement | null>(null);
    const scrollListenerActive = ref(false);
    const navTop = ref('');
    const navLeft = ref('');

    // 计算属性
    const settingPageStyle = computed(() => settingStore.getSettingPageStyle);
    const showTab = computed(() => settingStore.getShowLabel);
    const layout = computed(() => settingStore.getLayout);

    // 组件属性定义
    const props = defineProps<{
        layoutName: string;
        layoutSubName: string;
        menuData: RoutesDataItem[];
        menuCollapsed: boolean;
        belongTopMenu: string;
        defaultActive: string;
        defaultOpened: string;
        breadCrumbs: BreadcrumbType[];
        routeItem: RoutesDataItem;
    }>();

    // 侧边栏宽度更新工具方法
    function updateSidebarWidth(width: string) {
        const sidebar = layoutLeftRef.value?.firstElementChild as HTMLElement | null;
        if (sidebar) sidebar.style.width = width;
    }

    // 滚动监听核心逻辑
    function listener() {
        const classList = layoutLeftRef.value?.classList;
        if (!classList) return;

        const scrollY = layoutRightRef.value?.scrollTop ?? 0;
        const hasFixedClass = classList.contains('fixed-header-after-scroll');

        if (scrollY > 50 && !hasFixedClass) {
            classList.add('fixed-header-after-scroll');
            if (settingStore.menuCollapsed) {
                updateSidebarWidth('68px');
            }
        }
        if (scrollY < 50 && hasFixedClass) {
            classList.remove('fixed-header-after-scroll');
            if (settingStore.menuCollapsed) {
                updateSidebarWidth('');
            }
        }
    }

    // 16ms防抖优化，保证60fps滚动流畅度
    const debouncedScrollListener = debounce(listener, 16);

    // 注册滚动监听
    function addScrollListener() {
        if (scrollListenerActive.value || !layoutRightRef.value) return;
        layoutRightRef.value.addEventListener('scroll', debouncedScrollListener, false);
        scrollListenerActive.value = true;
    }

    // 移除滚动监听，清理资源
    function removeScrollListener() {
        if (!scrollListenerActive.value) return;
        layoutRightRef.value?.removeEventListener('scroll', debouncedScrollListener, false);
        debouncedScrollListener.cancel();
        scrollListenerActive.value = false;
        layoutLeftRef.value?.classList.remove('fixed-header-after-scroll');
    }

    // 监听布局切换，动态启停监听
    watch(layout, (newV, oldV) => {
        console.log('11111', newV, oldV);
        if (newV.indexOf('sidebar-separate') > 0) {
            addScrollListener();
            navTop.value = '';
            navLeft.value = '';
        } else {
            removeScrollListener();
            navTop.value = '9%';
            navLeft.value = '6%';
        }
        console.log('22222', navTop.value, navLeft.value);
    });

    // 页面挂载初始化
    onMounted(() => {
        if (layout.value.indexOf('sidebar-separate') > 0) {
            addScrollListener();
        } else {
            navTop.value = '9%';
            navLeft.value = '6%';
        }
    });

    // 组件卸载前清理监听，避免内存泄漏
    onBeforeUnmount(removeScrollListener);

    // 页面刷新逻辑
    const refreshContent = ref(0);
    function refreshFunc() {
        refreshContent.value++;
    }

    const emit = defineEmits(['indexRefreshCount']);
    async function indexRefreshCount() {
        emit('indexRefreshCount');
    }
</script>
<style lang="scss">
    .y9-dialog-overlay .y9-dialog .y9-dialog-body .y9-dialog-content {
        padding: 16px !important;
    }
</style>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    #indexlayout {
        display: flex;
        height: 100vh;
        overflow: hidden;
        min-width: 1350px;
    }

    #indexlayout-left {
        z-index: 1;
        box-shadow: 2px 2px 2px 1px rgb(0 0 0 / 6%);
    }

    #indexlayout-right {
        position: relative;
        flex: 1;
        overflow: auto;
        scrollbar-width: none;
        background-color: var(--bg-color);
        min-height: 780px;

        &.right {
            display: flex;
            flex-direction: column;
            background-color: var(--el-color-primary-light-9);
            min-height: 100vh;

            .indexlayout-right-main {
                flex: 1;
                background-color: #eef0f7;
                padding: $main-padding;
                padding-bottom: 0px;
                overflow: auto;
                scrollbar-width: none;
                box-shadow: 3px 3px 3px var(--el-color-info-light);

                :deep(.nav) {
                    position: absolute;
                    top: v-bind(navTop);
                    left: v-bind(navLeft);
                }

                :deep(.tab-nav) {
                    top: -#{$headerTabNavHeight};
                }

                &.sidebar-separate {
                    padding-left: calc(#{$leftSideBarWidth} + #{$sidebar-separate-margin-left} + #{$main-padding});

                    :deep(.nav) {
                        position: absolute;
                        top: 9%;
                        margin-left: 5%;
                    }
                }

                // 【补全缺失1】sidebar-separate 布局下，主内容区顶部不需要额外 padding，因为面包屑/Tab 嵌入其中
                &.sidebar-separate-menuCollapsed {
                    padding-left: calc(54px + #{$sidebar-separate-margin-left} + #{$main-padding});
                    transition-duration: 0.2s;
                }
            }
        }

        & > :deep(.breadcrumbs) {
            display: flex;
            align-items: center;
            justify-content: space-between;
            height: $headerBreadcrumbHeight;
            background-color: #eef0f7;
            padding: 0 48px;
            color: var(--el-text-color-primary) !important;

            a {
                color: var(--el-text-color-primary) !important;
            }

            &.sidebar-separate-uncollapsed {
                padding-left: calc(#{$sidebar-separate-margin-left} + #{$leftSideBarWidth} + #{$main-padding});
                transition-duration: 0.25s;
            }

            &.sidebar-separate-menuCollapsed {
                padding-left: calc(54px + #{$sidebar-separate-margin-left} + #{$main-padding});
                transition-duration: 0.25s;
            }
        }

        // & > #kernel-tabs {
        //     padding-left: calc(#{$sidebar-separate-margin-left} + #{$leftSideBarWidth} + #{$main-padding});
        //     padding-right: $main-padding;
        //     background-color: var(--el-color-primary-light-9);
        //     & > :deep(div) {
        //         background-color: var(--el-bg-color);
        //         width: 100%;
        //         margin-top: $sidebar-separate-margin-left;
        //     }
        //     & > :deep(i) {
        //         display: none;
        //     }
        // }
    }

    .indexlayout-main-conent {
        margin: 24px;
        position: relative;
    }

    // -----------fixed-header功能 css局部修改---------------------------------
    #indexlayout {
        &.fixedHeader {
            height: auto;

            & > #indexlayout-left {
                position: fixed;
                z-index: 3;
            }

            & > #indexlayout-right {
                padding-left: $leftSideBarWidth;
                transition-duration: 0.2s;
            }
        }

        &.fixedHeader-menuCollapsed {
            & > #indexlayout-right {
                padding-left: $menu-collapsed-width;
                transition-duration: 0.2s;
            }
        }
    }

    // -----------sidebar-separate布局 css局部修改----------------------
    #indexlayout {
        &.sidebar-separate {
            & > #indexlayout-right {
                padding-left: 0;
                transition-duration: 0.2s;
            }
        }

        & > #indexlayout-left.fixed-header-after-scroll {
            & > :deep(div) {
                top: 0;
                left: 0;
                width: calc(#{$leftSideBarWidth} + 20px);
                height: 100vh;
                transition-duration: 0.2s;
            }
        }

        & > #indexlayout-right {
            &.right {
                .indexlayout-right-main {
                    padding-top: 0;

                    &.sidebar-separate-menuCollapsed {
                        padding-left: calc(54px + #{$sidebar-separate-margin-left} + #{$main-padding});
                        transition-duration: 0.2s;
                    }
                }
            }

            & > .breadcrumbs {
                &.sidebar-separate-uncollapsed {
                    padding-left: calc(#{$sidebar-separate-margin-left} + #{$leftSideBarWidth} + #{$main-padding});
                    transition-duration: 0.25s;
                }

                &.sidebar-separate-menuCollapsed {
                    padding-left: calc(54px + #{$sidebar-separate-margin-left} + #{$main-padding});
                    transition-duration: 0.25s;
                }
            }
        }
    }

    // #indexlayout {
    //   & > #indexlayout-right {
    //     & > .tabs-position-left {
    //       padding-left: 18%;
    //     }
    //     & > .tabs-position-right {
    //       padding-right: 18%;
    //     }
    //   }
    // }
</style>
