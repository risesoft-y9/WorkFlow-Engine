<template>
    <el-dropdown @command="onMenuClick" :hide-on-click="true" class="user-el-dropdown">
        <div class="name" @click="(e) => e.preventDefault()">
            <!-- show & if 的vue指令 仅用于适配移动端 -->
            <div v-show="settingStore.getWindowWidth > 425">
                <span
                    >{{ userInfo.name }}
                    <el-badge
                        v-if="flowableStore.allCount > 0"
                        :value="flowableStore.allCount"
                        class="badge"
                    ></el-badge>
                </span>
            </div>
            <i class="ri-shut-down-line" v-if="settingStore.device === 'mobile'"></i>
        </div>
        <template #dropdown>
            <el-dropdown-menu>
                <el-dropdown-item command="personalCenter">
                    <div class="el-dropdown-item"> <i class="ri-user-line"></i>{{ $t('个人中心') }} </div>
                </el-dropdown-item>
                <!-- <el-dropdown-item command="signIn">
                    <div class="el-dropdown-item">
                        <i class="ri-calendar-check-line"></i>已签到
                    </div>
                </el-dropdown-item>
                <el-dropdown-item command="signOut">
                    <div class="el-dropdown-item">
                        <i class="ri-bookmark-line"></i>已签退
                    </div>
                </el-dropdown-item> -->
                <el-divider style="padding-bottom: 5px; margin: 0px; margin-top: 6px"></el-divider>
                <!-- <el-dropdown-item>
                    <div class="el-dropdown-item">
                        <i class="ri-route-line"></i>选择切换岗位
                    </div>
                </el-dropdown-item> -->
                <el-dropdown-item v-for="item in flowableStore.positionList" :key="item.id">
                    <div @click="setPosition(item.id)" class="el-dropdown-item" style="text-align: center">
                        <font v-if="flowableStore.currentPositionId == item.id" color="blue">{{ item.name }}</font>
                        <font v-else>{{ item.name }}</font>
                        <el-badge
                            v-if="item.todoCount > 0"
                            style="top: 3px"
                            :value="item.todoCount"
                            class="item"
                            type="danger"
                        ></el-badge>
                    </div>
                </el-dropdown-item>
                <el-divider style="padding-bottom: 5px; margin: 0px; margin-top: 6px"></el-divider>
                <!-- <el-dropdown-item command="backHome">
                    <div class="el-dropdown-item">
                        <i class="ri-logout-box-r-line"></i>返回首页
                    </div>
                </el-dropdown-item> -->
            </el-dropdown-menu>
        </template>
    </el-dropdown>
    <PersonInfo ref="personInfo" />
</template>
<script lang="ts">
    import { ref, computed, ComputedRef, defineComponent, inject } from 'vue';
    import { useRouter, useRoute } from 'vue-router';
    import { useSettingStore } from '@/store/modules/settingStore';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import y9_storage from '@/utils/storage';
    import IconSvg from './IconSvg';
    import { $y9_SSO } from '@/main';
    import PersonInfo from '@/views/personalCenter/personInfo.vue';
    interface RightTopUserSetupData {
        userInfo: Object;
        onMenuClick: (event: any) => Promise<void>;
        settingStore;
        flowableStore;
        setPosition;
        personInfo;
        fontSizeObj;
    }
    export default defineComponent({
        name: 'RightTopUser',
        components: {
            IconSvg,
            PersonInfo
        },
        setup(): RightTopUserSetupData {
            const settingStore = useSettingStore();
            const flowableStore = useFlowableStore();
            const router = useRouter();
            const currentrRute = useRoute();
            const personInfo = ref();
            // 注入 字体对象
            const fontSizeObj: any = inject('sizeObjInfo');
            // 获取当前登录用户信息
            const userInfo = y9_storage.getObjectItem('ssoUserInfo');
            // 点击菜单
            const onMenuClick = async (command: string) => {
                switch (command) {
                    case 'personalCenter':
                        personInfo.value.show(userInfo);
                        //router.push({name: "personInfo"})
                        break;
                    case 'signIn':
                        break;
                    case 'signOut':
                        break;
                    case 'changeDept':
                        break;
                    case 'backHome':
                        window.location.href = import.meta.env.VUE_APP_HOMEURL;
                        break;

                    default:
                        break;
                }
            };
            const setPosition = async (id) => {
                //切换岗位
                sessionStorage.setItem('positionId', id);
                flowableStore.$patch({
                    currentPositionId: id
                });
                let link = currentrRute.matched[0].path;
                if (link.indexOf('/index') > -1) {
                    window.location.href = import.meta.env.VUE_APP_HOST_INDEX + 'index?itemId=' + flowableStore.itemId;
                } else if (link.indexOf('/workIndex') > -1) {
                    window.location.href = import.meta.env.VUE_APP_HOST_INDEX + 'workIndex';
                }
            };
            return {
                settingStore,
                flowableStore,
                userInfo,
                onMenuClick,
                setPosition,
                personInfo,
                fontSizeObj
            };
        }
    });
</script>
<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';
    .user-el-dropdown {
        z-index: 9999;
        //height: $headerHeight;
        :deep(.el-dropdown--default) {
            display: flex;
            align-items: center;
        }
    }
    .name {
        color: var(--el-text-color-primary);
        font-size: v-bind('fontSizeObj.baseFontSize');
        display: flex;
        & > div {
            display: flex;
            flex-direction: column;
            justify-content: end;
            span {
                line-height: 20px;
                text-align: end;
                margin-top: auto;
            }
        }
        i {
            color: var(--el-color-primary);
            font-size: v-bind('fontSizeObj.maximumFontSize');
            margin-left: 8px;
        }
        :deep(.el-badge) {
            margin-top: 8px;
        }
    }

    .el-dropdown-item {
        width: 100%;
        display: flex;
    }
</style>
