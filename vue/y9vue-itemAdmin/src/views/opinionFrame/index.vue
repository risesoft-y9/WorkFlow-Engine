<template>
      <el-form class="opinionFrameform" ref="opinionFrameForm" :model="formData" :rules="rules">
        <y9Table :config="tableConfig" :filterConfig="filterConfig"  @on-curr-page-change="onCurrPageChange"
            @on-page-size-change="onPageSizeChange">
            <template #addBtn>
              <el-button class="global-btn-main" type="primary" @click="addOpinionFrame"><i class="ri-add-line"></i>新增</el-button>
            </template>
            <template #name="{row,column,index}">
                  <el-form-item prop="name" v-if="editIndex === index">
                      <el-input  v-model="formData.name" clearable/>
                  </el-form-item>
                  <span v-else>{{row.name}}</span>
              </template>
              <template #mark="{row,column,index}">
                  <el-form-item prop="mark" v-if="editIndex === index">
                          <el-input :disabled="isEdit" v-model="formData.mark" clearable/>
                  </el-form-item>
                  <span v-else>{{row.mark}}</span>
              </template>
              <template #opt_button="{row,column,index}">
                <div v-if="editIndex === index">
                  <el-button class="global-btn-second" size="small" @click="saveData(opinionFrameForm)"><i class="ri-book-mark-line"></i>保存</el-button>
                  <el-button class="global-btn-second" size="small" @click="cancalData(opinionFrameForm)"><i class="ri-close-line"></i>取消</el-button>
              </div>
              <div v-else>
                <el-button class="global-btn-second" size="small" @click="bindDetail(row)"><i class="ri-book-3-line"></i>授权详情</el-button>
                <el-button class="global-btn-second" size="small" @click="editOpinionFrame(row,index)"><i class="ri-edit-line"></i>修改</el-button>
                <el-button class="global-btn-danger" type="danger" size="small" @click="delOpinionFrame(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
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
import {opinionFrameList,saveOrUpdate,removeOpinionFrame} from '@/api/itemAdmin/opinionFrame';
import AuthorizeDetail from '@/views/opinionFrame/authorizeDetail.vue';

const opinionFrameForm = ref<FormInstance>();
const rules = reactive<FormRules>({
        name:{ required: true,message: '请输入意见框名称', trigger: 'blur' },
        mark:{ required: true,message: '请输入唯一标识', trigger: 'blur' },
      });
const data = reactive({
      isEmptyData:false,
      editIndex:'',
      formData:{id:'',name:'',mark:''},
      isEdit:false,
		  tableConfig: {
        columns: [
          { title: "序号", type:'index', width: '60', },
          { title: "意见框名称", key: "name", width: '150',slot:'name'},
          { title: "唯一标示", key: "mark",slot:'mark' },
          { title: "操作人", key: "userName", width: '150'},
          { title: "添加时间", key: "createDate", width: '180', },
          { title: "修改时间", key: "modifyDate", width: '200', },
          { title: "操作", width: '280', slot: 'opt_button' },
        ],
        border: false,
        headerBackground: true,
        tableData: [],
        pageConfig: {
          // 分页配置，false隐藏分页
          currentPage: 1, //当前页数，支持 v-model 双向绑定
          pageSize: 15, //每页显示条目个数，支持 v-model 双向绑定
          total: 0, //总条目数
        },
		},
		filterConfig:{//过滤配置
			itemList:[
				{
					type:"slot",
					span:8,
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
  } = toRefs(data);

async function getList() {
    let page = tableConfig.value.pageConfig.currentPage;
    let rows = tableConfig.value.pageConfig.pageSize;
    let res = await opinionFrameList(page, rows);
    tableConfig.value.tableData = res.rows;
    tableConfig.value.pageConfig.total = res.total;
}
getList()

//当前页改变时触发
function onCurrPageChange(currPage) {
    tableConfig.value.pageConfig.currentPage = currPage;
    getList();
}
//每页条数改变时触发
function onPageSizeChange(pageSize) {
    tableConfig.value.pageConfig.pageSize = pageSize;
    getList();
}

const addOpinionFrame = () => {
  for (let i = 0; i < tableConfig.value.tableData.length; i++) {
    if(tableConfig.value.tableData[i].id==''){
        isEmptyData.value = true;
    }
  }
  if(!isEmptyData.value){
    editIndex.value = tableConfig.value.tableData.length;
    tableConfig.value.tableData.push({id:'',name:'',mark:''});
    formData.value.id = '';
    formData.value.name = '';
    formData.value.mark = '';
    isEdit.value = false;
  }
}

const editOpinionFrame = (rows,index) => {
  editIndex.value = index;
  formData.value.id = rows.id;
  formData.value.name = rows.name;
  formData.value.mark = rows.mark;
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
    width:'65%',
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
              getList();
            } else {
              ElMessage({message:res.msg,type: 'error',offset:65});
            }
      });
    }
  });
}

const cancalData = (refForm) => {
    editIndex.value = '';
    formData.value.name = '';
    formData.value.mark = '';
    refForm.resetFields();
    for (let i = 0; i < tableConfig.value.tableData.length; i++) {
      if(tableConfig.value.tableData[i].id==''){
        tableConfig.value.tableData.splice(i,1);
      }
    }
    isEmptyData.value = false;
}

const delOpinionFrame = (rows) => {
    ElMessageBox.confirm("您确定要删除编号吗?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
         removeOpinionFrame(rows.id).then(res => {
            if (res.success) {
              ElMessage({ type: "success", message: res.msg ,offset:65});
              editIndex.value = '';
              getList();
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
.opinionFrameform .el-form-item{
  margin-bottom: 0px !important;
}
</style>
