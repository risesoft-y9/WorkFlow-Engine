<template>
  <div style="margin-bottom: 8px;">
    <el-button-group>
      <el-button type="primary" @click="addTemplateType"><i class="ri-add-line"></i>新增</el-button>
    </el-button-group>
  </div>
   <y9Table :config="tableConfig">
      <template #typeName="{row,column,index}">
        <el-form class="typeManager" ref="typeManngeForm" :model="formData" :rules="rules">
          <el-form-item prop="typeName" v-if="editIndex === index">
              <el-input v-model="formData.typeName" clearable/>
          </el-form-item>
          <span v-else>{{row.typeName}}</span>
        </el-form>
      </template>
      <template #opt="{row,column,index}">
        <div v-if="editIndex === index">
            <el-button class="global-btn-second" size="small" @click="saveData(typeManngeForm)"><i class="ri-book-mark-line"></i>保存</el-button>
            <el-button class="global-btn-second" size="small" @click="cancalData(typeManngeForm)"><i class="ri-close-line"></i>取消</el-button>
        </div>
        <div v-else>
            <el-button class="global-btn-second" size="small" @click="editTemplateType(row,index)"><i class="ri-edit-line"></i>修改</el-button>
            <el-button class="global-btn-second" size="small" @click="delTemplateType(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
        </div>
      </template>
  </y9Table>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type {ElNotification, ElMessage,ElLoading,FormInstance } from 'element-plus';
import {getTemplateTypeList,getTemplateType,saveTemplateType,removeTemplateType} from '@/api/itemAdmin/taoHongTemplate';
const data = reactive({
  editIndex:'',
  formData:{id:'',typeName:''},
  tableConfig: {
    columns: [
      { title: "序号", type:'index', width: '60', },
      { title: "类型名称", key: "typeName", width: 'auto',slot: 'typeName'},
      { title: "操作", width: '160', slot: 'opt' },
    ],
    tableData: [],
    pageConfig: false,
    height:'auto'
  },
})
	
let {
  tableConfig,
  editIndex,
  formData,
} = toRefs(data);

onMounted(()=>{
  getTypeList();
});

const typeManngeForm = ref<FormInstance>();
const rules = reactive<FormRules>({
  typeName:{ required: true,message: '请输入模板类型', trigger: 'blur' },
});

async function getTypeList() {
  let res = await getTemplateTypeList();
  tableConfig.value.tableData = res.data;
}

const isEmptyData = ref(false);
const addTemplateType = () => {
   for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].id==''){
        isEmptyData.value = true;
    }
  }
  if(!isEmptyData.value){
    editIndex.value = tableConfig.value.tableData.length
    tableConfig.value.tableData.push({id:'',typeName:'',});
    formData.value.id = '';
    formData.value.typeName = '';
  }
}

const editTemplateType = (rows,index) => {
  editIndex.value = index;
  formData.value.id = rows.id;
  formData.value.typeName = rows.typeName;
  for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].id==''){
      tableConfig.value.tableData.splice(i,1);
    }
  }
  isEmptyData.value = false;
}

const saveData = (refFrom) => {
  if(!refFrom) return;
  refFrom.validate(valid => {
    if (valid) {
        saveTemplateType(formData.value).then(res => {
            if (res.success) {
              ElMessage({ type: "success", message: res.msg ,offset:65});
              editIndex.value = '';
              isEmptyData.value = false;
              getTypeList();
            } else {
              ElMessage({message:res.msg,type: 'error',offset:65});
            }
      });
    }
  });
    
}

const cancalData = (refForm) => {
    editIndex.value = '';
    formData.value.typeName = '';
    refForm.resetFields();
    for (let i = 0; i < tableConfig.value.tableData.length; i++) {
      if(tableConfig.value.tableData[i].id==''){
        tableConfig.value.tableData.splice(i,1);
      }
    }
    isEmptyData.value = false;
}

const delTemplateType = (rows) => {
    ElMessageBox.confirm("您确定要删除当前类型吗?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
          removeTemplateType(rows.id).then(res => {
            if (res.success) {
              ElMessage({ type: "success", message: res.msg ,offset:65});
              getTypeList();
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
.typeManager .el-dialog__body {
   padding-top: 0px;
}

.typeManager .el-form-item--default {
    margin-bottom: 0px;
}
.typeManager .el-form-item {
    margin-bottom: 0px;
}
.typeManager .el-form-item__error {
    color: var(--el-color-danger);
    font-size: 12px;
    line-height: 1;
    padding-top: 2px;
    position: relative;
    top: 0%; 
    left: 0;
}
</style>
