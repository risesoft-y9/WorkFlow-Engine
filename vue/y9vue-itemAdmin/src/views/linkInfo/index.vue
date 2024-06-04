<template>
      <el-form class="linkInfoForm" ref="linkInfoRef" :model="formData" :rules="rules">
        <y9Table :config="tableConfig" :filterConfig="filterConfig">
            <template #addBtn>
              <el-input style="margin-left: 10px;" placeholder="链接名称" v-model="linkName" clearable/>
              <el-input style="margin-left: 10px;width: 500px;" placeholder="链接地址" v-model="linkUrl" clearable/>
              <el-button style="margin-left: 10px;" class="global-btn-main" type="primary" @click="getTableList"><i class="ri-search-line"></i>搜索</el-button>
              <el-button class="global-btn-main" type="primary" @click="addLinkInfo"><i class="ri-add-line"></i>新增</el-button>
            </template>
            <template #linkName="{row,column,index}">
                  <el-form-item prop="linkName" v-if="editIndex === index">
                      <el-input  v-model="formData.linkName" clearable/>
                  </el-form-item>
                  <span v-else>{{row.linkName}}</span>
              </template>
              <template #linkUrl="{row,column,index}">
                  <el-form-item prop="linkUrl" v-if="editIndex === index">
                    <el-input v-model="formData.linkUrl" clearable/>
                  </el-form-item>
                  <span v-else>{{row.linkUrl}}</span>
              </template>
              <template #opt_button="{row,column,index}">
                <div v-if="editIndex === index">
                  <el-button class="global-btn-second" size="small" @click="saveData(linkInfoRef)"><i class="ri-book-mark-line"></i>保存</el-button>
                  <el-button class="global-btn-second" size="small" @click="cancalData(linkInfoRef)"><i class="ri-close-line"></i>取消</el-button>
              </div>
              <div v-else>
                <el-button class="global-btn-second" size="small" @click="bindDetail(row)"><i class="ri-book-3-line"></i>授权详情</el-button>
                <el-button class="global-btn-second" size="small" @click="editLinkInfo(row,index)"><i class="ri-edit-line"></i>修改</el-button>
                <el-button class="global-btn-danger" type="danger" size="small" @click="delLinkInfo(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
              </div>
              </template>
          </y9Table>  
      </el-form>
      <y9Dialog v-model:config="dialogConfig">
        <AuthorizeDetail ref="authorizeDetail" :row="row"/>
      </y9Dialog>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type { ElMessage,ElLoading } from 'element-plus';
import {getLinkList,saveOrUpdate,removeLink} from '@/api/itemAdmin/linkInfo';
import AuthorizeDetail from '@/views/linkInfo/authorizeDetail.vue';

const linkInfoRef = ref<FormInstance>();
const rules = reactive<FormRules>({
  linkName:{ required: true,message: '请输入链接名称', trigger: 'blur' },
  linkUrl:{ required: true,message: '请输入链接地址', trigger: 'blur' },
});
const data = reactive({
      isEmptyData:false,
      editIndex:'',
      formData:{id:'',linkName:'',linkUrl:''},
      isEdit:false,
		  tableConfig: {
        columns: [
          { title: "序号", type:'index', width: '60', },
          { title: "链接名称", key: "linkName", width: '300',slot: 'linkName'},
          { title: "链接地址", key: "linkUrl",slot: 'linkUrl',align:'left'},
          { title: "添加时间", key: "createTime", width: '180', },
          { title: "操作", width: '280', slot: 'opt_button' },
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
        })
			},
			visibleChange:(visible) => {
			}
		},
    row:'',
    linkName:'',
    linkUrl:'',
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
    linkName,
    linkUrl,
  } = toRefs(data);

async function getTableList() {
    let res = await getLinkList(linkName.value, linkUrl.value);
    tableConfig.value.tableData = res.data;
}
getTableList();

const addLinkInfo = () => {
  for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].id==''){
        isEmptyData.value = true;
    }
  }
  if(!isEmptyData.value){
    editIndex.value = 0;
    tableConfig.value.tableData.unshift({id:'',linkName:'',linkUrl:''});
    formData.value.id = '';
    formData.value.linkName = '';
    formData.value.linkUrl = '';
    isEdit.value = false;
  }
}

const editLinkInfo = (rows,index) => {
  editIndex.value = index;
  formData.value.id = rows.id;
  formData.value.linkName = rows.linkName;
  formData.value.linkUrl = rows.linkUrl;
  isEdit.value = true;
  for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].id==''){
      tableConfig.value.tableData.splice(i,1);
    }
  }
  isEmptyData.value = false;
}

const bindDetail = (rows) => {
  row.value = rows;
  Object.assign(dialogConfig.value,{
    show:true,
    width:'45%',
    title:'授权详情',
    showFooter:false
  });
}

const saveData = (refFrom) => {
  if(!refFrom) return;
  refFrom.validate(valid => {
    if (valid) {
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        saveOrUpdate(formData.value).then(res => {
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
    formData.value.linkName = '';
    formData.value.linkUrl = '';
    refForm.resetFields();
    for (let i = 0; i < tableConfig.value.tableData.length; i++) {
      if(tableConfig.value.tableData[i].id==''){
        tableConfig.value.tableData.splice(i,1);
      }
    }
    isEmptyData.value = false;
}

const delLinkInfo = (rows) => {
    ElMessageBox.confirm("您确定要删除链接吗?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
         removeLink(rows.id).then(res => {
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
.linkInfoForm .el-form-item{
  margin-bottom: 0px !important;
}
</style>
