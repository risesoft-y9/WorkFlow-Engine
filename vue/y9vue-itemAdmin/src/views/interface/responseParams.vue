<template>
      <el-form class="paramsForm" ref="paramsRef" :model="formData" :rules="rules">
        <y9Table :config="tableConfig" :filterConfig="filterConfig" @select-all="handlerSelectData" @select="handlerSelectData">
            <template #addBtn>
              <el-button class="global-btn-main" type="primary" @click="addParams"><i class="ri-add-line"></i>新增</el-button>
            </template>
              <template #parameterName="{row,column,index}">
                  <el-form-item prop="parameterName" v-if="editIndex === index">
                    <el-input v-model="formData.parameterName" clearable/>
                  </el-form-item>
                  <span v-else>{{row.parameterName}}</span>
              </template>
              <template #remark="{row,column,index}">
                  <el-form-item v-if="editIndex === index">
                    <el-input v-model="formData.remark" clearable/>
                  </el-form-item>
                  <span v-else>{{row.remark}}</span>
              </template>
              <template #opt_button="{row,column,index}">
                <div v-if="editIndex === index">
                  <el-button class="global-btn-second" size="small" @click="saveData(paramsRef)"><i class="ri-book-mark-line"></i>保存</el-button>
                  <el-button class="global-btn-second" size="small" @click="cancalData(paramsRef)"><i class="ri-close-line"></i>取消</el-button>
              </div>
              <div v-else>
                <el-button class="global-btn-second" size="small" @click="editParams(row,index)"><i class="ri-edit-line"></i>修改</el-button>
                <el-button class="global-btn-danger" type="danger" size="small" @click="delParams(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
              </div>
              </template>
          </y9Table>  
      </el-form>
</template>
<script lang="ts" setup>
import axios from 'axios';
import qs from "qs";
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type { ElMessage,ElLoading } from 'element-plus';
import {findResponseParamsList,saveResponseParams,removeResponseParams} from '@/api/itemAdmin/interface';
const props = defineProps({
	row: {
      type: Object,
      default:() => { return {} }
    }
});
const paramsRef = ref<FormInstance>();
const rules = reactive<FormRules>({
  parameterName:{ required: true,message: '请输入参数名称', trigger: 'blur' },
});
const data = reactive({
      isEmptyData:false,
      editIndex:'',
      formData:{id:'',parameterName:''},
      isEdit:false,
		  tableConfig: {
        columns: [
          // { title: '', type: 'selection', fixed: 'left',width: '60' },
          { title: "序号", type:'index', width: '60', },
          { title: "参数名称", key: "parameterName", slot: 'parameterName'},
          { title: "参数备注", key: "remark", width: '200',slot: 'remark'},
          { title: "添加时间", key: "createTime", width: '180', },
          { title: "操作", width: '180', slot: 'opt_button' },
        ],
        border: false,
        headerBackground: true,
        tableData: [],
        pageConfig: false,
		},
		filterConfig:{//过滤配置
			itemList:[
				{
					type:"slot",
					span:12,
					slotName:"addBtn",
				},
			],
		},
    idsArr:[],
      //弹窗配置
		dialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
          return requestTest(resolve, reject);
        })
			},
      onReset: (newConfig) => {
				return new Promise(async (resolve, reject) => {
          reject()
        })
			},
			visibleChange:(visible) => {
			}
		},
    editParamsRef:'',
	})

  let {
    editIndex,
    isEmptyData,
    formData,
    isEdit,
    filterConfig,
    tableConfig,
    idsArr,
    dialogConfig,
    editParamsRef,
  } = toRefs(data);

async function getTableList() {
    let res = await findResponseParamsList('',props.row.id);
    tableConfig.value.tableData = res.data;
}
getTableList();

const addParams = () => {
  for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].id==''){
        isEmptyData.value = true;
    }
  }
  if(!isEmptyData.value){
    editIndex.value = 0;
    tableConfig.value.tableData.unshift({id:'',parameterName:'',remark:'',interfaceId:props.row.id});
    formData.value.id = '';
    formData.value.parameterName = '';
    formData.value.remark = '';
    formData.value.interfaceId = props.row.id;
    isEdit.value = false;
  }
}

const editParams = (rows,index) => {
  editIndex.value = index;
  formData.value.id = rows.id;
  formData.value.parameterName = rows.parameterName;
  formData.value.remark = rows.remark;
  formData.value.interfaceId = rows.interfaceId;
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
        saveResponseParams(formData.value).then(res => {
          loading.close();
            if (res.success) {
              ElMessage({ type: "success", message: res.msg ,offset:65});
              editIndex.value = '';
              isEmptyData.value = false;
              getTableList();
            } else {
              ElMessage({message:res.msg,type: 'error',offset:65});
            }
      });
    }
  });
}

const cancalData = (refForm) => {
    editIndex.value = '';
    formData.value.parameterName = '';
    formData.value.remark = '';
    refForm.resetFields();
    for (let i = 0; i < tableConfig.value.tableData.length; i++) {
      if(tableConfig.value.tableData[i].id == ''){
        tableConfig.value.tableData.splice(i,1);
      }
    }
    isEmptyData.value = false;
}

function handlerSelectData(row, data){
	idsArr.value = row;
}

const delParams = (rows) => {
  // if(idsArr.value.length == 0){
	// 	ElNotification({title: '操作提示',message: '请勾选要删除的数据',type: 'error',duration: 2000,offset: 80});
	// 	return;
	// }
  ElMessageBox.confirm("您确定要删除参数吗?","提示",{
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
    ).then(() => {
      let ids = [];
      ids.push(rows.id);
      // for(let obj of idsArr.value){
      //   ids.push(obj.id);
      // }
      removeResponseParams(ids.toString()).then(res => {
          if (res.success) {
            ElMessage({ type: "success", message: res.msg ,offset:65});
            editIndex.value = '';
            getTableList();
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
.paramsForm .el-form-item{
  margin-bottom: 0px !important;
}
</style>
