<template>
      <el-form class="interfaceForm" ref="interfaceRef" :model="formData" :rules="rules">
        <y9Table :config="tableConfig" :filterConfig="filterConfig">
            <template #addBtn>
              <el-input style="margin-left: 10px;" placeholder="接口名称" v-model="name" clearable/>
              <el-input style="margin-left: 10px;width: 500px;" placeholder="接口地址" v-model="address" clearable/>
              <el-select style="margin-left: 10px;" v-model="type" placeholder="请选择">
                <el-option key="" label="全部" value=""></el-option>
                <el-option key="GET" label="GET" value="GET"></el-option>
                <el-option key="POST" label="POST" value="POST"></el-option>
              </el-select>
              <el-button style="margin-left: 10px;" class="global-btn-main" type="primary" @click="getTableList"><i class="ri-search-line"></i>搜索</el-button>
              <el-button class="global-btn-main" type="primary" @click="addInterface"><i class="ri-add-line"></i>新增</el-button>
            </template>
            <template #interfaceName="{row,column,index}">
                  <el-form-item prop="interfaceName" v-if="editIndex === index">
                      <el-input  v-model="formData.interfaceName" clearable/>
                  </el-form-item>
                  <span v-else>{{row.interfaceName}}</span>
              </template>
              <template #requestType="{row,column,index}">
                  <el-form-item prop="requestType" v-if="editIndex === index">
                    <el-select v-model="formData.requestType">
                      <el-option key="GET" label="GET" value="GET"></el-option>
                      <el-option key="POST" label="POST" value="POST"></el-option>
                    </el-select>
                  </el-form-item>
                  <span v-else>{{row.requestType}}</span>
              </template>
              <template #interfaceAddress="{row,column,index}">
                  <el-form-item prop="interfaceAddress" v-if="editIndex === index">
                    <el-input v-model="formData.interfaceAddress" clearable/>
                  </el-form-item>
                  <span v-else>{{row.interfaceAddress}}</span>
              </template>
              <template #asyn="{row,column,index}">
                  <el-form-item prop="asyn" v-if="editIndex === index">
                    <el-select v-model="formData.asyn">
                      <el-option key="1" label="是" value="1"></el-option>
                      <el-option key="0" label="否" value="0"></el-option>
                    </el-select>
                  </el-form-item>
                  <span v-else>{{row.asyn == '1' ? '是' : '否'}}</span>
              </template>
              <template #abnormalStop="{row,column,index}">
                <el-form-item prop="abnormalStop" v-if="editIndex === index">
                    <el-select v-model="formData.abnormalStop">
                      <el-option key="1" label="是" value="1"></el-option>
                      <el-option key="0" label="否" value="0"></el-option>
                    </el-select>
                  </el-form-item>
                  <span v-else>{{row.abnormalStop == '1' ? '是' : '否'}}</span>
              </template>
              <template #opt_button="{row,column,index}">
                <div v-if="editIndex === index">
                  <el-button class="global-btn-second" size="small" @click="saveData(interfaceRef)"><i class="ri-book-mark-line"></i>保存</el-button>
                  <el-button class="global-btn-second" size="small" @click="cancalData(interfaceRef)"><i class="ri-close-line"></i>取消</el-button>
              </div>
              <div v-else>
                <el-button class="global-btn-second" size="small" @click="bindDetail(row)"><i class="ri-book-3-line"></i>授权详情</el-button>
                <el-button class="global-btn-second" size="small" @click="editInfo(row,index)"><i class="ri-edit-line"></i>修改</el-button>
                <el-button class="global-btn-second" size="small" @click="openParamsList(row)"><i class="ri-git-commit-line"></i>接口参数</el-button>
                <el-button class="global-btn-second" size="small" @click="handleTest(row)"><i class="ri-links-line"></i>请求测试</el-button>
                <el-button class="global-btn-danger" type="danger" size="small" @click="delInfo(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
              </div>
              </template>
          </y9Table>  
      </el-form>
      <y9Dialog v-model:config="dialogConfig">
        <AuthorizeDetail v-if="dialogConfig.type == 'bindDetail'" ref="authorizeDetail" :row="row"/>
        <ParamsList v-if="dialogConfig.type == 'paramsList'" ref="paramsListRef" :row="row"/>
        <EditParams v-if="dialogConfig.type == 'editParams'" ref="editParamsRef" :row="row" :params="paramsList"/>
      </y9Dialog>
