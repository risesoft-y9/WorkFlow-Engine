<template>
    <el-dialog
        v-model="dialogVisible"
        :title="$t(dialogTitle)"
        append-to-body
        destroy-on-close="true"
        top="100px"
        width="50%"
    >
        <div style="margin-left: 0%">
            <el-tabs v-model="activeName" class="demo-tabs" style="height: 600px; overflow: auto" tab-position="left">
                <el-tab-pane :label="$t('常用语设置')" :name="$t('常用语设置')">
                    <CommonManage v-if="activeName == '常用语设置'" ref="commonManageRef" />
                </el-tab-pane>
                <el-tab-pane :label="$t('委托设置')" :name="$t('委托设置')">
                    <EntrustManage v-if="activeName == '委托设置'" ref="entrustManageRef" />
                </el-tab-pane>
            </el-tabs>
        </div>
    </el-dialog>
</template>
<script lang="ts" setup>
    import { defineExpose, inject, ref } from 'vue';
    import CommonManage from '@/views/personalCenter/commonManage.vue';
    import EntrustManage from '@/views/personalCenter/entrustManage.vue';
    // 注入 字体对象
    const fontSizeObj: any = inject('sizeObjInfo');
    const dialogTitle = ref('');
    const dialogVisible = ref(false);

    const activeName = ref('常用语设置');

    defineExpose({ show });

    function show(id) {
        dialogVisible.value = true;
        dialogTitle.value = '个人中心';
    }
</script>
<style>
    .el-dialog__body {
        padding-top: 0px;
    }

    .box-card {
        width: 100%;
    }

    .clearfix {
        text-align: center;
        font-size: v-bind('fontSizeObj.largeFontSize');
        font-weight: 600;
    }
</style>
