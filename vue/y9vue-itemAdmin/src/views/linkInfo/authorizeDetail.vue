<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-09-08 11:14:05
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-14 16:25:46
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\views\linkInfo\authorizeDetail.vue
-->
<template>
     <y9Table :config="tableConfig" >
        <template #opt="{row,column,index}">
          <el-button class="global-btn-second" size="small" @click="deleteBindData(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
        </template>
    </y9Table>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type {ElNotification, ElMessage,ElLoading,FormInstance } from 'element-plus';
import {findByLinkId} from '@/api/itemAdmin/linkInfo';
import {removeBind} from "@/api/itemAdmin/item/linkInfoConfig";
const props = defineProps({
	row: {
      type: Object,
      default:() => { return {} }
    }
});
const data = reactive({
  tableConfig: {
    columns: [
      { title: "序号", type:'index', width: '60', },
      { title: "事项名称", key: "itemName", width: '200',},
      { title: "绑定角色", key: "roleNames" },
      { title: "操作", width: '110', slot: 'opt' },
    ],
    tableData: [],
    pageConfig: false,
    height:'auto'
  },
})
	
let {
  tableConfig,
} = toRefs(data);

onMounted(()=>{
  getTableList();
});

async function getTableList() {
  let res = await findByLinkId(props.row.id);
  tableConfig.value.tableData = res.data;
}

const deleteBindData = (rows) => {
    ElMessageBox.confirm("您确定要删除数据吗?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        removeBind(rows.id).then(res => {
            if (res.success) {
              ElMessage({ type: "success", message: res.msg ,offset:65});
              getTableList();
            } else {
              ElMessage({message:res.msg,type: 'error',offset:65});
            }
          });
        }).catch(() => {
         ElMessage({
            type: "info",
            message: "已取消删除",
            offset:65
          });
        });
}

</script>

<style lang="scss">
.authorizeDetail{
   margin: 15vh 0 1vh 15.5vw;
 }
.authorizeDetail .el-dialog__body {
   padding-top: 0px;
}

.authorizeDetail .el-form-item--default {
    margin-bottom: 0px;
}
.authorizeDetail .el-form-item {
    margin-bottom: 0px;
}
.authorizeDetail .el-form-item__error {
    color: var(--el-color-danger);
    font-size: 12px;
    line-height: 1;
    padding-top: 2px;
    position: relative;
    top: 0%; 
    left: 0;
}
</style>
