<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-06-14 10:06:24
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-03-27 09:03:06
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\views\y9form\form\formMaking.vue
-->
<template>
  <fm-making-form 
      ref="makingform" 
      style="height:100%;" 
      preview 
      generate-code 
      generate-json
      clearable
      upload
      :custom-fields="customFields"
      :jsonTemplates="jsonTemplates"
      v-loading="loading"
      element-loading-text="正在保存中"
      element-loading-spinner="el-icon-loading"
      element-loading-background="rgba(0, 0, 0, 0.8)"
      @ready="handleFormReady"
    >
    <template #action>
        <el-button type="text" @click="saveForm">
          <i class="fm-iconfont icon-check-box" style="font-size: 16px; font-weight: 600;" />
          保存
        </el-button>
        <el-button type="text" @click="formFieldList">
          <i class="fm-iconfont icon-slider" style="font-size: 16px; font-weight: 600;" />
          表单字段
        </el-button>
    </template>
  </fm-making-form>
  <y9Dialog v-model:config="dialogConfig">
    <y9Table :config="tableConfig" @on-curr-page-change="onCurrPageChange" @on-page-size-change="onPageSizeChange">
      <template #opt_button="{row,column,index}">
        <el-button class="global-btn-danger" type="danger" size="small" @click="delField(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
      </template>
    </y9Table>
  </y9Dialog>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type {ElMessageBox, ElMessage,ElLoading } from 'element-plus';
import y9_storage from "@/utils/storage";
import settings from '@/settings.ts';
import {saveFormJson,getForm,saveFormField,getFormBindFieldList,deleteFormFieldBind} from "@/api/itemAdmin/y9form.ts";
import json0 from '@/components/formMaking/demo/json0.js';
import json1 from '@/components/formMaking/demo/json1.js';
import json2 from '@/components/formMaking/demo/json2.js';
import json3 from '@/components/formMaking/demo/json3.js';
import json8 from '@/components/formMaking/demo/json8.js';
const props = defineProps({
  formInfo: {//当前tree节点信息
    type: Object,
    default:() => { return {} }
  },
})
const data = reactive({
		makingform:'',
    loading:false,
    customFields:[
        {
          name: '意见框',
          el: 'custom-opinion',
          options: {
            defaultValue: {},
            customClass: '',
            labelWidth: 0,
            isLabelWidth: false,
            hidden: false,
            dataBind: true,
            required: false,
            minHeight:'',//最小高度
            pattern: ''
          }
        },
        {
          name: '附件列表',
          el: 'custom-file',
          model:'custom_file',
          options: {
            defaultValue: {},
            customClass: '',
            labelWidth: 0,
            isLabelWidth: false,
            hidden: false,
            dataBind: true,
            required: false,
            pattern: ''
          }
        },
        {
          name: '正文组件',
          el: 'custom-word',
          model:'custom_word',
          options: {
            defaultValue: {},
            customClass: '',
            labelWidth: 0,
            isLabelWidth: false,
            hidden: false,
            dataBind: true,
            required: false,
            pattern: ''
          }
        },
        {
          name: '人员树',
          el: 'custom-personTree',
          model:'custom_personTree1',
          options: {
            defaultValue: {},
            customClass: '',
            labelWidth: 0,
            isLabelWidth: false,
            hidden: false,
            dataBind: true,
            required: false,
            tableField:'',//关联数据库字段
            pattern: ''
          }
        },
        {
          name: '编号按钮',
          el: 'custom-numberButton',
          options: {
            defaultValue: {},
            customClass: '',
            labelWidth: 0,
            isLabelWidth: false,
            hidden: false,
            dataBind: true,
            required: false,
            tableField:'',//关联数据库字段
            pattern: ''
          }
        },
        {
          name: '图片显示',
          el: 'custom-picture',
          model:'custom_picture',
          options: {
            defaultValue: {},
            customClass: '',
            labelWidth: 0,
            isLabelWidth: false,
            hidden: false,
            dataBind: true,
            required: false,
            tableField:'',//关联数据库字段
            pattern: ''
          }
        }
      ],
      jsonTemplates:[],//表单模板
      //弹窗配置
		  dialogConfig: {
        show: false,
        title: "",
        loading:false,
        onOkLoading: true,
        onOk: (newConfig) => {
          return new Promise(async (resolve, reject) => {
            
          })
        },
        visibleChange:(visible) => {
          // console.log('visible',visible)
        }
      },
      tableConfig: {
        columns: [
          { title: "序号", type:'index', width: '60', },
          { title: "表名称", key: "tableName", width: 'auto'},
          { title: "字段名称", key: "fieldName",width: 'auto' },
          { title: "字段中文名称", key: "fieldCnName", width: 'auto'},
          { title: "字段类型", key: "fieldType", width: 'auto', },
          { title: "操作", width: '80', slot: 'opt_button' },
        ],
        border: false,
        tableData: [],
        pageConfig: {
          // 分页配置，false隐藏分页
          currentPage: 1, //当前页数，支持 v-model 双向绑定
          pageSize: 15, //每页显示条目个数，支持 v-model 双向绑定
          total: 0, //总条目数
        },
		  },
})

