<template>
    <y9Table :config="tableConfig" :filterConfig="filterConfig">
      <template #addBtn>
        <el-button class="global-btn-main" type="primary" @click="addDynamicRole"><i class="ri-add-line"></i>新增</el-button>
      </template>
      <template #process="{row,column,index}">
        <font v-if="row.useProcessInstanceId">是</font>
        <font v-if="!row.useProcessInstanceId">否</font>
      </template>
      <template #opt="{row,column,index}">
        <el-button class="global-btn-second" size="small" @click="editDynamicRole(row)"><i class="ri-edit-line" link></i>修改</el-button>
        <el-button class="global-btn-danger" type="danger" size="small" @click="delDynamicRole(row)"><i class="ri-delete-bin-line" link></i>删除</el-button>
      </template>
    </y9Table>
    <y9Dialog v-model:config="dialogConfig">
      <NewOrModify ref="newOrModifyRef" :row="row"/>
    </y9Dialog>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type { ElMessage,ElLoading } from 'element-plus';
import {dynamicRoleList,removeDynamicRole,saveOrUpdate} from '@/api/itemAdmin/dynamicRole';
import NewOrModify from '@/views/dynamicRole/newOrEdit.vue';
const data = reactive({
  tableConfig: {
        //人员列表表格配置
        columns: [
            { title: '序号', type: 'index', width: '60' },
            { title: '角色名称', key: 'name',},
            { title: '类全路径', key: 'classPath', },
            { title: '是否与流程相关', key: 'useProcessInstanceId', width: '150',slot:'process' },
            { title: '描述', key: 'description', width: '180' },
            { title: '操作', slot: 'opt', width: '240' },
        ],
        border: false,
        headerBackground: true,
        tableData: [],
        pageConfig: false, //取消分页
    },
    filterConfig: {
        //过滤配置
        itemList: [
            {
                type: 'slot',
                span: 24,
                slotName: 'addBtn',
            },
        ],
        filtersValueCallBack: (filters) => {
            //过滤值回调
            currFilters.value = filters;
        },
    },
     //弹窗配置
		dialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
              let result = {success:false,msg:''};
              let valid = await newOrModifyRef.value.validForm();
              if(!valid){
                  reject();
                  return;
              }
              let formData = newOrModifyRef.value.dynamicRole;
              result = await saveOrUpdate(formData);
              ElNotification({
                  title: result.success ? '成功' : '失败',
                  message: result.msg,
                  type: result.success ? 'success' : 'error',
                  duration: 2000,
                  offset: 80
              });
              if(result.success){
                getDynamicRoleList();
              }
              resolve();
          })
			},
			visibleChange:(visible) => {
			}
		},
    row:'',
    newOrModifyRef:'',
});

let {
  tableConfig,
  filterConfig,
  dialogConfig,
  row,
  newOrModifyRef,
} = toRefs(data);

async function getDynamicRoleList() {
  let res = await dynamicRoleList();
  tableConfig.value.tableData = res.data;
}

getDynamicRoleList()

const addDynamicRole = () => {
  row.value = {};
  Object.assign(dialogConfig.value,{
    show:true,
    width:'50%',
    title:'添加动态角色',
    showFooter:true
  });
}

const editDynamicRole = (rows) => {
  row.value = rows;
  Object.assign(dialogConfig.value,{
    show:true,
    width:'50%',
    title:'编辑动态角色',
    showFooter:true
  });
}

const delDynamicRole = (rows) => {
    ElMessageBox.confirm("您确定要删除此动态角色吗?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
          removeDynamicRole(rows.id).then(res => {
            if (res.success) {
              ElMessage({ type: "success", message: res.msg ,offset:65});
              getDynamicRoleList();
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

<style scoped lang="scss">
@import "@/theme/global.scss";
</style>
