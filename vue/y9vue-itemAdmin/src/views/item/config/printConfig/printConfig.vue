<template>
  <y9Card :title="`打印模板配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
		<div class="margin-bottom-20" v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''">
			<el-button type="primary" @click="bindTemplate('word')" v-if="maxVersion != 1"
				class="global-btn-main">
			  	<i class="ri-file-word-line"></i>
			  	<span>Word模板</span>
			</el-button>
      <el-button type="primary" @click="bindTemplate('form')" 
				class="global-btn-main">
			  	<i class="ri-table-line"></i>
			  	<span>表单模板</span>
			</el-button>
		</div>
		<y9Table :config="printBindTableConfig" ></y9Table>
		<el-drawer class="eldrawer" v-model="tableDrawer" direction='rtl' :title="title" @close="closeDrawer">
     		<div style="margin-bottom: 10px;text-align: left;">
				<el-input style="width:200px;margin-right: 5px;" v-model="searchName" placeholder="模板名称" clearable></el-input>
				<el-button type="primary" size="small" @click="search"><i class="ri-search-2-line"></i>搜索</el-button>
			</div>
			<y9Table v-if="wordShow" :config="wordTableConfig" @select-all="handlerGetData" @select="handlerGetData"></y9Table>
			<y9Table v-if="formShow" :config="formTableConfig" @select-all="handlerGetData" @select="handlerGetData"></y9Table>
			<div slot="footer" class="dialog-footer" style="text-align: center;margin-top: 15px;">
				<el-button type="primary" @click="saveBind"><span>保存</span></el-button>
				<el-button @click="tableDrawer = false"><span>取消</span></el-button>
			</div>
		</el-drawer>
	</y9Card>
</template>

<script lang="ts" setup>
  import { $deepAssignObject, } from '@/utils/object.ts'
  import {getBindTemplateList,deleteBindPrintTemplate,saveBindTemplate,getPrintFormList} from "@/api/itemAdmin/item/printConfig";
  import {getPrintTemplateList} from "@/api/itemAdmin/printTemplate";
  const props = defineProps({
      currTreeNodeInfo: {//当前tree节点信息
        type: Object,
        default:() => { return {} }
      },
    })
	
	const data = reactive({
		//当前节点信息
		searchName:'',
		title:'',
		wordShow:false,
		formShow:false,
		currInfo:props.currTreeNodeInfo,
		tableDrawer: false,
		printBindTableConfig: {//人员列表表格配置
			columns: [
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{
					title: "模板名称",
					key: "templateName",
				},
				{
					title: "模板类型",
					key: "templateType",
					render: (row) => {
						var str = '';
						switch (row.templateType) {
							case '1':
								str = 'Word模板';
								break;
							case '2':
								str = '表单模板';
								break;
							default:
								break;
						}
						return str;
					}
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
		wordTableConfig: {
			columns: [
				{ title: '', type: 'selection', fixed: 'left',width: '60' },
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{
					title: "模板名称",
					key: "fileName",
				}
			],
			tableData: [],
			pageConfig: false,
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
				console.log('visible',visible)
				if(!visible){
					getTemplateBindList();
				}
			}
		},
    taskDefKey:'',
	})
	
	let {
		searchName,
		title,
		wordShow,
		formShow,
		tableDrawer,
		currInfo,
		printBindTableConfig,
		wordTableConfig,
		formTableConfig,
		dialogConfig,
        taskDefKey,
	} = toRefs(data);
	
	watch(() => props.currTreeNodeInfo,(newVal) => {
		currInfo.value = $deepAssignObject(currInfo.value, newVal);
    	getTemplateBindList();
	})

	onMounted(()=>{
		getTemplateBindList();
	});

  async function getTemplateBindList(){//权限配置
	printBindTableConfig.value.tableData = [];
    let res = await getBindTemplateList(props.currTreeNodeInfo.id);
    if(res.success){
      printBindTableConfig.value.tableData = res.data;
    }
  }

  async function bindTemplate(type){
		if(type=='word'){
			title.value = '绑定Word模板';
			wordShow.value = true;
			formShow.value = false;
			wordTableConfig.value.tableData = [];
			getWordList();
		}else{
			title.value = '绑定表单模板';
			wordShow.value = false;
			formShow.value = true;
			formTableConfig.value.tableData = [];
			getFormList();
		}
		tableDrawer.value = true;
 }

 function search(){
	if(wordShow.value){
		getWordList();
	}else{
		getFormList();
	}
 }

 function closeDrawer(){
	searchName.value = '';
	getTemplateBindList();
 }

const selectData = ref([]);
 // 表格 选择框 选择后获取数据
 function handlerGetData(id, data) {
	selectData.value = id;
 }

 async function getFormList(){
	let res = await getPrintFormList(props.currTreeNodeInfo.id,searchName.value);
	if(res.success){
		formTableConfig.value.tableData = res.data;
	}
 }

  async function getWordList(){
	let res = await getPrintTemplateList(searchName.value);
	if(res.success){
		wordTableConfig.value.tableData = res.data;
	}
 }

	async function saveBind(){
		let templateId ='';
		let templateName = '';
		let templateType = '';
		let templateUrl = '';
		if(selectData.value.length == 0){
			ElNotification({title: '操作提示',message: '请勾选要绑定的数据',type: 'error',duration: 2000,offset: 80});
			return;
		}
		if(selectData.value.length > 1){
			ElNotification({title: '操作提示',message: '只能勾选一条绑定的数据',type: 'error',duration: 2000,offset: 80});
			return;
		}
		if(wordShow.value){
			templateId = selectData.value[0].id;
			templateName = selectData.value[0].fileName;
			templateUrl = selectData.value[0].fileUrl;
			templateType = '1';
		}else{
			templateId = selectData.value[0].formId;
			templateName = selectData.value[0].formName;
			templateType = '2';
		}
		let result = {success:false,msg:''};
		result = await saveBindTemplate(props.currTreeNodeInfo.id,templateId,templateName,templateType,templateUrl);
		ElNotification({
			title: result.success ? '成功' : '失败',
			message: result.msg,
			type: result.success ? 'success' : 'error',
			duration: 2000,
			offset: 80
		});
		if(result.success){
			getTemplateBindList();
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
			result = await deleteBindPrintTemplate(row.id);
			ElNotification({
				title: result.success ? '成功' : '失败',
				message: result.msg,
				type: result.success ? 'success' : 'error',
				duration: 2000,
				offset: 80
			});
			if(result.success){
				getTemplateBindList();
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