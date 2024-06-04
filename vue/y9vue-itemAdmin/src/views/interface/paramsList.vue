<template>
    <el-tabs v-model="activeName" style="height:40px" @tab-click="tabclick">
      <el-tab-pane label="请求参数" name="Request"></el-tab-pane>
      <el-tab-pane label="响应参数" name="Response"></el-tab-pane>
    </el-tabs>
      <el-form class="paramsForm" style="margin-top: 10px;" ref="paramsRef" :model="formData" :rules="rules">
        <y9Table :config="tableConfig" class="interfaceParamsTable" :filterConfig="filterConfig" @select-all="handlerSelectData" @select="handlerSelectData">
            <template #addBtn>
              <el-button class="global-btn-main" type="primary" @click="addParams"><i class="ri-add-line"></i>新增</el-button>
              <el-button class="global-btn-main" v-if="activeName == 'Request'" type="primary" @click="handleTest"><i class="ri-links-line"></i>请求测试</el-button>
            </template>
            <template #parameterType="{row,column,index}">
                <el-form-item prop="parameterType" v-if="editIndex === index">
                  <el-select v-model="formData.parameterType">
                    <el-option key="Params" label="Params" value="Params"></el-option>
                    <el-option key="Headers" label="Headers" value="Headers"></el-option>
                    <el-option key="Body" label="Body" value="Body"></el-option>
                  </el-select>
                </el-form-item>
                <span v-else>{{row.parameterType}}</span>
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
                  <el-button class="global-btn-second" size="small" @click="saveData()"><i class="ri-book-mark-line"></i>保存</el-button>
                  <el-button class="global-btn-second" size="small" @click="cancalData()"><i class="ri-close-line"></i>取消</el-button>
              </div>
              <div v-else>
                <el-button class="global-btn-second" size="small" @click="editParams(row,index)"><i class="ri-edit-line"></i>修改</el-button>
                <el-button class="global-btn-danger" type="danger" size="small" @click="delParams(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
              </div>
              </template>
          </y9Table>  
      </el-form>
      <y9Dialog v-model:config="dialogConfig">
        <EditParams ref="editParamsRef" :row="row" :params="tableConfig.tableData"/>
      </y9Dialog>
</template>
<script lang="ts" setup>
import axios from 'axios';
import qs from "qs";
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type { ElMessage,ElLoading } from 'element-plus';
import {findRequestParamsList,saveRequestParams,removeRequestParams,saveAllResponseParams,findResponseParamsList,saveResponseParams,removeResponseParams} from '@/api/itemAdmin/interface';
import EditParams from '@/views/interface/editParams.vue';
const props = defineProps({
	row: {
      type: Object,
      default:() => { return {} }
    }
});
const rules = reactive<FormRules>({
  parameterName:{ required: true,message: '请输入参数名称', trigger: 'blur' },
  parameterType:{ required: true,message: '请输入参数类型', trigger: 'blur'},
});
const data = reactive({
      isEmptyData:false,
      editIndex:'',
      formData:{id:'',parameterName:'',parameterType:''},
      isEdit:false,
		  tableConfig: {
        columns: [
          { title: "序号", type:'index', width: '60', },
          { title: "参数名称", key: "parameterName", slot: 'parameterName'},
          { title: "参数类型", key: "parameterType",width: '180',slot: 'parameterType'},
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
          return saveAllResParams(resolve, reject);
        })
			},
			visibleChange:(visible) => {
			}
		},
    paramsRef:'',
    editParamsRef:'',
    activeName:'Request',
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
    activeName,
    paramsRef,
  } = toRefs(data);


async function getTableList() {
  if(activeName.value == 'Request'){
    let res = await findRequestParamsList('','',props.row.id);
    tableConfig.value.tableData = res.data;
  }else if(activeName.value == 'Response'){
    let res = await findResponseParamsList('',props.row.id);
    tableConfig.value.tableData = res.data;
  }
}

getTableList();

function tabclick(tab,event){//页签切换
	activeName.value = tab.props.name;
  cancalData();
	if(activeName.value == 'Request'){
		tableConfig.value.columns.splice(2,0,{title: "参数类型",key: "parameterType",width: '180',slot: 'parameterType'})
	}else{
		tableConfig.value.columns.splice(2,1);
	}
  getTableList();
}


