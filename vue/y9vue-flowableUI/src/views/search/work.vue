<template>
  <el-container>
  </el-container>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import { useRoute,useRouter } from 'vue-router';
const router = useRouter();
const currentrRute = useRoute();
onMounted(() => {
  let type = currentrRute.query.type?currentrRute.query.type: '';
  openSearch(type);
});
 
  
function openSearch(type){
  let path = "/workIndex/searchList";
  router.push({path:path,query:{type:type}});
}

</script>
<style> 
</style>
