<script lang="ts" setup>
    import RightTopUser from '../components/RightTopUser.vue';
    import { Edit } from '@element-plus/icons';
    import { useSettingStore } from '@/store/modules/settingStore';
    import y9_storage from '@/utils/storage';

    const settingStore = useSettingStore();

    // 全屏功能
    const { isFullscreen, toggle } = useFullscreen();
    const toggleFullScreen = toggle;

    // 白天黑夜功能
    const isDark = useDark({
        selector: 'html',
        valueDark: 'theme-dark',
        valueLight: '',
    });
    const toggleDark = useToggle(isDark);

    // 锁屏
    const lockScreenFunc = () => {
        settingStore.$patch({
            lockScreen: true,
        });
    };

    // 搜索
    const searchFunc = () => {};
    // 刷新
    const emits = defineEmits(['refresh']);
    const refreshFunc = () => {
        // window.location = window.location.href
        emits('refresh');
    };

    // 个人信息 —— 头像
    const userInfo = y9_storage.getObjectItem('ssoUserInfo');
</script>

<template>
    <div id="right-top">
        <div class="left">
            <span>{{ $t('事项管理') }}</span>
        </div>
        <!-- <div class="center">
            
        </div> -->
        <div class="right">
            <div class="item full-screen" @click="toggleFullScreen">
                <i class="ri-fullscreen-line"></i>
                <span>{{ $t('全屏') }}</span>
            </div>
            <!-- <div class="item search" @click="searchFunc" v-show="settingStore.getSearch">
                <i class="ri-search-line"></i>
                <span></span>
            </div> -->
            <div class="item" @click="lockScreenFunc" v-show="settingStore.getLock">
                <i class="ri-lock-2-line"></i>
                <span>{{ $t('锁屏') }}</span>
            </div>
            <div class="item web-setting">
                <i class="ri-edit-box-line"></i>
                <span>{{ $t('设置') }}</span>
            </div>
            <!-- <div class="item notify">
                <el-badge :value="3" class="badge"></el-badge>
                <i class="ri-notification-line"></i>
            </div> -->
            <div class="item" @click="refreshFunc" v-show="settingStore.getRefresh">
                <i class="ri-refresh-line"></i>
                <span>{{ $t('刷新') }}</span>
            </div>
            <!-- <div class="item isDark">
                <i class="ri-moon-line" @click="toggleDark" v-if="!isDark"></i>
                <i class="ri-sun-line" @click="toggleDark" v-else></i>
            </div> -->
            <div class="item user">
                <RightTopUser style="z-index: 9999" />
            </div>
            <div class="item user">
                <!-- <img src="@/assets/images/app-icon.png"> -->
                <el-avatar :src="userInfo.avator ? userInfo.avator : ''"> {{ userInfo.loginName }} </el-avatar>
            </div>
            <!-- <div class="item" @click="logout">
                <i class="ri-logout-box-r-line"></i>
                <span>{{ '退出' }}</span>
            </div> -->
        </div>
    </div>
</template>

<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';
    #right-top {
        background-color: var(--el-bg-color);
        color: var(--el-text-color-primary);
        display: flex;
        justify-content: space-between;
        width: 100%;
        z-index: 2;
        border-bottom: 1px solid var(--el-color-primary-light-9);
        .left,
        .center,
        .right {
            display: flex;
            height: $headerHeight;
            line-height: $headerHeight;
            font-size: var(--el-font-size-extra-large);
            & > .item {
                overflow: hidden;
                padding: 0 15px;
                min-width: 5px;
                display: flex;
                align-items: center;
                i {
                    position: relative;
                    // top: 4px;
                }
                span {
                    font-size: var(--el-font-size-base);
                    margin-left: 5px;
                }
                &:hover {
                    cursor: pointer;
                    color: var(--el-color-primary);
                }
            }
        }

        .left {
            min-width: 300px;
            margin-left: 20px;
            // v-bind('fontSizeObj.extraLargeFont')
            font-size: 20px;
            font-weight: 500;
            color: var(--el-color-primary);
        }
        .right {
            //min-width: 300px;
            & > .item {
                &.notify {
                    .badge {
                        position: absolute;
                        z-index: 1;
                        & > .el-badge__content--danger {
                            background-color: var(--el-color-danger);
                        }
                    }
                }
                &.user {
                    //min-width: 202px;
                    padding: 0 12px;
                    display: flex;
                    align-items: center;

                    & > img {
                        height: $headerHeight - 18px;
                        width: $headerHeight - 18px;
                        border-radius: 50%;
                    }
                    & > .name {
                        font-size: var(--el-font-size-base);
                        display: flex;
                        flex-direction: column;
                        justify-content: end;
                        span {
                            line-height: 20px;
                            text-align: end;
                        }
                    }
                    i {
                        color: var(--el-color-primary);
                        font-size: 48px;
                        margin-left: 8px;
                        margin-bottom: 8px;
                    }
                    .el-avatar {
                        background-color: var(--el-color-primary);
                    }
                }
            }
        }
    }
</style>
