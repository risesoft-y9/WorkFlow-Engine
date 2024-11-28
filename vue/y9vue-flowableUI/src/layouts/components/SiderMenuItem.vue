<template>
    <template v-if="!item.hidden">
        <template v-if="item.children && Array.isArray(item.children) && hasChildRoute(item.children)">
            <el-sub-menu :index="item.name" class="y9-el-sub-menu">
                <template #title>
                    <template v-if="item.meta.icon">
                        <img
                            v-if="item.meta.icon.indexOf('data:image/png;base64') > -1"
                            :src="item.meta.icon"
                            style="height: 25px; vertical-align: middle"
                        />
                        <i
                            v-if="item.meta.icon.indexOf('data:image/png;base64') == -1"
                            :class="['icon', item.meta.icon]"
                        />
                    </template>
                    {{ $t(item.meta.title) }}
                    <div v-if="item.todoCount != 0">
                        <el-badge :value="item.todoCount" class="item submenu" sty type="danger"></el-badge>
                    </div>
                </template>
                <sider-menu-item
                    v-for="item2 in item.children"
                    :key="item2.path"
                    :belongTopMenu="belongTopMenu"
                    :routeItem="item2"
                ></sider-menu-item>
            </el-sub-menu>
        </template>
        <template v-else>
            <template v-if="item.name == 'email'">
                <a-link to="">
                    <el-menu-item :index="item.path" @click="openEmail()">
                        <template v-if="item.meta.icon">
                            <img
                                v-if="item.meta.icon.indexOf('data:image/png;base64') > -1"
                                :src="item.meta.icon"
                                style="height: 25px; vertical-align: middle; margin-right: 15px; margin-left: -5px"
                            />
                            <img
                                v-else-if="item.meta.icon.indexOf('assets') > -1"
                                :src="getImageUrl(item.meta.icon.split('/')[1])"
                                style="height: 25px; vertical-align: middle; margin-right: 15px; margin-left: -5px"
                            />
                            <i
                                v-else-if="item.meta.icon.indexOf('data:image/png;base64') == -1"
                                :class="['icon', item.meta.icon]"
                            />
                        </template>
                        <template #title>
                            {{ $t(item.meta.title) }}
                            <div v-if="item.todoCount != 0">
                                <el-badge :value="item.todoCount" class="item submenu" type="danger"></el-badge>
                            </div>
                        </template>
                    </el-menu-item>
                </a-link>
            </template>
            <template v-else>
                <a-link
                    :to="
                        item.path +
                        '?itemId=' +
                        (item.path.indexOf('/workIndex') > -1 && item.itemId != undefined
                            ? item.itemId
                            : flowableStore.itemId)
                    "
                    @click="setAppType(item)"
                >
                    <el-menu-item
                        :index="item.path + (item.path.indexOf('/workIndex') > -1 ? item.itemId : '')"
                        @click="toggleCollapsedFunc"
                    >
                        <template v-if="item.meta.icon">
                            <img
                                v-if="item.meta.icon.indexOf('data:image/png;base64') > -1"
                                :src="item.meta.icon"
                                style="height: 25px; vertical-align: middle; margin-right: 15px; margin-left: -5px"
                            />
                            <img
                                v-else-if="item.meta.icon.indexOf('assets') > -1"
                                :src="getImageUrl(item.meta.icon.split('/')[1])"
                                style="height: 25px; vertical-align: middle; margin-right: 15px; margin-left: -5px"
                            />
                            <i
                                v-else-if="item.meta.icon.indexOf('data:image/png;base64') == -1"
                                :class="['icon', item.meta.icon]"
                            />
                        </template>
                        <template #title>
                            {{ $t(item.meta.title) }}
                            <div v-if="item.name == 'draft' && flowableStore.getDraftCount != 0">
                                <el-badge
                                    :value="flowableStore.getDraftCount"
                                    class="item submenu"
                                    type="primary"
                                ></el-badge>
                            </div>
                            <div v-if="item.name == 'todo' && flowableStore.getTodoCount != 0">
                                <el-badge
                                    :value="flowableStore.getTodoCount"
                                    class="item submenu"
                                    type="danger"
                                ></el-badge>
                            </div>
                            <div v-if="item.name == 'doing' && flowableStore.getDoingCount != 0">
                                <el-badge
                                    :value="flowableStore.getDoingCount"
                                    class="item submenu"
                                    type="primary"
                                ></el-badge>
                            </div>
                            <div v-if="item.name == 'done' && flowableStore.getDoneCount != 0">
                                <el-badge
                                    :value="flowableStore.getDoneCount"
                                    class="item submenu"
                                    type="primary"
                                ></el-badge>
                            </div>
                            <div v-if="item.name == 'draftRecycle' && flowableStore.getDraftRecycleCount != 0">
                                <el-badge
                                    :value="flowableStore.getDraftRecycleCount"
                                    class="item submenu"
                                    type="primary"
                                ></el-badge>
                            </div>
                            <div v-if="item.name == 'monitorDoing' && flowableStore.getMonitorDoing != 0">
                                <el-badge
                                    :value="flowableStore.getMonitorDoing"
                                    class="item submenu"
                                    type="primary"
                                ></el-badge>
                            </div>
                            <div v-if="item.name == 'monitorDone' && flowableStore.getMonitorDone != 0">
                                <el-badge
                                    :value="flowableStore.getMonitorDone"
                                    class="item submenu"
                                    type="primary"
                                ></el-badge>
                            </div>
                            <div v-if="item.name == 'wtodo' && flowableStore.getWtodoCount != 0">
                                <el-badge
                                    :value="flowableStore.getWtodoCount"
                                    class="item submenu"
                                    type="primary"
                                ></el-badge>
                            </div>
                            <div v-if="item.name == 'wdone' && flowableStore.getWdoneCount != 0">
                                <el-badge
                                    :value="flowableStore.getWdoneCount"
                                    class="item submenu"
                                    type="primary"
                                ></el-badge>
                            </div>
                            <div v-else-if="item.todoCount != 0">
                                <el-badge :value="item.todoCount" class="item submenu" type="danger"></el-badge>
                            </div>
                        </template>
                    </el-menu-item>
                </a-link>
            </template>
        </template>
    </template>
