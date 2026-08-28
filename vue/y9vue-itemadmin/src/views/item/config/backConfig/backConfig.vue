<!--
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-13 09:49:46
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2026-06-01 14:38:43
 * @Descripttion: 按钮配置
 * @FilePath: \y9-vue\y9vue-itemAdmin\src\views\item\config\buttonConfig\buttonConfig.vue
-->
<template>
    <y9Card :title="`退回配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
        <y9Table v-model:selectedVal="userSelectedData" :config="tableConfig">
            <template #opt_button="{ row, column, index }">
                <span style="margin-right: 15px; font-weight: 600" @click="taskBindManage(row)"
                    ><i class="ri-add-line"></i>退回配置</span
                >
            </template>
        </y9Table>

        <y9Dialog v-model:config="dialogConfig">
            <taskBind
                ref="taskBindRef"
                :currTreeNodeInfo="currTreeNodeInfo"
                :processDefinitionId="currTreeNodeInfo.processDefinitionId"
                :taskDefKey="taskDefKey"
                :taskDefName="taskDefName"
                :bpmnList="tableConfig.tableData"
            />
        </y9Dialog>
    </y9Card>
</template>

<script lang="ts" setup>
    import { onMounted, reactive, toRefs, watch } from 'vue';
    import { $deepAssignObject } from '@/utils/object';
    import taskBind from './taskBind.vue';
    import { getBpmList } from '@/api/itemAdmin/item/backConfig';

    const props = defineProps({
        currTreeNodeInfo: {
            //当前tree节点信息
            type: Object,
            default: () => {
                return {};
            }
        },
        maxVersion: Number,
        selectVersion: Number
    });

    const data = reactive({
        userSelectedData: '',
        //当前节点信息
        currInfo: props.currTreeNodeInfo,
        tableConfig: {
            columns: [
                {
                    title: '序号',
                    type: 'index',
                    width: '60'
                },
                {
                    title: '任务节点名称',
                    key: 'taskDefName',
                    width: '260'
                },
                {
                    title: '可退回任务节点',
                    key: 'realTaskDefName',
                },
                {
                    title: '操作',
                    width: '220',
                    slot: 'opt_button'
                }
            ],
            showOverflowTooltip:false,
            tableData: [],
            pageConfig: false, //取消分页
            height: 'auto'
        },
        dialogConfig: {
            show: false,
            width: '',
            title: '',
            showFooter: false,
            onOkLoading: true,
            onOk: (newConfig) => {
                return new Promise(async (resolve, reject) => {});
            },
            visibleChange: (visible) => {
                console.log('visible', visible);
                if (!visible) {
                    getButtonList();
                }
            }
        },
        taskDefKey: '',
        taskDefName:''
    });

    let { userSelectedData, currInfo, tableConfig, dialogConfig, taskDefKey,taskDefName } = toRefs(data);

    watch(
        () => props.currTreeNodeInfo,
        (newVal) => {
            currInfo.value = $deepAssignObject(currInfo.value, newVal);
            getButtonList();
        },
        { deep: true }
    );

    onMounted(() => {
        getButtonList();
    });

    async function getButtonList() {
        //权限配置
        tableConfig.value.tableData = [];
        let res = await getBpmList(props.currTreeNodeInfo.processDefinitionId, props.currTreeNodeInfo.id);
        if (res.success) {
            let data = res.data.filter((item) => item.taskDefKey != "");
            tableConfig.value.tableData = data;
        }
    }

    function taskBindManage(row) {
        taskDefKey.value = row.taskDefKey;
        taskDefName.value = row.taskDefName;
        Object.assign(dialogConfig.value, {
            show: true,
            width: '50%',
            title: '退回配置【' + row.taskDefName + '】',
            showFooter: false
        });
    }
</script>

<style lang="scss" scoped></style>
