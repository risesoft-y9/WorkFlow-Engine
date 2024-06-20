<template>
  <div class="optionClassTable">
  <el-form ref="optionClassForm" :model="formData" :rules="rules">
    <y9Table :config="tableConfig" :filterConfig="filterConfig">
      <template #addBtn>
        <el-button class="global-btn-main" type="primary" @click="addData"><i class="ri-add-line"></i>新增</el-button>
      </template>
      <template #name="{row,column,index}">
          <el-form-item prop="name" v-if="editIndex === index">
            <el-input  v-model="formData.name" clearable/>
          </el-form-item>
          <span v-else>{{row.name}}</span>
      </template>
      <template #type="{row,column,index}">
          <el-form-item prop="type" v-if="editIndex === index">
            <el-input :disabled="isEdit" v-model="formData.type" clearable/>
          </el-form-item>
          <span v-else>{{row.type}}</span>
      </template>
      <template #opt="{row,column,index}">
          <div v-if="editIndex === index">
            <el-button class="global-btn-second" size="small" @click="saveData(optionClassForm)"><i class="ri-book-mark-line"></i>保存</el-button>
            <el-button class="global-btn-second" size="small" @click="cancalData(optionClassForm)"><i class="ri-close-line"></i>取消</el-button>
          </div>
          <div v-else>
            <el-button class="global-btn-second" size="small" @click="editOptionValue(row)"><i class="ri-book-3-line"></i>字典管理</el-button>
            <el-button class="global-btn-second" size="small" @click="editData(row,index)"><i class="ri-edit-line"></i>修改</el-button>
            <el-button class="global-btn-danger" type="danger" size="small" @click="delData(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
          </div>
      </template>
    </y9Table>
  </el-form>
  </div>
  <y9Dialog v-model:config="dialogConfig">
    <OptionValue ref="optionValueRef" :row="row"/>
  </y9Dialog>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type { ElMessage,ElLoading } from 'element-plus';
import {getOptionClassList,saveOptionClass,delOptionClass} from '@/api/itemAdmin/optionClass';
import OptionValue from '@/views/optionClass/optionValue.vue';
const optionClassForm = ref<FormInstance>();
const rules = reactive<FormRules>({
        name:{ required: true,message: '请输入字典名称', trigger: 'blur' },
        type:{ required: true,message: '请输入字典标识', trigger: 'blur' },
      });
const data = reactive({
  editIndex:'',
  formData:{id:'',type:'',name:''},
  tableConfig: {
      //人员列表表格配置
      columns: [
        { title: '序号', type: 'index', width: '60' },
        { title: '字典名称', key: 'name', slot: 'name' },
        { title: '字典标识', key: 'type',slot:'type' },
        { title: '操作', slot: 'opt', width: '280' },
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
    },
     //弹窗配置
		dialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
        })
			},
			visibleChange:(visible) => {
			}
		},
    row:'',
    isEmptyData:false,
    isEdit:false,
});

let {
  editIndex,
  tableConfig,
  filterConfig,
  dialogConfig,
  row,
  isEmptyData,
  isEdit,
  formData,
} = toRefs(data);

async function getList() {
  let res = await getOptionClassList();
  for(let item of res.data){
    item.id = item.type;
  }
  tableConfig.value.tableData = res.data;
}

getList();

const addData = () => {
  for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].type==''){
        isEmptyData.value = true;
    }
  }
  if(!isEmptyData.value){
    editIndex.value = tableConfig.value.tableData.length;
    tableConfig.value.tableData.push({id:'',name:'',type:''});
    formData.value.name = '';
    formData.value.type = '';
    isEdit.value = false;
  }
}

const editData = (rows,index) => {
  editIndex.value = index;
  formData.value.name = rows.name;
  formData.value.type = rows.type;
  isEdit.value = true;
  for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].type==''){
      tableConfig.value.tableData.splice(i,1);
    }
  }
  isEmptyData.value = false;
}

const editOptionValue = (rows) => {
  row.value = rows;
  Object.assign(dialogConfig.value,{
    show:true,
    width:'50%',
    title:"字典管理【"+rows.name+"】",
    showFooter:false
  });
}

const saveData = (refFrom) => {
  if(!refFrom) return;
  refFrom.validate(valid => {
    if (valid) {
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        saveOptionClass(formData.value).then(res => {
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
    formData.value.type = '';
    refForm.resetFields();
    for (let i = 0; i < tableConfig.value.tableData.length; i++) {
      if(tableConfig.value.tableData[i].type==''){
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
         delOptionClass(rows.type).then(res => {
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
.optionClassTable .el-form-item--default {
    margin-bottom: 0px;
}
.optionClassTable .el-form-item {
    margin-bottom: 0px;
}
.optionClassTable .el-form-item__error {
    color: var(--el-color-danger);
    font-size: 12px;
    line-height: 1;
    padding-top: 2px;
    position: relative;
    top: 0%; 
    left: 0;
}
</style>
