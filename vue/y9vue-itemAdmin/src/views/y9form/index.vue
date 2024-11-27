<!--
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-05-05 11:38:27
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-11 09:32:35
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\views\y9form\index.vue
-->
<template>
    <systemTree ref="itemTreeRef" @onTreeClick="onTreeClick">
        <template #rightContainer>
            <tableManage ref="tableManageRef" :currTreeNodeInfo="currTreeNodeInfo"></tableManage>
            <formManage ref="formManageRef" :currTreeNodeInfo="currTreeNodeInfo"></formManage>
        </template>
    </systemTree>
    <y9Dialog v-model:config="dialogConfig">
        <itemForm ref="itemFormRef" isEditState="true"></itemForm>
    </y9Dialog>
</template>
<script lang="ts" setup>
    import { reactive } from 'vue';
    import systemTree from './systemTree.vue';
    import tableManage from '@/views/y9form/table/tableManage.vue';
    import formManage from '@/views/y9form/form/formManage.vue';
    //数据
    const data = reactive({
        currTreeNodeInfo: {}, //当前tree节点的信息
        //弹窗配置
        dialogConfig: {
            show: false,
            title: '',
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {
                    let result = { success: false, msg: '' };
                    ElNotification({
                        title: result.success ? '成功' : '失败',
                        message: result.msg,
                        type: result.success ? 'success' : 'error',
                        duration: 2000,
                        offset: 80
                    });
                    resolve();
                });
            },
            visibleChange: (visible) => {
                if (!visible) {
                    dialogConfig.value.onOkLoading = false;
                }
            }
        }
    });

    const { currTreeNodeInfo, dialogConfig } = toRefs(data);

    //点击tree的回调
    function onTreeClick(currTreeNode) {
        currTreeNodeInfo.value = currTreeNode;
    }
</script>

<style scoped lang="scss"></style>