let {
		makingform,
    customFields,
    loading,
    jsonTemplates,
    dialogConfig,
    tableConfig,
	} = toRefs(data);

// initForm();

async function handleFormReady(){
  jsonTemplates.value = [
      {
        title: '空白表单',
        json: json0,
        url: '/itemAdmin/images/json00.png'
      },
      {
        title: '办件单',
        json: json1,
        url: '/itemAdmin/images/json1.jpg'
      },
      {
        title: '报销单',
        json: json2,
        url: '/itemAdmin/images/json2.jpg'
      },
      {
        title: '复杂表格 - 人员履历表',
        json: json3,
        url: '/itemAdmin/images/json3.png'
      },
      {
        title: '复杂表格 - 动态增减表单项',
        json: json8,
        url: '/itemAdmin/images/json8.png'
      },
    ]

  let res = await getForm(props.formInfo.id);
  if(res.success){
    let y9form = res.data.y9Form;
    let formFieldJson = res.data.formField;
    makingform.value.clear();
    if(y9form.formJson != null && y9form.formJson != ""){
      makingform.value.setJSON(y9form.formJson);
    }
    let formField = JSON.parse(formFieldJson); 
    makingform.value.setDataInfo(formField,y9form.systemName,props.formInfo.id);
  }
}
async function saveForm(){
    let json = makingform.value.getJSON();//表单json数据
    let fieldBind = makingform.value.getFieldBind();//表单字段绑定数据
    let formJson = JSON.stringify(json).toString();
    let fieldBindJson = JSON.stringify(fieldBind).toString();
    loading.value = true;
    let res = await saveFormJson(props.formInfo.id,formJson);
    loading.value = false;
    ElNotification({
      title: res.success ? '成功' : '失败',
      message: res.msg,
      type: res.success ? 'success' : 'error',
      duration: 2000,
      offset: 80
    });
    if(res.success){
      let res1 = await saveFormField(props.formInfo.id,fieldBindJson);
      if(!res1.success){
          ElNotification({
          title: res1.success ? '成功' : '失败',
          message: res1.msg,
          type: res1.success ? 'success' : 'error',
          duration: 2000,
          offset: 80
        });
      }
    }
  }

  	//当前页改变时触发
  function onCurrPageChange(currPage) {
      tableConfig.value.pageConfig.currentPage = currPage;
      reloadTable();
  }
  //每页条数改变时触发
  function onPageSizeChange(pageSize) {
      tableConfig.value.pageConfig.pageSize = pageSize;
      reloadTable();
  }

  async function formFieldList(){
    Object.assign(dialogConfig.value,{
      show:true,
      width:'65%',
      title:'字段绑定详情',
      showFooter:false
    });
    reloadTable();
  }
  async function reloadTable(){
    let page = tableConfig.value.pageConfig.currentPage;
    let rows = tableConfig.value.pageConfig.pageSize;
    let res = await getFormBindFieldList(props.formInfo.id,page,rows);
    if(res.success){
      tableConfig.value.tableData = res.rows;
      tableConfig.value.pageConfig.total = res.total;
    }
  }
 

  async function delField(row){
    ElMessageBox.confirm("您确定要删除绑定字段吗?","提示",{
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
    ).then(async () => {
        let res = await deleteFormFieldBind(row.id);
        if (res.success) {
          ElMessage({ type: "success", message: res.msg ,offset:65});
          makingform.value.removeFormField(row);
          tableConfig.value.pageConfig.currentPage = 1;
          reloadTable();
        } else {
          ElMessage({message:res.msg,type: 'error',offset:65});
        }
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
.formMaking .el-dialog__body{
  height: calc(100% - 55px);
  padding: 10px;
}
</style>
<style lang="scss" scoped>
:deep(.components-list .form-edit-widget-label){
  border-radius: 4px 4px !important;
  border: none;
  background-color: var(--el-color-primary-light-5);
}
:deep(.el-button) {
   box-shadow: none !important;
 }
:deep(.widget-form-container.pc) {
  top: 0px;
  right: 0px;
  left: 0px;
}
:deep(.center-container .btn-bar) {
  border-bottom: solid 1px var(--fm-border-color);
}
.y9-dialog-overlay .y9-dialog .y9-dialog-body .y9-dialog-header {
   border-bottom: none;
}

:deep(.widget-config-container .el-header) {
  border-bottom: 1px solid var(--fm-border-color);
}
.fm2-container {
  border: none;
}
:deep(.el-tabs__nav-wrap){
  height: 46px !important;
}
:deep(.el-tabs__nav-wrap::after){
  visibility: hidden;
}
:deep(.widget-form-container form){
  border: none !important;
}

.widget-left-panel .container-left-arrow::before {
  content: "";
  border-bottom: 1px;
  border-top: 1px;
  border-right: 1px;
}
</style>