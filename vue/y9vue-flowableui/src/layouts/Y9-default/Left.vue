<template>
    <div
        id="left"
        :class="{
            narrow: menuCollapsed,
            'sidebar-separate': layoutSubName === 'sidebar-separate' ? true : false,
            'add-backgroundImage': settingStore.getMenuBg ? true : false
        }"
        :style="{ 'background-image': settingStore.getMenuBg ? 'url(' + settingStore.getMenuBg + ')' : '' }"
    >
        <div class="left-logo">
            <div class="logo-url">
                <img v-if="menuCollapsed" alt="y9-logo" src="@/assets/images/yun.png" />
                <span v-if="!menuCollapsed" class="logo-title">{{
                    currentrRute.path.indexOf('/workIndex') > -1 ? $t('工作台') : $t(flowableStore.itemName)
                }}</span>
            </div>
        </div>
        <div class="left-menu">
            <!-- <div style="height:56px;" id="indexlayout-left" :class="{ narrow: menuCollapsed }">
              <li style="line-height:56px;list-style:none;font-size: 16px;letter-spacing: 2px;margin-left:10px;">
                <div @click="reload" style="margin-left:0px; text-decoration: none; color: #555555;">
                  <img title="工作台" v-if="currentrRute.path.indexOf('/workIndex') > -1" src="@/assets/gongzuotai.png" style="height:35px;vertical-align: middle;"/> -->
            <!-- <i class="ri-computer-line" title="工作台" v-if="currentrRute.path.indexOf('/workIndex') > -1" style="font-size: 24px;vertical-align: middle;"></i>
            <img :title="flowableStore.itemInfo.name" v-else-if="flowableStore.itemInfo.iconData == '' || flowableStore.itemInfo.iconData == null" src="@/assets/images/gongdan.png" style="height:35px;vertical-align: middle;"/>
            <img :title="flowableStore.itemInfo.name" v-else :src="flowableStore.itemInfo.iconData" style="height:35px;vertical-align: middle;"/>
            <span v-if="!menuCollapsed" style="margin-left: 10px;vertical-align: middle;font-size: 20px;">{{currentrRute.path.indexOf('/workIndex') > -1 ? '工作台' : flowableStore.itemName}}</span>
          </div>
        </li>
      </div> -->
            <sider-menu
                :belongTopMenu="belongTopMenu"
                :defaultActive="defaultActive"
                :defaultOpened="defaultOpened"
                :menuCollapsed="menuCollapsed"
                :menuData="menuData"
            ></sider-menu>
        </div>
    </div>
</template>
<script lang="ts" setup>
    import { inject } from 'vue';
    import SiderMenu from '@/layouts/components/SiderMenu.vue';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useRoute } from 'vue-router';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    const settingStore = useSettingStore();
    const props = defineProps({
        menuCollapsed: {
            type: Boolean as computed<Boolean>,
            required: true
        },
        belongTopMenu: {
            type: String,
            default: ''
        },
        defaultActive: {
            type: String,
            default: ''
        },
        defaultOpened: {
            type: String,
            default: ''
        },
        menuData: {
            type: Array,
            default: () => {
                return [];
            }
        },
        layoutSubName: {
            type: String as Ref<string>,
            required: true
        }
    });

    onMounted(() => {});
</script>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    #left .el-menu-item {
        height: 38px !important;
    }

    $sidebar-separate-margin-top: calc(#{$sidebar-separate-margin-left} + #{$headerHeight});
    $sidebar-separate-menu-height: calc(100vh - #{$sidebar-separate-margin-top});

    #left {
        display: flex;
        height: 100vh;
        flex-direction: column;
        width: $leftSideBarWidth;
        background-color: var(--el-bg-color);
        //background-color: #161b2d;
        //border-right: 1px solid #f8f8f8;
        transition-duration: 0.25s;

        &.sidebar-separate {
            position: absolute;
            z-index: 1;
            left: $sidebar-separate-margin-left;
            top: $sidebar-separate-margin-top;
            height: $sidebar-separate-menu-height;
            border-top-left-radius: 0.25rem;
            border-top-right-radius: 0.25rem;
            box-shadow: 2px 2px 2px 1px rgba(0, 0, 0, 0.06);
        }

        .left-logo {
            width: 100%;
            height: $headerHeight;
            line-height: $headerHeight;
            text-align: center;
            vertical-align: middle;

            .logo-url {
                display: inline-block;
                width: 100%;
                height: 100%;
                overflow: hidden;

                .logo-title {
                    display: inline-block;
                    font-size: v-bind('fontSizeObj.extraLargeFont');
                    font-weight: 500;

                    color: var(--el-color-primary);
                }
            }

            img {
                width: $logoWidth;
                vertical-align: middle;
            }
        }

        .left-menu {
            flex: 1;
            overflow: hidden auto;

            & > ul {
                border-right: none;
                background-color: var(--el-bg-color);
                //background-color: #161b2d;
                :deep(a) {
                    text-decoration: none;

                    & > li {
                        //  font-size: 15px;
                        i {
                            margin-right: 15px;
                            font-size: v-bind('fontSizeObj.largeFontSize');
                        }

                        &.is-active {
                            color: var(--el-color-primary);
                            background-color: $background-color;
                        }
                    }

                    & li:hover {
                        background-color: var(--el-color-primary-light-9);
                        color: var(--el-color-primary-light-3);
                    }
                }
            }

            .left-scrollbar {
                width: 100%;
                height: 100%;
            }
        }

        &.narrow {
            width: $menu-collapsed-width;
        }

        @include scrollbar;
    }

    // 设置菜单背景时 css修改
    #left.add-backgroundImage {
        & > .left-logo {
            & > .logo-url .logo-title {
                color: var(--el-color-white);
            }
        }

        & > .left-menu {
            & > ul {
                background-color: transparent;
                background: transparent;

                :deep(a) {
                    text-decoration: none;

                    & > li {
                        color: var(--el-color-white);

                        &.is-active {
                            color: var(--el-color-primary);
                            background-color: var(--el-color-primary-light-9);
                        }
                    }

                    :hover {
                        color: var(--el-color-primary);
                        background-color: var(--el-color-primary-light-9);
                    }
                }

                :deep(li) {
                    .el-sub-menu__title {
                        color: var(--el-color-white);
                    }

                    div:hover {
                        color: var(--el-color-primary);
                        background-color: var(--el-color-primary-light-9);
                    }

                    ul > a:hover {
                        color: var(--el-color-primary);
                        background-color: var(--el-color-primary-light-9);
                    }
                }
            }
        }
    }
</style>
