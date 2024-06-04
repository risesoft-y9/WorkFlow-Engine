<template>
  <div style="margin-bottom: 8px;">
    <el-button-group>
      <el-button type="primary" @click="addProperty"><i class="ri-add-line"></i>新增</el-button>
    </el-button-group>
  </div>
  <el-form ref="wordManagerForm" :model="formData" :rules="rules">
    <y9Table :config="tableConfig">
        <template #name="{row,column,index}">
            <el-form-item prop="name" v-if="editIndex === index">
                <el-input v-model="formData.name" clearable/>
            </el-form-item>
            <span v-else>{{row.name}}</span>
        </template>
        <template #initNumber="{row,column,index}">
            <el-form-item prop="initNumber" v-if="editIndex === index">
                <el-input v-model="formData.initNumber" clearable/>
            </el-form-item>
            <span v-else>{{row.initNumber}}</span>
        </template>
        <template #opt="{row,column,index}">
          <div v-if="editIndex === index">
              <el-button class="global-btn-second" size="small" @click="saveData(wordManagerForm)"><i class="ri-book-mark-line"></i>保存</el-button>
              <el-button class="global-btn-second" size="small" @click="cancalData(wordManagerForm)"><i class="ri-close-line"></i>取消</el-button>
          </div>
          <div v-else>
              <el-button class="global-btn-second" size="small" @click="editProperty(row,index)"><i class="ri-edit-line"></i>修改</el-button>
              <el-button class="global-btn-second" size="small" @click="delProperty(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
          </div>
        </template>
    </y9Table>
  </el-form>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type {ElNotification, ElMessage,ElLoading,FormInstance } from 'element-plus';
import {organWordApi} from '@/api/itemAdmin/organWord';
const wordManagerForm = ref<FormInstance>();
var checkNumber = (rule, value, callback) => {
  if (!value) {
    return callback(new Error('初始值不能为空'));
  }
  var numReg = /^[0-9]*$/
  var numRe = new RegExp(numReg)
  if (!numRe.test(value)) {
    return callback(new Error('初始值只能输入数字'));
  }
  return callback();
};
const rules = reactive<FormRules>({
  name:{ required: true,message: '请输入数据名称', trigger: 'blur' },
  initNumber:[{validator: checkNumber, trigger: 'blur' }]
});

const props = defineProps({
	row: {
      type: Object,
      default:() => { return {} }
    }
});
const data = reactive({
  editIndex:'',
  formData:{id:'',name:'',initNumber:'',organWordId:''},
  isEmptyData:false,
  tableConfig: {
    columns: [
      { title: "序号", type:'index', width: '60', },
      { title: "机关代字", key: "name", width: 'auto',slot: 'name'},
      { title: "初始值", key: "initNumber",width:"auto" ,slot: 'initNumber'},
      { title: "操作", width: '160', slot: 'opt' },
    ],
    tableData: [],
    pageConfig: false,
    height:'auto'
  },
})
let {
  editIndex,
  formData,
  isEmptyData,
  tableConfig
} = toRefs(data);

onMounted(()=>{
  getList();
});
  
async function getList() {
  let res = await organWordApi.propertyList(props.row.id);
  tableConfig.value.tableData = res.data;
}

const addProperty = () => {
  for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].id==''){
      isEmptyData.value = true;
    }
  }
  if(!isEmptyData.value){
    editIndex.value = tableConfig.value.tableData.length;
    tableConfig.value.tableData.push({id:'',name:'',initNumber:'',organWordId:''});
    formData.value.id = '';
    formData.value.name = '';
    formData.value.initNumber = '';
  }
}

const editProperty = (rows,index) => {
  editIndex.value = index;
  formData.value.id = rows.id;
  formData.value.name = rows.name;
  formData.value.initNumber = rows.initNumber;
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
        formData.value.organWordId = props.row.id;
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        organWordApi.saveProperty(formData.value).then(res => {
          loading.close();
            if (res.success) {
              ElMessage({ type: "success", message: res.msg ,offset:65});
              editIndex.value = '';
              isEmptyData.value = false;
              getList();
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

const delProperty = (rows) => {
    ElMessageBox.confirm("您确定要删除数据吗?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
          organWordApi.removeProperty(rows.id).then(res => {
            if (res.success) {
              ElMessage({ type: "success", message: res.msg ,offset:65});
              getList();
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
.wordManager .el-dialog__body {
   padding-top: 0px;
}

.wordManager .el-form-item--default {
    margin-bottom: 0px;
}
.wordManager .el-form-item {
    margin-bottom: 0px;
}

</style>
