<template>
    <el-container></el-container>
</template>
<script lang="ts" setup>
    import { onMounted } from 'vue';
    import { useFlowableStore } from '@/store/modules/flowableStore';
    import { useRoute, useRouter } from 'vue-router';

    const router = useRouter();
    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    if (currentrRute.path == '/workIndex/work') {
        flowableStore.$patch({
            itemId: currentrRute.query?.itemId ? currentrRute.query?.itemId : ''
        });
        if (flowableStore.itemId == '') {
            flowableStore.itemId = flowableStore.itemList[0] != undefined ? flowableStore.itemList[0].url : '';
        }
    }
    onMounted(() => {
        openTodo();
    });

    function openTodo() {
        let itemId = flowableStore.getItemId;
        let path = '/workIndex/todo';
        router.push({ path: path, query: { itemId: itemId } });
    }
</script>
<style></style>
