<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-10-27 11:46:53
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-07-12 11:44:50
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\views\index\work.vue
-->
<template>
    <el-container></el-container>
</template>
<script lang="ts" setup>
    import { onMounted } from 'vue';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useRoute, useRouter } from 'vue-router';
    import { getTaskOrProcessInfo } from '@/api/flowableUI/index';

    const router = useRouter();
    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    if (currentrRute.path == '/index/work') {
        if (flowableStore.itemId == '') {
            flowableStore.$patch({
                itemId: currentrRute.query?.itemId ? currentrRute.query?.itemId : ''
            });
        }
    }
    onMounted(async () => {
        let type = currentrRute.query?.type;
        if (type != undefined) {
            if (type == 'fromTodo') {
                //统一待办跳转过来
                let taskId = currentrRute.query.taskId;
                let res = await getTaskOrProcessInfo(taskId, '', type);
                if (res.data.taskId == '') {
                    //待办不存在,打开待办列表
                    openTodo();
                } else {
                    openDoc('todo', res.data.processSerialNumber, res.data.processInstanceId, taskId);
                }
            } else if (type == 'fromHistory') {
                //关联文件跳转过来
                let processInstanceId = currentrRute.query.processInstanceId;
                let res = await getTaskOrProcessInfo('', processInstanceId, type);
                if (res.data.processInstanceId == '') {
                    //流程不存在,打开待办列表
                    openTodo();
                } else {
                    let itembox = currentrRute.query?.itembox;
                    let taskId = currentrRute.query?.taskId;
                    openDoc(itembox, res.data.processSerialNumber, res.data.processInstanceId, taskId);
                }
            } else if (type == 'fromCplane') {
                let processInstanceId = currentrRute.query.processInstanceId;
                let res = await getTaskOrProcessInfo('', processInstanceId, type);
                if (res.data.taskId == '') {
                    //办结件
                    openDoc('done', res.data.processSerialNumber, res.data.processInstanceId, '');
                } else {
                    openDoc(
                        res.data.isTodo ? 'todo' : 'doing',
                        res.data.processSerialNumber,
                        res.data.processInstanceId,
                        res.data.taskId
                    );
                }
            }
        } else {
            openTodo();
        }
    });

    function openTodo() {
        let itemId = flowableStore.getItemId;
        let path = '/index/todo';
        router.push({ path: path, query: { itemId: itemId } });
    }

    function openDoc(itembox, processSerialNumber, processInstanceId, taskId) {
        let link = currentrRute.matched[0].path;
        let query = {
            itemId: flowableStore.itemId,
            processSerialNumber: processSerialNumber,
            itembox: itembox,
            taskId: taskId,
            processInstanceId: processInstanceId,
            listType: itembox
        };
        router.push({ path: link + '/edit', query: query });
    }
</script>
<style></style>
