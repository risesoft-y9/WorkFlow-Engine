<template>
  <y9Card :title="`前置表单配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
		<div class="margin-bottom-20" v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''">
      		<el-button type="primary" @click="bindForm()" 
				class="global-btn-main">
			  	<i class="ri-table-line"></i>
			  	<span>绑定表单</span>
			</el-button>
		</div>
		<y9Table :config="tableConfig" ></y9Table>
		<el-drawer class="eldrawer" v-model="tableDrawer" direction='rtl' title="绑定前置表单" @close="closeDrawer">
     		<div style="margin-bottom: 10px;text-align: left;">
				<el-input style="width:200px;margin-right: 5px;" v-model="searchName" placeholder="模板名称" clearable></el-input>
				<el-button type="primary" size="small" @click="search"><i class="ri-search-2-line"></i>搜索</el-button>
			</div>
			<y9Table :config="formTableConfig" @select-all="handlerGetData" @select="handlerGetData"></y9Table>
			<div slot="footer" class="dialog-footer" style="text-align: center;margin-top: 15px;">
				<el-button type="primary" @click="saveBind"><span>保存</span></el-button>
				<el-button @click="tableDrawer = false"><span>取消</span></el-button>
			</div>
		</el-drawer>
	</y9Card>
</template>

<script lang="ts" setup>
  import { $deepAssignObject, } from '@/utils/object.ts'
  import {getBindList,deleteBindForm,saveBindForm,getY9FormList} from "@/api/itemAdmin/item/preFormConfig";
  const props = defineProps({
      currTreeNodeInfo: {//当前tree节点信息
        type: Object,
        default:() => { return {} }
      },
    })
	
	const data = reactive({
		//当前节点信息
		searchName:'',
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
					title: "表单名称",
					key: "formName",
				},
				{
					title: "操作",
					render: (row) => {
						let button = [ 
							h('span',{
								style:{
									marginRight:'15px'
								},
								onClick: () => {
									deleteBind(row);
								}
							},[
								h('i',{
									class: 'ri-delete-bin-line',
									style:{
										marginRight:'4px'
									}
								}),
							],'删除'),
						]
						return button;
					}
				},
			],
			tableData: [],
			pageConfig:false,//取消分页
			height:'auto'
		},
		formTableConfig: {
			columns: [
				{ title: '', type: 'selection', fixed: 'left' ,width: '60' },
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{
					title: "表单名称",
					key: "formName",
				}
			],
			tableData: [],
			pageConfig: false,
			height:'auto'
		},
	})
	
	let {
		searchName,
		tableDrawer,
		currInfo,
		tableConfig,
		formTableConfig,
	} = toRefs(data);
	
	watch(() => props.currTreeNodeInfo,(newVal) => {
		currInfo.value = $deepAssignObject(currInfo.value, newVal);
    	getPreFormBind();
	})

	onMounted(()=>{
		getPreFormBind();
	});

  async function getPreFormBind(){
	tableConfig.value.tableData = [];
    let res = await getBindList(props.currTreeNodeInfo.id);
    if(res.success){
		if(res.data != null){
			tableConfig.value.tableData.push(res.data);
		}
    }
  }

  async function bindForm(){
		formTableConfig.value.tableData = [];
		getFormList();
		tableDrawer.value = true;
 }

 function search(){
	getFormList();
 }

 function closeDrawer(){
	searchName.value = '';
 }

const selectData = ref([]);
 // 表格 选择框 选择后获取数据
 function handlerGetData(id, data) {
	selectData.value = id;
 }

 async function getFormList(){
	let res = await getY9FormList(props.currTreeNodeInfo.id,props.currTreeNodeInfo.systemName,searchName.value);
	if(res.success){
		formTableConfig.value.tableData = res.data;
	}
 }


	async function saveBind(){
		let formId ='';
		let formName = '';
		if(selectData.value.length == 0){
			ElNotification({title: '操作提示',message: '请勾选要绑定的数据',type: 'error',duration: 2000,offset: 80});
			return;
		}
		if(selectData.value.length > 1){
			ElNotification({title: '操作提示',message: '只能勾选一条绑定的数据',type: 'error',duration: 2000,offset: 80});
			return;
		}
		formId = selectData.value[0].formId;
		formName = selectData.value[0].formName;
		let result = {success:false,msg:''};
		result = await saveBindForm(props.currTreeNodeInfo.id,formId,formName);
		ElNotification({
			title: result.success ? '成功' : '失败',
			message: result.msg,
			type: result.success ? 'success' : 'error',
			duration: 2000,
			offset: 80
		});
		if(result.success){
			getPreFormBind();
			tableDrawer.value = false;
		}
    }

	function deleteBind(row){
		ElMessageBox.confirm(
			'你确定要删除绑定的模板吗？',
			'提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'info',
		}).then(async () => {
			let result = {success:false,msg:''};
			result = await deleteBindForm(row.id);
			ElNotification({
				title: result.success ? '成功' : '失败',
				message: result.msg,
				type: result.success ? 'success' : 'error',
				duration: 2000,
				offset: 80
			});
			if(result.success){
				getPreFormBind();
			}
		}).catch(() => {
			ElMessage({
				type: 'info',
				message: '已取消删除',
				offset: 65
			});
		});
	}
</script>

<style>
  	.permconfig .el-dialog__body{
		  padding: 5px 10px;
  	}

	  .eldrawer .el-drawer__header{
		margin-bottom:0;
		padding-bottom:16px;
		border-bottom: 1px solid #eee;
	}
</style>