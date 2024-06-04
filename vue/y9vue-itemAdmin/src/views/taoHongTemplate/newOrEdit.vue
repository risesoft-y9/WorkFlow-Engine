<template>
	<el-dialog 
	:title="dialogTitle" 
	custom-class="addOrEditTaoHong" 
	v-model="dialogVisible" 
	width="600px" 
	:close-on-click-modal="false"
	destroy-on-close>
		<div>
			<el-form ref="taoHongForm" :model="taoHongData" :rules="rules" label-width="80px">
				<el-form-item prop="bureauName" label="委办局">
                  <el-select v-model="taoHongData.bureauName" filterable placeholder="请选择委办局" @change="selectBureau" style="width:100%;">
                      <el-option
                          v-for="item in bureauList"
                          :key="item.id"
                          :label="item.name"
                          :value="item.id"
                        />
                  </el-select>
				</el-form-item>
				<el-form-item prop="templateType" label="类型">
					<el-select v-model="taoHongData.templateType" placeholder="请选择" style="width:100%;">
						<el-option v-for="item in typeList" :key="item.typeName" :label="item.typeName" :value="item.typeName">
						</el-option>
					</el-select>
				</el-form-item>
				<el-form-item label="上传模板">
					<el-upload :http-request="saveFile" action="" :file-list="filesList" limit="1" :before-upload="beforeUpload"
						ref="upload" :auto-upload="false" :on-change="onChange" :on-remove="onRemove">
						<el-button type="primary">选择文件</el-button> <div slot="tip" class="el-upload__tip" style="color:red;">只能上传doc/docx文件</div>
					</el-upload>
                	<el-progress v-if="uploadLoading" type="line" :percentage="percentage" :stroke-width="18" class="progress" status="success"
				            :text-inside="true" :show-text="true"></el-progress>
				</el-form-item>
				</el-form>
			<div style="text-align: right;">
				<span slot="footer" class="dialog-footer">
					<el-button type="primary" @click="unploadFile(taoHongForm)">上传</el-button>
					<el-button @click="dialogVisible = false">取消</el-button>
				</span>
			</div>
		</div>
	</el-dialog>
</template>
<script lang="ts" setup>
import {ref, defineProps, defineExpose, onMounted, reactive, watch, defineEmits} from 'vue';
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus';
import type { UploadInstance } from 'element-plus';
import {bureauTree,getTemplateTypeList,saveOrUpdate,getTemplateInfo} from '@/api/itemAdmin/taoHongTemplate';
import axios from 'axios';
import y9_storage from "@/utils/storage";
import settings from "@/settings.ts";

const dialogVisible = ref(false);
const innerVisible = ref(false);
const dialogTitle = ref('');
const bureauList = ref([]);
const typeList = ref([]);
const taoHongData = ref({templateGuid:'',bureauGuid:'',bureauName:'',templateType:''});
const fileTypes = ref(['.doc','.docx']);
const filesList = ref([]);
const percentage = ref(0);
const upload = ref<UploadInstance>();	
const uploadLoading = ref(false);
const taoHongForm = ref<FormInstance>();
const rules = reactive<FormRules>({
		bureauName:{ required: true, message:'请选择委办局',trigger: 'blur' },
		templateType:{ required: true, message:'请选择模板类型',trigger: 'blur' }
      });

const props = defineProps({
	reloadTable : Function
});

defineExpose({ show });
async function show(id){
	dialogVisible.value = true;
	dialogTitle.value = "添加套红模板";
	taoHongData.value = {templateGuid:'',bureauGuid:'',bureauName:'',templateType:''};
	if(id != ''){
		dialogTitle.value = "编辑套红模板";
		getTemplateInfo(id).then(res => {
			taoHongData.value = res.data.taoHongTemplate;
		});
	}
	filesList.value = [];
	if (upload.value != undefined) {
		upload.value.clearFiles();
	}
	getBureauList();
	getTypeList();
}

async function getBureauList() {
  let res = await bureauTree();
  bureauList.value = res.data;
}

async function getTypeList(){
    getTemplateTypeList().then(res => {
		typeList.value = res.data;
	});
}

const selectBureau = (val) => {
		let obj = bureauList.value.find((element) => {
        return element.id === val;
    })
    taoHongData.value.bureauGuid = val;
    taoHongData.value.bureauName = obj.name;
}

const getToken = () => {
	return y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
}

const beforeUpload = (file) => {
	let extName = file.name.substring(file.name.lastIndexOf("."));
	if (fileTypes.value[0].indexOf(extName) == -1 && fileTypes.value[1].indexOf(extName) == -1) {
		ElMessage({ type: "error", message: "请上传指定格式的文件", offset: 65 });
		return false;
	}
}

const onChange = (file, fileList) => {
	filesList.value = fileList;
	console.log(filesList.value);
}

const onRemove = (file, fileList) => {
	filesList.value = fileList;
}

const saveFile = (params) =>{
    percentage.value = 0;

	let formData = new FormData();
  	formData.append("templateGuid", taoHongData.value.templateGuid);
  	formData.append("bureauGuid", taoHongData.value.bureauGuid);
  	formData.append("bureauName", taoHongData.value.bureauName);
  	formData.append("templateType", taoHongData.value.templateType);
	formData.append("file", params.file);

	let config = {
		onUploadProgress: progressEvent => {
			//progressEvent.loaded:已上传文件大小,progressEvent.total:被上传文件的总大小
			let percent = (progressEvent.loaded / progressEvent.total * 100) | 0;
			percentage.value = percent;
		},
		headers: {
			'Content-Type': 'multipart/form-data',
			'Authorization': 'Bearer ' + getToken()
		}
	};
    uploadLoading.value = true;
	const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
	axios.post(import.meta.env.VUE_APP_CONTEXT + "vue/taoHongTemplate/saveOrUpdate", formData, config).then((res) => {
		loading.close();
		if (res.data.success) {
			uploadLoading.value = false;
			dialogVisible.value = false;
		}
        props.reloadTable();
        upload.value.clearFiles();
		ElMessage({ type: res.data.success ? 'success' : 'error', message: res.data.msg , offset: 65 });
	}).catch((err) => {
		ElMessage({ type: 'error', message: '发生异常', offset: 65 });
	});
}

const unploadFile = (refForm) => {
	if(!refForm) return;
	refForm.validate(valid => {
		if (valid) {
			if (filesList.value.length != 0) {
				upload.value.submit();
			} else {
				ElMessage({ type: 'error', message: '请选择文件上传！', offset: 65 });
			}
		}
	});
}
	

</script>


<style>
.progress {
	width: 200px;
	height: 200px;
	position: absolute;
	top: 50%;
	left: 50%;
	margin-left: -100px;
	margin-top: -100px;
	z-index: 99999;
}
</style>
