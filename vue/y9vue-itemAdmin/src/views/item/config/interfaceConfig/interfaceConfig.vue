<template>
  	<y9Card :title="`接口配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
		<div class="margin-bottom-20" v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''">
      		<el-button type="primary" @click="bindInterface()" 
				class="global-btn-main">
			  	<i class="ri-git-pull-request-line"></i>
			  	<span>绑定接口</span>
			</el-button>
		</div>
		<y9Table :config="tableConfig" >
			<template #opt_button="{row, column, index}">
				<span style="margin-right:15px;" @click="taskBind(row)"><i class="ri-add-line"></i>任务绑定</span>
				<span style="margin-right:15px;" @click="paramsBind(row)"><i class="ri-add-line"></i>参数绑定</span>
				<span style="margin-right:15px;" @click="delBind(row)"><i class="ri-delete-bin-line"></i>删除绑定</span>
			</template>
		</y9Table>
		<el-drawer class="eldrawer" v-model="tableDrawer" direction='rtl' title="绑定接口" @close="closeDrawer">
			<div style="margin-bottom: 10px;text-align: left;">
				<el-input style="width:200px;margin-right: 5px;" v-model="interfaceName" placeholder="接口名称" clearable></el-input>
				<el-input style="width:200px;margin-right: 5px;" v-model="interfaceAddress" placeholder="接口地址" clearable></el-input>
				<el-button type="primary" size="small" @click="getSelectList"><i class="ri-search-2-line"></i>搜索</el-button>
			</div>
			<y9Table :config="selectTableConfig" @select-all="handlerGetData" @select="handlerGetData"></y9Table>
			<div slot="footer" class="dialog-footer" style="text-align: center;margin-top: 15px;">
				<el-button type="primary" @click="saveBind"><span>保存</span></el-button>
				<el-button @click="tableDrawer = false"><span>取消</span></el-button>
			</div>
		</el-drawer>
		<y9Dialog v-model:config="dialogConfig" class="ParamsList">
			<TaskBind ref="TaskBindRef" v-if="dialogConfig.type == 'taskBind'" :currTreeNodeInfo="currTreeNodeInfo" :interface="row"/>
			<ParamsList ref="ParamsListRef" v-if="dialogConfig.type == 'paramsList'" :currTreeNodeInfo="currTreeNodeInfo" :interface="row"/>
		</y9Dialog>
	</y9Card>
</template>

<script lang="ts" setup>
  import { $deepAssignObject, } from '@/utils/object.ts'
  import {getBindList,saveItemBind,removeBind} from "@/api/itemAdmin/item/interfaceConfig";
  import {findInterfaceList} from '@/api/itemAdmin/interface';
  import TaskBind from './taskBind.vue'
  import ParamsList from './paramsList.vue'
  const props = defineProps({
      currTreeNodeInfo: {//当前tree节点信息
        type: Object,
        default:() => { return {} }
      },
      maxVersion:Number,
      selectVersion:Number,
    })
	
	const data = reactive({
		//当前节点信息
		currInfo:props.currTreeNodeInfo,
		tableDrawer: false,
		tableConfig: {//人员列表表格配置
			columns: [
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{
					title: "接口名称",
					key: "interfaceName",
					width: '200',
				},
				{
					title: "接口地址",
					key: "interfaceAddress",
				},
				{
					title: "操作",
					width: '300',
					slot: 'opt_button'
				},
			],
			tableData: [],
			pageConfig:false,//取消分页
			height:'auto'
		},
		selectTableConfig: {
			columns: [
				{ title: '', type: 'selection', fixed: 'left' ,width: '60' },
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{ title: "接口名称", key: "interfaceName", width: '160'},
				// { title: "请求类型", key: "requestType", width: '120'},
          		{ title: "接口地址", key: "interfaceAddress"},
			],
			tableData: [],
			pageConfig: false,
			height:'auto'
		},
		interfaceName:'',
		interfaceAddress:'',
		selectData:[],
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
				if(!visible){
				}
			}
		},
		row:'',
	})
	
	let {
		currInfo,
		tableDrawer,
		tableConfig,
		selectTableConfig,
        interfaceName,
		interfaceAddress,
		selectData,
		dialogConfig,
		row,
	} = toRefs(data);
	
	watch(() => props.currTreeNodeInfo,(newVal) => {
		currInfo.value = $deepAssignObject(currInfo.value, newVal);
    	getConfig();
	},{deep:true,})

	onMounted(()=>{
		getConfig();
	});

  async function getConfig(){
	tableConfig.value.tableData = [];
    let res = await getBindList(props.currTreeNodeInfo.id);
    if(res.success){
      tableConfig.value.tableData = res.data;
    }
  }

  function taskBind(rows) {
	row.value = rows;
	Object.assign(dialogConfig.value,{
		show:true,
		width:'50%',
		title:'任务绑定【'+rows.interfaceName+'】',
		showFooter:false,
		type:'taskBind'
	});
  }

  function paramsBind(rows) {
	row.value = rows;
	Object.assign(dialogConfig.value,{
		show:true,
		width:'50%',
		title:'参数列表【'+rows.interfaceName+'】',
		showFooter:false,
		type:'paramsList'
	});
  }

 // 表格 选择框 选择后获取数据
 function handlerGetData(id, data) {
	selectData.value = id;
 }

  function closeDrawer(){
	interfaceName.value = '';
	interfaceAddress.value = '';
 }

async function bindInterface(){
	selectTableConfig.value.tableData = [];
	selectData.value = [];
	getSelectList();
	tableDrawer.value = true;
 }

 async function getSelectList(){
	selectTableConfig.value.tableData = [];
	let res = await findInterfaceList(interfaceName.value, '',interfaceAddress.value);
	if(res.success){
		for(let item of res.data){
			let add = true;
			for(let row of tableConfig.value.tableData){
				if(item.id == row.interfaceId){
					add = false;
					break;
				}
			}
			if(add){
				selectTableConfig.value.tableData.push(item);
			}
		}
	}
 }

function delBind(row){
	ElMessageBox.confirm(
		'你确定要删除绑定的接口吗？',
		'提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'info',
	}).then(async () => {
		let result = {success:false,msg:''};
		result = await removeBind(row.id);
		ElNotification({
			title: result.success ? '成功' : '失败',
			message: result.msg,
			type: result.success ? 'success' : 'error',
			duration: 2000,
			offset: 80
		});
		if(result.success){
			getConfig();
		}
	}).catch(() => {
		ElMessage({
			type: 'info',
			message: '已取消删除',
			offset: 65
		});
	});
}

 async function saveBind(){
	let ids = [];
	if(selectData.value.length == 0){
		ElNotification({title: '操作提示',message: '请勾选要绑定的数据',type: 'error',duration: 2000,offset: 80});
		return;
	}
	for (let i = 0; i < selectData.value.length; i++) {
		ids.push(selectData.value[i].id);
	}
	let result = {success:false,msg:''};
	result = await saveItemBind(props.currTreeNodeInfo.id,ids.toString());
	ElNotification({
		title: result.success ? '成功' : '失败',
		message: result.msg,
		type: result.success ? 'success' : 'error',
		duration: 2000,
		offset: 80
	});
	if(result.success){
		getConfig();
		tableDrawer.value = false;
	}
}


</script>

<style>
  	.permconfig .el-dialog__body{
		  padding: 5px 10px;
  	}
</style>
<style lang="scss">
	  .ParamsList .y9-dialog .y9-dialog-content{
		padding-top:10px !important;
	  }
</style>