<template>
  <el-progress v-if="uploadLoading" type="line" :percentage="percentage" :stroke-width="18" class="progress" status="success"
    :text-inside="true" :show-text="true"></el-progress>
    <y9Table :config="tableConfig" :filterConfig="filterConfig">
      <template #upload>
        <el-upload :http-request="saveFile" action="" :show-file-list="false" :limit="1" ref="upload" :before-upload="beforeUpload" :auto-upload="true" >
          <el-button type="primary"><i class="ri-upload-line"></i>上传模板(文件格式doc/docx)</el-button>
        </el-upload>
      </template>
      <template #fileName="{row,column,index}">
            <el-link type="primary" :underline="false" @click="downloadFile(row)">{{row.fileName}}</el-link>
      </template>
      <template #opt="{row,column,index}">
        <el-button class="global-btn-danger" type="danger" size="small" @click="delTemplate(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
      </template>
    </y9Table>
    
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type {ElMessageBox, ElMessage,ElLoading } from 'element-plus';
import type { UploadInstance } from 'element-plus';
import {getPrintTemplateList,deletePrintTemplate} from '@/api/itemAdmin/printTemplate';
import axios from 'axios';
import y9_storage from "@/utils/storage";
import settings from '@/settings.ts';

const uploadLoading = ref(false);
const upload = ref<UploadInstance>();
const data = reactive({
  percentage:0,
  fileTypes:['.doc','.docx'],
  tableConfig: {
        //人员列表表格配置
        columns: [
            { title: '序号', type: 'index', width: '60' },
            { title: '模板名称', key: 'fileName', slot: 'fileName' },
            { title: '文件大小', key: 'fileSize', },
            { title: '上传人', key: 'userName', width: '150' },
            { title: '上传时间', key: 'uploadTime', width: '180' },
            { title: '操作', slot: 'opt', width: '240' },
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
                    slotName: 'upload',
                },
            ],
            filtersValueCallBack: (filters) => {
                //过滤值回调
                currFilters.value = filters;
            },
        },
});

let {
  percentage,
  fileTypes,
  tableConfig,
  filterConfig
} = toRefs(data);

async function getPrintList() {
    let res = await getPrintTemplateList();
    tableConfig.value.tableData = res.data;
}

getPrintList()

const getToken = () => {
	return y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
}

const beforeUpload = (file) => {
	let extName = file.name.substring(file.name.lastIndexOf("."));
	if (fileTypes.value[0].indexOf(extName) == -1 || fileTypes.value[1].indexOf(extName) == -1) {
		ElMessage({ type: "error", message: "请上传指定格式的文件", offset: 65 });
		return false;
	}
}

const multipleSelection = ref([])
const handleSelectionChange = (val) => {
  multipleSelection.value = val;
}

const delTemplate = (rows) => {
    ElMessageBox.confirm("您确定要删除当前模板?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
          deletePrintTemplate(rows.id).then(res => {
            if (res.success) {
              ElMessage({ type: "success", message: res.msg ,offset:65});
              getPrintList();
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

const downloadFile = (row) => {
  window.open(import.meta.env.VUE_APP_CONTEXT + "vue/printTemplate/download?id="+row.id+"&access_token="+getToken()); 
}

const saveFile = (params) =>{
  percentage.value = 0;
	let formData = new FormData();
	formData.append("files", params.file);

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
	axios.post(import.meta.env.VUE_APP_CONTEXT + "vue/printTemplate/uploadTemplate", formData, config).then((res) => {
		loading.close();
		if (res.data.success) {
			uploadLoading.value = false;
		}
        getPrintList();
        upload.value.clearFiles();
		ElMessage({ type: res.data.success ? 'success' : 'error', message: res.data.msg , offset: 65 });
	}).catch((err) => {
		ElMessage({ type: 'error', message: '发生异常', offset: 65 });
	});
}

</script>

<style lang="scss">

</style>
