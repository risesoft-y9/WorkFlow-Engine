<template>
    <div id="indexlayout">
        <div id="indexlayout-right">
            <RightTop id="right-top" @refresh="refreshFunc" />
            <Navs
                id="header-menus"
                :belongTopMenu="belongTopMenu"
                :defaultActive="defaultActive"
                :menuCollapsed="menuCollapsed"
                :menuData="menuData"
            />
            <BreadCrumbs
                :layoutSubName="layoutSubName"
                :list="breadCrumbs"
                :menuCollapsed="menuCollapsed"
                class="breadcrumbs"
            />
            <div :key="refreshContent" class="indexlayout-right-main">
                <router-view v-if="flowableStore.isReload" v-on:refreshCount="indexRefreshCount()"></router-view>
            </div>
        </div>
    </div>
    <!-- <component :is="settingPageStyle === 'Dcat' ? Settings : ''"></component> -->
    <component :is="settingPageStyle === 'Admin-plus' ? Settings : ''"></component>
    <!-- <component :is="settingStore.getLockScreen ? Lock : ''"></component> -->
    <Lock v-show="settingStore.getLockScreen" />
    <Search />
</template>

<script lang="ts" setup>
    import { computed, ref } from 'vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import type { BreadcrumbType, RoutesDataItem } from '@/utils/routes';

    import Navs from './Navs.vue';
    import RightTop from './RightTop.vue';
    import Settings from '../components/SettingsMobile.vue';
    import BreadCrumbs from '@/layouts/components/BreadCrumbs/index.vue';
    import Lock from '@/layouts/components/Lock/index.vue';
    import Search from '@/layouts/components/search/index.vue';
    import { useFlowableStore } from '@/store/modules/flowableStore';

    const props = withDefaults(
        defineProps<{
            layoutName: string;
            layoutSubName: string;
            menuData: RoutesDataItem[];
            menuCollapsed: boolean;
            belongTopMenu: string;
            defaultActive: string;
            defaultOpened: string;
            breadCrumbs: BreadcrumbType[];
            routeItem: RoutesDataItem;
        }>(),
        { menuCollapsed: false }
    );

    const flowableStore = useFlowableStore();
    const settingStore = useSettingStore();
    const settingPageStyle = computed(() => settingStore.getSettingPageStyle);

    const emits = defineEmits(['indexRefreshCount']);
    const refreshContent = ref(0);

    function refreshFunc() {
        refreshContent.value++;
    }

    async function indexRefreshCount() {
        emits('indexRefreshCount');
    }
</script>
<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    // 定义局部变量，确保与全局变量兼容或提供默认值
    $nav-height: 55px;

    #indexlayout {
        display: flex;
        height: 100vh;
        width: 100%;
        overflow: hidden; // 彻底禁止全局页面滚动
        min-width: 1350px; // 保留最小宽度约束，防止极端小屏布局崩坏

        #indexlayout-right {
            position: relative; // 为内部绝对定位元素提供上下文
            display: flex;
            flex-direction: column;
            flex: 1;
            height: 100%;
            overflow: hidden; // 外层容器禁止滚动，仅主内容区允许滚动
            background-color: var(--el-color-primary-light-9);
            min-width: 1364px; // 恢复原代码中的右侧最小宽度约束
            min-height: 780px; // 恢复原代码中的右侧最小高度约束

            // 顶部栏：固定定位，脱离 flex 流
            > #right-top {
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                z-index: 10;
                height: $headerHeight;
            }

            // 导航菜单：固定定位，脱离 flex 流
            > #header-menus {
                position: fixed;
                top: $headerHeight;
                left: 0;
                right: 0;
                z-index: 10;
                height: $nav-height;
            }

            // 面包屑：固定定位，脱离 flex 流
            > .breadcrumbs {
                position: fixed;
                top: calc(#{$headerHeight} + #{$nav-height});
                left: 0;
                right: 0;
                z-index: 10;
                display: flex;
                align-items: center;
                justify-content: space-between;
                margin: 0 auto; // 恢复面包屑居中布局
                height: $headerBreadcrumbHeight;
                background-color: #eef0f7;
                padding: 0 35px; // 保持改造后的内边距，也可根据需求改回 48px
            }

            // 唯一滚动区域
            > .indexlayout-right-main {
                flex: 1; // 自动占满剩余所有可用空间
                overflow-y: auto; // 仅开启垂直方向滚动
                background-color: #eff1f7; // 恢复原代码硬编码背景色，避免变量未定义错误
                padding: $main-padding;
                padding-top: 0;
                padding-bottom: 0;
                position: relative; // 确保内部绝对定位元素相对于此容器定位
                // 默认（无面包屑）：margin-top = header(60) + nav(55) = 115px
                margin-top: calc(#{$nav-height} - 2px);

                // 应用全局滚动条样式 mixin
                @include scrollbar;

                // 表格高度：默认无面包屑时 100vh - 260px
                // :deep(.el-table) {
                //     height: calc(100vh - 260px) !important;
                // }

                // 恢复丢失的深度选择器样式，确保内部组件定位正确
                :deep(.nav) {
                    position: absolute;
                    top: 15%;
                    left: 6%;
                }

                :deep(.buttonDiv1) {
                    position: absolute; // 补充 position 以确保 right 生效
                    right: 0%;
                }

                :deep(.tab-nav) {
                    position: absolute; // 补充 position 以确保 top 生效
                    top: -#{$headerTabNavHeight};
                }
            }

            // 有面包屑时：main margin-top
            // tableHeight 偏移量增加到 340px
            &:has(> .breadcrumbs) > .indexlayout-right-main {
                margin-top: calc(#{$nav-height} + #{$headerBreadcrumbHeight});
                padding-bottom: $main-padding;

                // :deep(.el-table) {
                //     height: calc(100vh - 340px) !important;
                // }
            }
        }
    }
</style>
