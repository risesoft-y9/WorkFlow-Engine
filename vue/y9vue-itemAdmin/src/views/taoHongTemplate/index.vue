<template>
			<el-progress v-if="uploadLoading" type="line" :percentage="percentage" :stroke-width="18" class="progress" status="success"
				:text-inside="true" :show-text="true"></el-progress>
        <y9Table :config="tableConfig" :filterConfig="filterConfig">
          <template #btnHeader>
              <el-button class="global-btn-main" type="primary" @click="addTemplate"><i class="ri-add-line"></i>新增</el-button>
              <el-button class="global-btn-second" @click="typeManage"><i class="ri-settings-5-line"></i>类型管理</el-button>
          </template>
          <template #searchHeader>
            <div style="display: inline-block;text-align: center;">
              <el-input v-model="bureauName" @keyup.enter="search" placeholder="输入委办局名称查询" style="width:300px;margin-right: 10px;" clearable></el-input>
              <el-button type="primary" @click="search"><i class="ri-search-line"></i>搜索</el-button>
            </div>
          </template>
          <template #templateName="{ row, column, index }">
            <el-link type="primary" :underline="false" @click="downloadFile(row)">{{row.template_fileName}}</el-link>
          </template>
          <template #opt="{ row, column, index }">
            <el-button class="global-btn-second" size="small" @click="editTemplate(row)"><i class="ri-edit-line"></i>修改</el-button>
            <el-button class="global-btn-danger" type="danger" size="small" @click="delTemplate(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
          </template>
        </y9Table>
      <NewOrModify ref="newOrModify" :reloadTable="getTaoHongTemplateList"/>
      <y9Dialog v-model:config="dialogConfig">
        <TypeManage ref="typeManageRef" />
      </y9Dialog>
      
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type {ElNotification, ElMessage,ElLoading,FormInstance,UploadInstance } from 'element-plus';
import {getTemplateList,removeTaoHongTemplate} from '@/api/itemAdmin/taoHongTemplate';
import NewOrModify from '@/views/taoHongTemplate/newOrEdit.vue';
import TypeManage from '@/views/taoHongTemplate/typeManage.vue';
import y9_storage from "@/utils/storage";
import settings from "@/settings.ts";

const data = reactive({
  bureauName:'',
  tableConfig: {
        //人员列表表格配置
        columns: [
            { title: '序号', type: 'index', width: '60' },
            { title: '模板名称', key: 'template_fileName', slot: 'templateName' },
            { title: '委办局名称', key: 'bureauName', },
            { title: '模板类型', key: 'templateType', width: '150' },
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
                span: 18,
                slotName: 'btnHeader',
            },
          {
              type: 'slot',
              slotName: 'searchHeader',
              span: 6,
              labelAlign:"center",
              justify:"flex"
          },
        ],
        filtersValueCallBack: (filters) => {
            //过滤值回调
            currFilters.value = filters;
        },
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
});

let {
  bureauName,
  tableConfig,
  filterConfig,
  dialogConfig
} = toRefs(data);

async function getTaoHongTemplateList(name) {
  let res = await getTemplateList(name);
  tableConfig.value.tableData = res.data;
}

getTaoHongTemplateList('');

const newOrModify = ref();
const addTemplate = () => {
  newOrModify.value.show('');
}

const editTemplate = (rows) => {
  newOrModify.value.show(rows.templateGuid);
}

const search = () => {
  getTaoHongTemplateList(bureauName.value);
}

const typeManage = () => {
  Object.assign(dialogConfig.value,{
    show:true,
    width:'30%',
    title:"类型管理",
    showFooter:false
  });
}

const getToken = () => {
	return y9_storage.getObjectItem(settings.siteTokenKey, 'access_token');
}

const downloadFile = (row) => {
  window.open(import.meta.env.VUE_APP_CONTEXT + "vue/taoHongTemplate/download?template_guid="+row.templateGuid+"&access_token="+getToken()); 
}

const delTemplate = (rows) => {
    ElMessageBox.confirm("您确定要删除当前模板吗?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
          removeTaoHongTemplate(rows.templateGuid).then(res => {
            if (res.success) {
              ElMessage({ type: "success", message: res.msg ,offset:65});
              getTaoHongTemplateList();
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

<style scoped lang="scss">
@import "@/theme/global.scss";
.basic-btns {
        display: flex;
        justify-content: space-between;
        margin-bottom: 20px;
    }
:deep(.y9-dialog-overlay .y9-dialog .y9-dialog-body .y9-dialog-content[data-v-0c2bb858]) {
        padding: 21px 45px 21px 21px !important;
    }
</style>