</template>
<script lang="ts" setup>
import axios from 'axios';
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type { ElMessage,ElLoading } from 'element-plus';
import {findInterfaceList,saveInterface,removeInterface,findRequestParamsList,saveAllResponseParams} from '@/api/itemAdmin/interface';
import AuthorizeDetail from '@/views/interface/authorizeDetail.vue';
import ParamsList from '@/views/interface/paramsList.vue';
import EditParams from '@/views/interface/editParams.vue';
const interfaceRef = ref<FormInstance>();
const rules = reactive<FormRules>({
  interfaceName:{ required: true,message: '请输入接口名称', trigger: 'blur' },
  interfaceAddress:{ required: true,message: '请输入接口地址', trigger: 'blur' },
});
const data = reactive({
      isEmptyData:false,
      editIndex:'',
      formData:{id:'',interfaceAddress:'',interfaceName:'',requestType:'GET',asyn:'0',abnormalStop:'0'},
      isEdit:false,
		  tableConfig: {
        columns: [
          { title: "序号", type:'index', width: '60', },
          { title: "接口名称", key: "interfaceName", width: '180',slot: 'interfaceName'},
          { title: "请求方式", key: "requestType", width: '110',slot: 'requestType'},
          { title: "接口地址", key: "interfaceAddress",slot: 'interfaceAddress',align:'left'},
          { title: "异步调用", key: "asyn",slot: 'asyn', width: '90'},
          { title: "异常停止", key: "abnormalStop",slot: 'abnormalStop', width: '90'},
          { title: "添加时间", key: "createTime", width: '160', },
          { title: "操作", width: '450', slot: 'opt_button' },
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
     //弹窗配置
		dialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
          if(dialogConfig.value.type == 'editParams'){
            return requestTest(resolve, reject);
          }
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
    row:'',
    name:'',
    address:'',
    type:'',
    paramsList:[],
    editParamsRef:'',
	})

  let {
    editIndex,
    isEmptyData,
    formData,
    isEdit,
    filterConfig,
    tableConfig,
    dialogConfig,
    row,
    name,
    address,
    type,
    paramsList,
    editParamsRef,
  } = toRefs(data);

async function getTableList() {
    let res = await findInterfaceList(name.value,type.value, address.value);
    tableConfig.value.tableData = res.data;
}
getTableList();

const addInterface = () => {
  for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].id==''){
        isEmptyData.value = true;
    }
  }
  if(!isEmptyData.value){
    editIndex.value = 0;
    tableConfig.value.tableData.unshift({id:'',interfaceName:'',interfaceAddress:'',requestType:'GET',asyn:'0',abnormalStop:'0'});
    formData.value.id = '';
    formData.value.interfaceName = '';
    formData.value.interfaceAddress = '';
    formData.value.requestType = 'GET';
    formData.value.asyn = '0';
    formData.value.abnormalStop = '0';
    isEdit.value = false;
  }
}

const editInfo = (rows,index) => {
  editIndex.value = index;
  formData.value.id = rows.id;
  formData.value.interfaceName = rows.interfaceName;
  formData.value.interfaceAddress = rows.interfaceAddress;
  formData.value.requestType = rows.requestType;
  formData.value.asyn = rows.asyn;
  formData.value.abnormalStop = rows.abnormalStop;
  isEdit.value = true;
  for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].id == ''){
      tableConfig.value.tableData.splice(i,1);
    }
  }
  isEmptyData.value = false;
}

const bindDetail = (rows) => {
  row.value = rows;
  Object.assign(dialogConfig.value,{
    show:true,
    width:'30%',
    title:'授权详情',
    showFooter:false,
    type:'bindDetail'
  });
}

const openParamsList = (rows) => {
  row.value = rows;
  Object.assign(dialogConfig.value,{
    show:true,
    width:'55%',
    title:'接口参数',
    showFooter:false,
    type:'paramsList'
  });
}

async function handleTest(rows) {
  row.value = rows;
  let res = await findRequestParamsList('','',rows.id);
  paramsList.value = res.data;
  dialogConfig.value.resetText = false;
  Object.assign(dialogConfig.value,{
    show:true,
    width:'30%',
    title:'参数填写',
    showFooter:true,
    okText: '请求测试',
    type:'editParams'
  });
}

const saveData = (refFrom) => {
  if(!refFrom) return;
  refFrom.validate(valid => {
    if (valid) {
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        saveInterface(formData.value).then(res => {
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
    formData.value.interfaceName = '';
    formData.value.interfaceAddress = '';
    formData.value.requestType = 'GET';
    formData.value.asyn = '0';
    formData.value.abnormalStop = '0';
    refForm.resetFields();
    for (let i = 0; i < tableConfig.value.tableData.length; i++) {
      if(tableConfig.value.tableData[i].id==''){
        tableConfig.value.tableData.splice(i,1);
      }
    }
    isEmptyData.value = false;
}

const delInfo = (rows) => {
    ElMessageBox.confirm("您确定要删除【"+rows.interfaceName+"】接口吗?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
         removeInterface(rows.id).then(res => {
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


function requestTest (resolve, reject) {
    let options = {
      method: row.value.requestType,
      url: row.value.interfaceAddress,
      headers: (() => {
        let headObj = {}
        paramsList.value.forEach(item => {
          if (item.parameterType == 'Headers') {
            headObj[item.parameterName] = item.value
          }
        })
        return headObj
      })()
    }
    options.data = (() => {
      let formData = new FormData();
      paramsList.value.forEach(item => {
          if (item.parameterType == 'Body') {
            formData.append(item.parameterName, item.value);
            options.headers['Content-Type'] = 'application/json';
          }
        })
        return formData;
    })()
    options.params = (() => {
      let paramsObj = {}
      paramsList.value.forEach(item => {
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
      let res = await saveAllResponseParams(row.value.id,JSON.stringify(resData.data))
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
.interfaceForm .el-form-item{
  margin-bottom: 0px !important;
}
</style>
