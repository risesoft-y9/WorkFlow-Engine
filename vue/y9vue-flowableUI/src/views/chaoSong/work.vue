<template>
  <el-container>
  </el-container>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import { useRoute,useRouter } from 'vue-router';
import {useFlowableStore} from "@/store/modules/flowableStore";
const router = useRouter();
const currentrRute = useRoute();
const flowableStore = useFlowableStore();
onMounted(() => {
  let processInstanceId = currentrRute.query.processInstanceId?currentrRute.query.processInstanceId: '';
  let itemId = currentrRute.query.itemId?currentrRute.query.itemId: '';
  let id = currentrRute.query.type?currentrRute.query.id: '';
  flowableStore.$patch({
    itemName: '阅件'
  });
  openDoc(id,itemId,processInstanceId);
});
 
  
function openDoc(id,itemId,processInstanceId) {
    let query = {
        itemId: itemId,
        processInstanceId: processInstanceId,
        status: 0,
        id: id,
        listType: 'csTodo',
    };
    let path = "/workIndex/csEdit";
    router.push({path:path, query: query });
}

</script>
<style> 
</style>
