<template>
    <el-upload
      class="upload-demo"
      ref="upload"
      :multiple="multiple"
      :http-request="saveFile"
      :file-list="filesList"
      action=""
      style="margin: 20px;"
      :on-change="onChange"
      :on-remove="onRemove"
      :auto-upload="false">
      <template #trigger>
        <el-button :size="fontSizeObj.buttonSize"
        :style="{ fontSize: fontSizeObj.baseFontSize }" type="primary">{{ $t('选取文件') }}</el-button>
      </template>
      <el-button style="margin-left: 10px;" :size="fontSizeObj.buttonSize"
        :style="{ fontSize: fontSizeObj.baseFontSize }" type="success" @click="submitUpload">{{ $t('确定上传') }}</el-button>
    </el-upload>
    
    <div class="loading" v-if="uploadLoading" 
			v-loading="true"
			:element-loading-text="$t('正在上传中..')"
			element-loading-spinner="el-icon-loading"
			element-loading-background="rgba(0, 0, 0, 0.8)">
			<el-progress type="line" :percentage="percentage" :stroke-width="18" class="progress" color="#67c23a" :text-inside="true" :show-text="true"></el-progress>
		</div>
</template>

<script lang="ts" setup>
import {ref, defineProps, defineExpose, onMounted, reactive, watch, defineEmits, inject} from 'vue';
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus';
import type { UploadInstance } from 'element-plus';
import axios from 'axios';
import {saveAttachment} from "@/api/flowableUI/attachment";
import y9_storage from "@/utils/storage";
import settings from "@/settings";
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo'); 
const props = defineProps({
  reloadTable: Function,
  dialogConfig:{
    type: Object,
    default:() => { return {} }
  },
  processSerialNumber:String,
  processInstanceId:String,
  taskId:String,
})

const upload = ref<UploadInstance>();
const data = reactive({
      multiple: true,
      uploadLoading: false,
      filesList: [],
      percentage: 0
});

let {
      multiple,
      uploadLoading,
      filesList,
      percentage
	} = toRefs(data);
  
function submitUpload() {
  if (filesList.value.length != 0) {
		upload.value!.submit();
  } else {
    ElMessage({ type: 'error', message: t('请选择文件上传！'), offset: 65, appendTo: '.upload-demo' });
  }
}
 
const onChange = (file, fileList) => {
	filesList.value = fileList;
}

const onRemove = (file, fileList) => {
	filesList.value = fileList;
}

const getToken = () => {
	return y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
}

function saveFile(params){
  percentage.value = 0;
  let formData = new FormData();
  formData.append("file", params.file);
  formData.append("processSerialNumber", props.processSerialNumber);
  formData.append("processInstanceId", props.processInstanceId);
  formData.append("taskId", props.taskId);
  formData.append("fileSource", "");
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
  axios.post(import.meta.env.VUE_APP_HOST + import.meta.env.VUE_APP_NAME + "/vue/attachment/upload",formData,config).then((res)=>{
    uploadLoading.value = false;
    if(res.data.success){
      props.reloadTable();
      props.dialogConfig.show = false;
    }
    ElMessage({type: res.data.success ? 'success' : 'error', message: res.data.msg, offset: 65, appendTo: '.upload-demo'});
  }).catch((err)=>{
    uploadLoading.value = false;
    ElMessage({ type: "error", message: t("发生异常") , offset: 65, appendTo: '.upload-demo'});
  });
}
</script>

<style>
  .addfile .el-main-table{
    padding: 0px;
  }
  .addfile .el-card__body{
    padding: 0 20px;
  }
  .addfile .el-table__header-wrapper{
    border-top: 1px solid #ebeef5;
  }
  .addfile .el-table-column--selection .cell {
    padding-left: 10px;
    padding-right: 10px;
  }
 .addfile .el-progress-bar__outer{
    background-color: #bbb;
  }
  .loading{
      position: absolute;
      left: 0;
      top: 0;
      right: 0;
      bottom: 0;
      background: black;
      opacity: 0.8;
  }
  .progress{
    width: 200px;
    height: 200px;
    position: absolute;
    top: 50%;
    left: 50%;
    margin-left: -100px;
    margin-top: -50px;
    z-index: 99999;
  }
</style>

<style scoped lang="scss">
.upload-demo {
 /*message */
  :global(.el-message .el-message__content) {
      font-size: v-bind('fontSizeObj.baseFontSize');
  }
}
</style>