<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-09-08 11:14:05
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-24 10:12:17
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6.4-xxx\y9-vue\y9vue-itemAdmin\src\views\interface\editParams.vue
-->
<template>
    <el-form ref="paramsForm" label-width="150px" >
      <template v-for="item of params">
        <el-form-item :label="item.parameterName">
          <el-input v-model="item.value" clearable></el-input>
        </el-form-item>
      </template>
      <el-form-item label="返回结果">
        <el-input type="textarea" v-model="resData" :readonly="false" rows="5" resize="none"/>
      </el-form-item>
    </el-form>

</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type {ElNotification, ElMessage,ElLoading,FormInstance } from 'element-plus';
const props = defineProps({
	row: {
    type: Object,
    default:() => { return {} }
  },
  params: {
    type: Array,
    default:() => { return [] }
  }
});
const data = reactive({
  paramsForm:{},
  resData:''
})
	
let {
  paramsForm,
  resData
} = toRefs(data);

defineExpose({
  resData,
})

onMounted(()=>{
});


</script>

<style lang="scss">

</style>
