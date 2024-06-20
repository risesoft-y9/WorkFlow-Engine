<template>
   <y9Table :config="tableConfig">
      <template #opt="{row,column,index}">
        <el-button class="global-btn-danger" type="danger" size="small" @click="deleteBindData(row)"><i class="ri-delete-bin-line" link></i>删除</el-button>
      </template>
  </y9Table>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type {ElNotification, ElMessage,ElLoading,FormInstance } from 'element-plus';
import {getBindListByButtonId,deleteBind} from '@/api/itemAdmin/commonButton';
const props = defineProps({
    row: {
      type: Object,
      default:() => { return {} }
    }
  })

const data = reactive({
		tableConfig: {
			columns: [
				{ title: "序号", type:'index', width: '60', },
				{ title: "事项名称", key: "itemName",width: '150', },
				{ title: "流程定义", key: "processDefinitionId", },
				{ title: "任务节点", key: "taskDefKey", width: '300', },
				{ title: "绑定角色", key: "roleNames", width: '200', },
				{ title: "操作", slot: 'opt', width: '120', },
			],
			tableData: [],
			pageConfig:false,//取消分页
      loading:false,
      height:'auto'
		},
	})
	
	let {
    tableConfig,
	} = toRefs(data);
	
  onMounted(()=>{
    getList();
	});


async function getList() {
  tableConfig.value.loading = true;
  let res = await getBindListByButtonId(props.row.id);
  tableConfig.value.loading = false;
  tableConfig.value.tableData = res.data;
}

const deleteBindData = (rows) => {
    ElMessageBox.confirm("您确定要删除数据吗?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
          deleteBind(rows.id).then(res => {
            if (res.success) {
              ElMessage({ type: "success", message: res.msg ,offset:65});
              getList();
            } else {
              ElMessage({message:res.msg,type: 'error',offset:65});
            }
          });
        })
        .catch(() => {
         ElMessage({
            type: "info",
            message: "已取消删除",
            offset:65
          });
        });
}

</script>

<style lang="scss">
.bindDetail{
   margin: 15vh 0 1vh 15.5vw;
 }
.bindDetail .el-dialog__body {
   padding-top: 0px;
}

.bindDetail .el-form-item--default {
    margin-bottom: 0px;
}
.bindDetail .el-form-item {
    margin-bottom: 0px;
}
.bindDetail .el-form-item__error {
    color: var(--el-color-danger);
    font-size: 12px;
    line-height: 1;
    padding-top: 2px;
    position: relative;
    top: 0%; 
    left: 0;
}
</style>
