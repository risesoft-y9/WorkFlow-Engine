<template>
    <div class="item" @click="showInfo">
        <i class="ri-user-line"></i>
        <span>{{ $t('个人中心') }}</span>
    </div>
    <div v-if="positions?.length" class="item">
        <el-dropdown :hide-on-click="true" class="position-el-dropdown" @command="onMenuClick">
            <div class="name" @click="(e) => e.preventDefault()">
                <!-- show & if 的vue指令 仅用于适配移动端 -->
                <div>
                    <i class="ri-route-line"></i>
                    <span>{{ $t('选择岗位') }}</span>
                    <el-badge :value="flowableStore.allCount - flowableStore.currentCount" class="badge"></el-badge>
                </div>
            </div>
            <template #dropdown>
                <el-dropdown-menu>
                    <div v-for="(item, index) in flowableStore.positionList" :key="index">
                        <el-dropdown-item v-if="item.id !== positionId" :command="item">
                            <div class="el-dropdown-item">
                                <div>
                                    <i class="ri-shield-user-line"></i>{{ item.name }}
                                    <el-badge :value="item.todoCount" class="badge"></el-badge>
                                </div>
                            </div>
                        </el-dropdown-item>
                    </div>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
    </div>
    <div v-else class="item" style="display: none">
        <div class="position-el-dropdown">
            <!-- show & if 的vue指令 仅用于适配移动端 -->
            <div class="name" style="cursor: not-allowed; color: #aaa">
                <i class="ri-route-line"></i>
                <span>{{ $t('选择岗位') }}</span>
            </div>
        </div>
    </div>
    <div class="item notify">
        <i class="ri-user-location-line"></i>
        <span>{{ positionName }}</span>
        <el-badge v-if="flowableStore.currentCount! > 0" :value="flowableStore.currentCount" class="badge"></el-badge>
    </div>
    <PersonInfo ref="personInfo" />
</template>
<script lang="ts" setup>
    import { inject, ref } from 'vue';
    import PersonInfo from '@/views/personalCenter/personInfo.vue';
    import { useRoute } from 'vue-router';
    import y9_storage from '@/utils/storage';
    import { useFlowableStore } from '@/store/modules/flowableStore';

    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    if (currentrRute.path == '/index/work' || currentrRute.path == '/readIndex') {
        //处理消息机器人跳转办理，消息机器人跳转带岗位id
        let type = currentrRute.query?.type;
        if (type != undefined) {
            if (type == 'fromTodo') {
                //统一待办跳转过来
                let positionId = currentrRute.query.positionId;
                if (positionId != undefined && positionId != null) {
                    sessionStorage.setItem('positionId', positionId);
                    flowableStore.positionList.forEach((item, index) => {
                        if (item.id == positionId) {
                            sessionStorage.setItem('positionName', item.name);
                        }
                    });
                }
            }
        }
    }

    let positionName = sessionStorage.getItem('positionName') ? sessionStorage.getItem('positionName') : '';

    // const personInfo = ref();
    // 获取当前登录用户信息
    const userInfo = y9_storage.getObjectItem('ssoUserInfo');
    // 岗位列表
    let positions: any = flowableStore.positionList;
    let positionId = sessionStorage.getItem('positionId');

    positions.forEach((item, index) => {
        if (item.id == positionId) {
            positions.splice(index, 1);
        }
    });

    // 点击菜单
    const onMenuClick = async (command: any) => {
        sessionStorage.setItem('positionId', command?.id);
        // 设置 positionName
        sessionStorage.setItem('positionName', command?.name);
        flowableStore.$patch({
            currentPositionId: command?.id,
            currentCount: command?.todoCount,
            currentPositionName: command?.todoCount
        });
        let link = currentrRute.matched[0].path;
        if (link.indexOf('/index') > -1) {
            window.location.href = import.meta.env.VUE_APP_HOST_INDEX + 'index?itemId=' + flowableStore.itemId;
        } else if (link.indexOf('/workIndex') > -1) {
            window.location.href = import.meta.env.VUE_APP_HOST_INDEX + 'workIndex';
        }
    };

    const personInfo = ref();
    const showInfo = () => {
        personInfo.value.show(userInfo);
    };
</script>
<style lang="scss" scoped>
    @import '@/theme/global-vars.scss';

    .item {
        overflow: hidden;
        padding: 0 11px;
        min-width: 5px;

        i {
            position: relative;
            top: 4px;
        }

        span {
            font-size: v-bind('fontSizeObj.baseFontSize');
            margin-left: 5px;
        }

        &:hover {
            cursor: pointer;
            color: var(--el-color-primary);
        }

        &.notify {
            .badge {
                top: -4px;
                z-index: 1;

                & > .el-badge__content--danger {
                    background-color: var(--el-color-danger);
                }
            }
        }
    }

    .position-el-dropdown {
        z-index: 9999;
        min-width: 5px;
        height: $headerHeight;
        text-align: center;
        line-height: 50px;
        font-size: v-bind('fontSizeObj.extraLargeFont');

        :deep(.el-dropdown--default) {
            display: flex;
            align-items: center;
        }

        i {
            position: relative;
            top: 4px;
        }

        span {
            margin-left: 5px;
        }

        .badge {
            // position: absolute;
            top: -4px;
            margin-left: 7px;
            z-index: 1;

            & > .el-badge__content--danger {
                background-color: var(--el-color-danger);
            }
        }
    }

    .el-dropdown-item {
        div {
            width: 100%;
            display: flex;
            padding: 0 5px;
            top: 2px;
            left: -5px;
        }
    }

    :deep(.el-badge) {
        .el-badge__content {
            border: none;
        }

        sup {
            top: 0;
        }
    }
</style>
