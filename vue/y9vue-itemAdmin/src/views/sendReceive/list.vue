<template>
  <div class="content">
      <el-card class="sendReceiveList">
        <template #header>
          <div class="card-header">
            <span>收发部门设置-{{deptName}}</span>
          </div>
        </template>
        <div style="margin-bottom: 8px;height: 30px;">
            <div style="float:left;" v-if="deptId!=''">
          是否收发部门：
              <el-switch
                v-model="receiveDept"
                class="mb-2"
                inline-prompt
                active-text="是"
                inactive-text="否"
                @change="setReceiveDept"
              /></div>
            <div style="float:right;" v-if="isShow">
              <el-button-group>
                <el-button type="primary" @click="addPerson"><i class="ri-add-line"></i>收发员</el-button>
                <el-button type="primary" @click="setOrCancelSend(true)"><i class="ri-check-line"></i>设置发文</el-button>
                <el-button type="danger"  @click="setOrCancelSend(false)"><i class="ri-close-line"></i>取消发文</el-button>
                <el-button type="primary" @click="setOrCancelReceive(true)"><i class="ri-check-line"></i>设置收文</el-button>
                <el-button type="danger" @click="setOrCancelReceive(false)"><i class="ri-close-line"></i>取消收文</el-button>
              </el-button-group>
            </div>
        </div>
        
        <div class="sendReceiveTable" v-if="isShow" style="margin-top: 15px;">
          <y9Table :config="tableConfig" @on-change="handleSelectionChange">
            <template #send="{row,column,index}">
              <i v-if="row.send=='是'" class="ri-check-line" style="color:green;font-weight: bold;font-size: 22px;"></i>
              <i v-if="row.send=='否'" class="ri-close-line" style="color:red;font-weight: bold;font-size: 22px;"></i>
            </template>
            <template #receive="{row,column,index}">
              <i v-if="row.receive=='是'" class="ri-check-line" style="color:green;font-weight: bold;font-size: 22px;"></i>
              <i v-if="row.receive=='否'" class="ri-close-line" style="color:red;font-weight: bold;font-size: 22px;"></i>
            </template>
            <template #opt="{row,column,index}">
              <el-button class="global-btn-second" size="small" @click="deletePerson(row)"><i class="ri-delete-bin-line"></i>删除</el-button>
            </template>
          </y9Table>
        </div>
      </el-card>
      <el-drawer v-model="treeDrawer" direction="rtl" title="选择人员">
        <permTree ref="permTreeRef" :treeApiObj="treeApiObj" :showHeader="showHeader" :selectField="selectField" @onCheckChange="onCheckChange"/>
        <div slot="footer" class="dialog-footer" style="text-align: center;margin-top: 15px;">
          <el-button type="primary" @click="confirmClick"><span>保存</span></el-button>
          <el-button  @click="treeDrawer = false"><span>取消</span></el-button>
        </div>
    </el-drawer>
  </div>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import type { ElMessage,ElLoading } from 'element-plus';
import {getDeptTree,getOrgChildTree,saveOrCancelDept,personList,savePerson,delPerson,setSend,setReceive} from '@/api/itemAdmin/sendReceive';

const props = defineProps({
  deptId : String,
  deptName : String,
  isReceiveSendDept:Boolean
})

const params = ref({deptId:''});
const data = reactive({
    treeDrawer:false,
    receiveDept:false,
    isShow:false,
		permTreeRef:"",//tree实例
		treeApiObj:{//tree接口对象
			topLevel: ()=>{
        return getDeptTree(params.value);
      },
			childLevel:{//子级（二级及二级以上）tree接口
				api:'',
				params:{treeType:''}
			},
			search:{
				api:'',
				params:{
					key:'',
					treeType:'tree_type_position'
				}
			},
		},
    selectField: [
			//设置需要选择的字段
			{
				fieldName: 'orgType',
				value: ['Position'],
			},
		],
		treeSelectedData: [],//tree选择的数据
		showHeader:false,
    tableConfig: {
        //人员列表表格配置
        columns: [
            { title: '', type: 'selection', width: '60' },
            { title: '序号', type: 'index', width: '60' },
            { title: '姓名', key: 'name',width:'400'},
            { title: '发文权限', key: 'send',width:'auto',slot:'send' },
            { title: '收文权限', key: 'receive', width: '150' ,slot:'receive' },
            { title: '操作', slot: 'opt', width: '260' },
        ],
        border: true,
        tableData: [],
        pageConfig: false, //取消分页
    },
});

let {
		permTreeRef,
    receiveDept,
    isShow,
		treeDrawer,
		treeApiObj,
    tableConfig,
		showHeader,
		treeSelectedData,
    selectField
	} = toRefs(data);

watch(props,(newProps) => {
  receiveDept.value = props.isReceiveSendDept;
  isShow.value = props.isReceiveSendDept;
  getList()
})

