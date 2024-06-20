<template>
<div class="viewTypeTable">
  <el-form ref="viewTypeForm" :model="formData" :rules="rules">
    <y9Table :config="tableConfig" :filterConfig="filterConfig"  @on-curr-page-change="onCurrPageChange"
            @on-page-size-change="onPageSizeChange">
      <template #addBtn>
        <el-button class="global-btn-main" type="primary" @click="addData"><i class="ri-add-line"></i>新增</el-button>
      </template>
      <template #name="{row,column,index}">
        <el-form-item prop="name" v-if="editIndex === index">
            <el-input  v-model="formData.name" clearable/>
        </el-form-item>
        <span v-else>{{row.name}}</span>
      </template>
      <template #mark="{row,column,index}">
        <el-form-item prop="mark" v-if="editIndex === index">
            <el-input :disabled="isEdit" v-model="formData.mark" clearable/>
        </el-form-item>
        <span v-else>{{row.mark}}</span>
      </template>
      <template #opt="{row,column,index}">
        <div v-if="editIndex === index">
          <el-button class="global-btn-second" size="small" @click="saveData(viewTypeForm)"><i class="ri-book-mark-line"></i>保存</el-button>
          <el-button class="global-btn-second" size="small" @click="cancalData(viewTypeForm)"><i class="ri-close-line"></i>取消</el-button>
        </div>
        <div v-else>
          <el-button class="global-btn-second" size="small" @click="editData(row,index)"><i class="ri-edit-line"></i>修改</el-button>
          <el-button class="global-btn-danger" type="danger" size="small" @click="delData(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
        </div>
      </template>
    </y9Table>
  </el-form>
  </div>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type { ElMessage,ElLoading } from 'element-plus';
import {viewTypeList,saveOrUpdate,removeViewType} from '@/api/itemAdmin/viewType';

// const editIndex = ref('');
// const isEdit = ref(false);
// const formData = ref({id:'',name:'',mark:''});
const viewTypeForm = ref<FormInstance>();
const rules = reactive<FormRules>({
        name:{ required: true,message: '请输入名称', trigger: 'blur' },
        mark:{ required: true,message: '请输入唯一标示', trigger: 'blur' },
      });
const data = reactive({
  editIndex:'',
  isEdit:false,
  formData:{id:'',name:'',mark:''},
  tableConfig: {
    columns: [
      { title: "序号", type:'index', width: '60', },
      { title: "名称", key: "name", width: '150',slot:'name'},
      { title: "唯一标示", key: "mark",slot:'mark' },
      { title: "使用的事项", key: "itemNames", width: '150'},
      { title: "操作人", key: "userName", width: '150', },
      { title: "添加时间", key: "createDate", width: '180', },
      { title: "修改时间", key: "modifyDate", width: '180', },
      { title: "操作", width: '280', slot: 'opt' },
    ],
    border: false,
    headerBackground: true,
    tableData: [],
    pageConfig: {
      // 分页配置，false隐藏分页
      currentPage: 1, //当前页数，支持 v-model 双向绑定
      pageSize: 15, //每页显示条目个数，支持 v-model 双向绑定
      total: 0, //总条目数
    },
  },
  filterConfig:{//过滤配置
    itemList:[
      {
        type:"slot",
        span:8,
        slotName:"addBtn",
      },
    ],
  },
  isEmptyData:false,
})

  let {
    filterConfig,
    tableConfig,
    editIndex,
    isEdit,
    formData,
    isEmptyData,
  } = toRefs(data);

async function getList() {
    let page = tableConfig.value.pageConfig.currentPage;
    let rows = tableConfig.value.pageConfig.pageSize;
    let res = await viewTypeList(page, rows);
    tableConfig.value.tableData = res.rows;
    tableConfig.value.pageConfig.total = res.total;
}

getList();

//当前页改变时触发
function onCurrPageChange(currPage) {
    tableConfig.value.pageConfig.currentPage = currPage;
    getList();
}
//每页条数改变时触发
function onPageSizeChange(pageSize) {
    tableConfig.value.pageConfig.pageSize = pageSize;
    getList();
}

const addData = () => {
  for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].id==''){
        isEmptyData.value = true;
    }
  }
  if(!isEmptyData.value){
    editIndex.value = tableConfig.value.tableData.length;
    tableConfig.value.tableData.push({id:'',name:'',mark:''});
    formData.value.id = '';
    formData.value.name = '';
    formData.value.mark = '';
    isEdit.value = false;
  }
  
}

const editData = (rows,index) => {
  editIndex.value = index;
  formData.value.id = rows.id;
  formData.value.name = rows.name;
  formData.value.mark = rows.mark;
  isEdit.value = true;
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
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        saveOrUpdate(formData.value).then(res => {
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
    formData.value.name = '';
    formData.value.mark = '';
    refForm.resetFields();
    for (let i = 0; i < tableConfig.value.tableData.length; i++) {
      if(tableConfig.value.tableData[i].id==''){
        tableConfig.value.tableData.splice(i,1);
      }
    }
    isEmptyData.value = false;
}

const delData = (rows) => {
    ElMessageBox.confirm("您确定要删除数据吗?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
         removeViewType(rows.id).then(res => {
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
.viewTypeTable .el-form-item--default {
    margin-bottom: 0px;
}
.viewTypeTable .el-form-item {
    margin-bottom: 0px;
}
.viewTypeTable .el-form-item__error {
    color: var(--el-color-danger);
    font-size: 12px;
    line-height: 1;
    padding-top: 2px;
    position: relative;
    top: 0%; 
    left: 0;
}
</style>