const addParams = () => {
  for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].id==''){
        isEmptyData.value = true;
    }
  }
  if(!isEmptyData.value){
    editIndex.value = 0;
    tableConfig.value.tableData.unshift({id:'',parameterName:'',parameterType:'',remark:'',interfaceId:props.row.id});
    formData.value.id = '';
    formData.value.parameterName = '';
    formData.value.parameterType = '';
    formData.value.remark = '';
    formData.value.interfaceId = props.row.id;
    isEdit.value = false;
  }
}

const editParams = (rows,index) => {
  editIndex.value = index;
  formData.value.id = rows.id;
  formData.value.parameterName = rows.parameterName;
  formData.value.parameterType = rows.parameterType;
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


const saveData = () => {
  if(!paramsRef) return;
  paramsRef.value.validate(async valid => {
    if (valid) {
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        let res = {success:false,msg:''};
        if(activeName.value == 'Request'){
          res = await saveRequestParams(formData.value);
        }else{
          res = await saveResponseParams(formData.value);
        }
        loading.close();
        if (res.success) {
          ElMessage({ type: "success", message: res.msg ,offset:65});
          editIndex.value = '';
          isEmptyData.value = false;
          getTableList();
        } else {
          ElMessage({message:res.msg,type: 'error',offset:65});
        }
    }
  });
}

const cancalData = () => {
    editIndex.value = '';
    formData.value.parameterName = '';
    formData.value.parameterType = '';
    formData.value.remark = '';
    paramsRef.value.resetFields();
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
  ElMessageBox.confirm("您确定要删除参数吗?","提示",{
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
    ).then(async () => {
        let ids = [];
        ids.push(rows.id);
        let res = {success:false,msg:''};
        if(activeName.value == 'Request'){
          res = await removeRequestParams(ids.toString());
        }else{
          res = await removeResponseParams(ids.toString());
        }
        if (res.success) {
          ElMessage({ type: "success", message: res.msg ,offset:65});
          editIndex.value = '';
          getTableList();
        } else {
          ElMessage({message:res.msg,type: 'error',offset:65});
        }
      })
      .catch(() => {
        ElMessage({
          type: "info",
          message: "已取消删除",
          offset:65
        });
      });
}

function handleTest() {
  dialogConfig.value.resetText = false;
  Object.assign(dialogConfig.value,{
    show:true,
    width:'30%',
    title:'参数填写',
    showFooter:true,
    okText: '请求测试',
  });
}

function requestTest (resolve, reject) {
    let options = {
      method: props.row.requestType,
      url: props.row.interfaceAddress,
      headers: (() => {
        let headObj = {}
        tableConfig.value.tableData.forEach(item => {
          if (item.parameterType == 'Headers') {
            headObj[item.parameterName] = item.value
          }
        })
        return headObj
      })()
    }
    options.data = (() => {
      let formData = new FormData();
        tableConfig.value.tableData.forEach(item => {
          if (item.parameterType == 'Body') {
            formData.append(item.parameterName, item.value);
            options.headers['Content-Type'] = 'application/json';
          }
        })
        return formData;
    })()
    options.params = (() => {
      let paramsObj = {}
      tableConfig.value.tableData.forEach(item => {
        if (item.parameterType == 'Params') {
          paramsObj[item.parameterName] = item.value
        }
      })
      return paramsObj
    })()
    axios(options).then(res => {
      console.log(res);
      if(res.data.success){
        dialogConfig.value.resetText = '生成响应参数';
      }
      editParamsRef.value.resData = JSON.stringify(res.data);
      reject();
    }).catch(err => {
      ElMessageBox.alert(err.message)
      reject();
    })
  }

  async function saveAllResParams(resolve, reject){
    let resData = JSON.parse(editParamsRef.value.resData);
    if(typeof(resData.data) != 'object'){
      ElMessage({ type: "error", message: "响应格式不符合，无法生成响应参数" ,offset:65});
      reject();
    }else{
      dialogConfig.value.loading = true;
      let res = await saveAllResponseParams(props.row.id,JSON.stringify(resData.data))
      if(res.success){
        ElMessage({ type: "success", message: res.msg ,offset:65});
      }else{
        ElMessage({ type: "error", message: res.msg ,offset:65});
      }
      dialogConfig.value.loading = false;
      return reject();
    }
  }
</script>

<style lang="scss">
.paramsForm .el-form-item{
  margin-bottom: 0px !important;
}

.paramsForm .y9-filter .y9-filter-item{
  margin-bottom: 10px !important;
}
</style>