// onMounted (() => {
//   //receiveDept.value = props.isReceiveSendDept;
//   getList()
// })

async function getList() {
  let res = await personList(props.deptId);
  tableConfig.value.tableData = res.data;
}

const setReceiveDept = (val) => {
  isShow.value = val;
  let status = ''
  if(val){
    status = 'save';
  }
  saveOrCancelDept(props.deptId,status).then(res => {
      ElMessage({ type: res.success?"success":"error", message: res.msg ,offset:65});
  });
}

const addPerson = () => {
    params.value.deptId = props.deptId;
    treeSelectedData.value = [];
		treeApiObj.value.childLevel.api = getOrgChildTree;
		treeApiObj.value.childLevel.params.treeType = 'tree_type_position';
		treeDrawer.value = true;
		setTimeout(() => {
			permTreeRef.value.onRefreshTree();
		}, 500);
}

const pids = ref([]);
function onOrgClick(ids) {
  pids.value = ids.value;
}

const cancelClick = () => {
  treeDrawer.value = false;
}

//tree点击选择框时触发
const onCheckChange = (node,isChecked) => {
		//已经选择的节点
		treeSelectedData.value = permTreeRef.value?.y9TreeRef?.getCheckedNodes(true);
	}

const confirmClick = () => {
  if(treeSelectedData.value.length == 0){
			ElNotification({title: '提示',message: '请选择人员',type: 'info',duration: 2000,offset: 80});
			reject();
			return;
		}
		let personIds = [];
		for (let i = 0; i < treeSelectedData.value.length; i++) {
			personIds.push(treeSelectedData.value[i].id);
		}
  const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
  savePerson(personIds.toString(),props.deptId).then(res => {
    loading.close();
        if(res.success){
          ElMessage({type: 'success', message: res.msg,offset: 65});
          getList();
          treeDrawer.value = false;
        }else{
          ElMessage({type: 'error', message: res.msg, offset: 65});
        }
      }).catch(() => {
        loading.close();
    });
}

const multipleSelection = ref([]);
const handleSelectionChange = (val) => {
  multipleSelection.value = val;
}

const setOrCancelSend = (send) => {//设置、取消发文权限
      if(multipleSelection.value.length == 0){
        ElMessage({ type: 'error', message: '请勾选列表人员', offset: 65 });
        return;
      }
      const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
      setSend(send,multipleSelection.value.join(",")).then(res => {
          loading.close();
          if(res.success){
            ElMessage({type: 'success', message: res.msg,offset: 65});
            getList();
          }else{
            ElMessage({type: 'error', message: res.msg, offset: 65});
          }
      });
    }

const setOrCancelReceive = (receive) =>{//设置、取消收文权限
      if(multipleSelection.value.length == 0){
        ElMessage({ type: 'error', message: '请勾选列表人员', offset: 65 });
        return;
      }
      const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
      setReceive(receive,multipleSelection.value.join(",")).then(res => {
          loading.close();
          if(res.success){
            ElMessage({type: 'success', message: res.msg,offset: 65});
            getList();
          }else{
            ElMessage({type: 'error', message: res.msg, offset: 65});
          }
      });
}

const deletePerson = (rows) => {
    ElMessageBox.confirm("您确定要删除数据吗?","提示",{
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
          delPerson(rows.id).then(res => {
          loading.close();
          if(res.success){
            ElMessage({type: 'success', message: res.msg,offset: 65});
            getList();
          }else{
            ElMessage({type: 'error', message: res.msg, offset: 65});
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
.sendReceiveTable .el-form-item--default {
    margin-bottom: 0px;
}
.sendReceiveTable .el-form-item {
    margin-bottom: 0px;
}
.sendReceiveTable .el-form-item__error {
    color: var(--el-color-danger);
    font-size: 12px;
    line-height: 1;
    padding-top: 2px;
    position: relative;
    top: 0%; 
    left: 0;
}

.sendReceiveList {
  height: calc( 100vh - 60px - 80px - 35px );
  overflow: hidden;
  --1b065fc0-showBoxShadow__2px_2px_2px_1px_rgba_0_0_0_0_06____none_: 2px 2px 2px 1pxrgba(0,0,0,0.06);
  --1b065fc0-showBorder__solid_1px__ebeef5___none_: solid 1px #ebeef5;
  --1b065fc0-showHeaderSplit__solid_1px__eee___none_: solid 1px #eee;
  --1b065fc0-headerPadding__16px___none_: 16px;
}

.sendReceiveList .el-card__header {
    line-height: 30px;
}

.content .el-dialog__body{
  padding: 5px 10px;
}

.content .el-drawer__header{
		margin-bottom:0;
		padding-bottom:16px;
		border-bottom: 1px solid #eee;
	}
</style>
