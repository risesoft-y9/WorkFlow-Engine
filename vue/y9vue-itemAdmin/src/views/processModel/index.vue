<!--
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-05-05 11:38:27
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-06-13 15:50:34
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-processAdmin\src\views\processModel\index.vue
-->
<template>
  <!-- <div class="content">
      <el-card> -->
         <y9Table :config="modelTableConfig" :filterConfig="filterConfig">
            <template #processModel>
				<el-button @click="addModel" class="global-btn-main" type="primary" style="margin-right: 10px;"><i class="ri-add-line" />新增</el-button>
                <el-upload
                    :show-file-list="false"
                    action=""
                    :http-request="uploadSectionFile"
                    name="file"
                    :before-upload="beforeAvatarUpload"
                    style="display: inline-block;"
                >
                    <el-button class="global-btn-second"><i class="ri-database-2-line" />导入</el-button>
                    <font style="font-family: 微软雅黑;font-size: 14px;color: red;margin-left: 5px;">(文件格式.bpmn20.xml)</font>
                </el-upload>
		    </template>
            <template #opt_button="{row,column,index}">
                <el-button size="small" class="global-btn-second" @click="editModel(row)"><i class="ri-edit-line" title="编辑"/>编辑</el-button>
                <el-button size="small" class="global-btn-second" @click="deploy(row)"><i class="ri-database-2-line" title="部署"/>部署</el-button>
                <el-button size="small" class="global-btn-second" @click="exportModel(row)"><i class="ri-download-line" title="导出"/>导出</el-button>
                <el-button size="small" class="global-btn-second" @click="delModel(row)"><i class="ri-delete-bin-line" title="删除"/>删除</el-button>
            </template>
        </y9Table>
      <!-- </el-card> -->
    <y9Dialog v-model:config="dialogConfig">
        <el-form ref="formRef" :model="model" :rules="rules" :inline-message="true" label-width="120px">
                <el-form-item prop="form_name" label="流程名称">
                    <el-input v-model="model.form_name"></el-input>
                </el-form-item>
                <el-form-item prop="form_key" label="流程定义key">
                    <el-input v-model="model.form_key"></el-input>
                </el-form-item>
                <el-form-item prop="form_description" label="概述">
                    <el-input size="small" v-model="model.form_description" type="textarea" :rows="5" resize="none"></el-input>
                </el-form-item>
            </el-form>
    </y9Dialog>
  <!-- </div> -->
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type {FormInstance,ElMessageBox, ElMessage,ElLoading } from 'element-plus';
import y9_storage from "@/utils/storage";
import settings from '@/settings.ts';
import axios from 'axios';
import {getModelList,deleteModel,deployModel,createModel} from '@/api/processAdmin/processModel';
import type { UploadProps } from 'element-plus';
const tableData = ref([]);
const formRef = ref<FormInstance>();
const rules = ref({
    form_name:[{ required: true, trigger: 'blur',message: '请输入流程名称' }],
    form_key:[{ required: true, trigger: 'blur',message: '请输入流程定义key' }]});

const data = reactive({
        model:{},
		modelTableConfig: {//人员列表表格配置
			columns: [
				{ title: "序号", type:'index', width: '60', },
				{ title: "名称", key: "name", },
				{ title: "流程定义key", key: "key", width: '200', },
				{ title: "版本", key: "version", width: '100', },
                { title: "创建时间", key: "createTime", width: '180', },
                { title: "修改时间", key: "lastUpdateTime", width: '180', },
				{ title: "操作", width: '350', slot: 'opt_button' },
			],
            border: false,
            headerBackground: true,
			tableData: [],
			pageConfig:false,//取消分页
		},
		filterConfig:{//过滤配置
			itemList:[
				{
					type:"slot",
					span:24,
					slotName:"processModel",
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
					formRef.value.validate(async valid => {
                        if(valid){
                            const loading = ElLoading.service({lock: true,text: '正在处理中',background:'rgba(0, 0, 0, 0.3)'});
                            createModel(model.value.form_name,model.value.form_key,model.value.form_description).then(res => {
                                ElMessage({type: res.success ? 'success' : 'error',message:res.msg,offset:65});
                                loading.close();
                                if (res.success) {
                                    let y9UserInfo =JSON.parse(sessionStorage.getItem('ssoUserInfo'));
                                    window.open(res.data + "?personId="+y9UserInfo.tenantId+":"+y9UserInfo.personId);//跳转编辑页面
                                    getTableList();
                                }
                            });
                            resolve()
                        }else {
							ElMessage({
								type: 'error',
								message: '验证失败，请重新确认表单信息!',
								offset: 65
							});
							reject()
						}
                    });
				})
			},
			visibleChange:(visible) => {
				
			}
		},
	})
	
	let {
        model,
        filterConfig,
		modelTableConfig,
		dialogConfig,
	} = toRefs(data);

onMounted(()=>{
    getTableList();
});

async function getTableList(){
  getModelList().then((res) => {
    if(res.success){
      modelTableConfig.value.tableData = res.data;
    }
  });
}

function addModel(){
    model.value = {};
    Object.assign(dialogConfig.value,{
			show:true,
			width:'40%',
			title:'创建流程模型',
            okText:'提交',
            cancelText:'关闭',
			showFooter:true
		});
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (rawFile.name.indexOf('.xml') == -1) {
    ElMessage({ type: 'error', message: '请上传指定格式的文件!', offset: 65 });
    return false;
  }
  return true;
}

function uploadSectionFile(params) {
    let formData = new FormData();
    formData.append("file", params.file);
    let config = {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    };
    const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
    let url = import.meta.env.VUE_APP_PROCESS_CONTEXT + "vue/processModel/import?access_token=" + y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
    axios.post(url,formData,config).then((res)=>{
        loading.close();
        ElMessage({ type: res.data.success ? 'success' : 'error', message: res.data.msg, offset: 65 });
        if(res.data.success){
            getTableList();
        }
    }).catch((err)=>{
        loading.close();
        ElMessage({ type: 'error', message: "发生异常", offset: 65 });
    });
}

function deploy(row){//部署
    ElMessageBox.confirm('确定部署【' + row.name + '】?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        deployModel(row.id).then((res) => {
            ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65 });
            loading.close();
            if (res.success) {
                getTableList();
            }
        });
    }).catch(() => {
        ElMessage({ type: 'info', message: '已取消删除', offset: 65 });
    });
}

function delModel(row) {
    ElMessageBox.confirm('是否删除流程设计【' + row.name + '】?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(() => {
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        deleteModel(row.id).then((res) => {
            ElMessage({ type: res.success ? 'success' : 'error', message: res.msg, offset: 65 });
            loading.close();
            if (res.success) {
                getTableList();
            }
        });
    }).catch(() => {
        ElMessage({ type: 'info', message: '已取消删除', offset: 65 });
    });
}

function editModel(row){//编辑
    let y9UserInfo =JSON.parse(sessionStorage.getItem('ssoUserInfo'));
    window.open(import.meta.env.VUE_APP_PROCESS_CONTEXT + "modeler.html?personId="+y9UserInfo.tenantId+":"+y9UserInfo.personId+"#/editor/" + row.id); 
}

function exportModel(row){//导出
    window.open(import.meta.env.VUE_APP_PROCESS_CONTEXT + 'vue/processModel/exportModel?modelId=' + row.id + "&access_token=" + y9_storage.getObjectItem(settings.siteTokenKey,'access_token')); 
}

</script>

<style lang="scss">
@import "@/theme/global.scss";
.createModel .el-form-item--default .el-form-item__label {
  width: 100px;
}
</style>