</template>
<script lang="ts">
    import { computed, ComputedRef, defineComponent, inject, nextTick, PropType, Ref, toRefs } from 'vue';
    import { getRouteBelongTopMenu, hasChildRoute, RoutesDataItem } from '@/utils/routes';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import ALink from '@/layouts/components/ALink/index.vue';
    import Icon from './Icon.vue';
    import settings from '@/settings';
    import { useRoute, useRouter } from 'vue-router';

    interface SiderMenuItemSetupData {
        item: Ref;
        topMenuPath: ComputedRef<string>;
        hasChildRoute: (children: RoutesDataItem[]) => boolean;
        toggleCollapsedFunc: () => void;
        flowableStore;
        getImageUrl;
        setAppType;
        openEmail;
        fontSizeObj;
    }

    export default defineComponent({
        name: 'SiderMenuItem',
        props: {
            routeItem: {
                type: Object as PropType<RoutesDataItem>,
                required: true
            },
            belongTopMenu: {
                type: String,
                default: ''
            }
        },
        components: {
            ALink,
            Icon
        },
        setup(props): SiderMenuItemSetupData {
            const { routeItem } = toRefs(props);
            const topMenuPath = computed<string>(() => getRouteBelongTopMenu(routeItem.value as RoutesDataItem));
            const router = useRouter();
            const currentrRute = useRoute();
            const flowableStore = useFlowableStore();
            const settingStore = useSettingStore();
            const { toggleCollapsed } = settingStore;
            const toggleCollapsedFunc = () => {
                if (settingStore.getDevice === 'mobile') {
                    toggleCollapsed();
                }
            };
            const getImageUrl = (name) => {
                return new URL(`../../assets/${name}`, import.meta.url).href;
            };
            const setAppType = (app) => {
                if (routeItem.value.path.indexOf('/workIndex') > -1) {
                    flowableStore.$patch({
                        appType: app.name,
                        itemName: app.parentTitle,
                        itemId: app.itemId != undefined ? app.itemId : flowableStore.itemId
                    });
                    if (currentrRute.path == routeItem.value.path && app.itemId != undefined) {
                        flowableStore.$patch({
                            isReload: false
                        });
                        setTimeout(() => {
                            nextTick(() => {
                                flowableStore.$patch({
                                    isReload: true
                                });
                            });
                        }, 500);
                    }
                }
            };

            const openEmail = () => {
                window.location.href = settings.emailURL;
            };
            // 注入 字体对象
            const fontSizeObj: any = inject('sizeObjInfo');
            return {
                item: routeItem,
                topMenuPath: topMenuPath,
                hasChildRoute,
                toggleCollapsedFunc,
                flowableStore,
                getImageUrl,
                setAppType,
                openEmail,
                fontSizeObj
            };
        }
    });
</script>

<style lang="scss" scoped>
    .y9-el-sub-menu {
        :deep(.el-sub-menu__title) {
            font-size: v-bind('fontSizeObj.baseFontSize');
        }

        & > div {
            text-decoration: none;

            i {
                font-size: v-bind('fontSizeObj.largeFontSize');
                margin-right: 15px;
            }
        }
    }

    .el-teleport,
    .el-popper {
        ul.el-menu {
            & > a {
                text-decoration: none;
            }

            li.el-menu-item {
                & > i {
                    font-size: v-bind('fontSizeObj.largeFontSize');
                    margin-right: 15px;
                }
            }
        }
    }

    .el-menu {
        background-color: none;
    }

    .el-menu-item {
        font-size: v-bind('fontSizeObj.baseFontSize');
        line-height: 32px;

        :deep(.el-badge) {
            margin-left: 5px;
            margin-top: 2px;
        }
    }
</style>
<style>
    .y9-el-sub-menu.el-sub-menu .el-menu {
        background: transparent;
    }

    .submenu .el-badge__content {
        vertical-align: middle;
        top: -2.5px;
    }
</style>
