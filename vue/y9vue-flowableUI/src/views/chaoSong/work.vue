<template>
    <el-container></el-container>
</template>
<script lang="ts" setup>
    import { onMounted } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import { useFlowableStore } from '@/store/modules/flowableStore';

    const router = useRouter();
    const currentrRute = useRoute();
    const flowableStore = useFlowableStore();
    onMounted(() => {
        let processInstanceId = currentrRute.query.processInstanceId ? currentrRute.query.processInstanceId : '';
        let itemId = currentrRute.query.itemId ? currentrRute.query.itemId : '';
        let id = currentrRute.query.type ? currentrRute.query.id : '';
        let itembox = currentrRute.query.itembox ? currentrRute.query.itembox : 'todoChaoSong';
        flowableStore.$patch({
            itemName: '阅件'
        });
        openDoc(id, itemId, processInstanceId, itembox);
    });

    function openDoc(id, itemId, processInstanceId, itembox) {
        let query = {
            itemId: itemId,
            processInstanceId: processInstanceId,
            status: 0,
            id: id,
            listType: 'csTodo',
            itembox: itembox
        };
        let path = '/workIndex/csEdit';
        router.push({ path: path, query: query });
    }
</script>
<style></style>
