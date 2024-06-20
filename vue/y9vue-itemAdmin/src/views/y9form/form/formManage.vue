<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-06 16:35:20
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-09 17:15:39
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\views\y9form\form\formManage.vue
-->
<template>
	<y9Card class="y9formcard" :title="`表单管理${currInfo.name ? ' - ' + currInfo.name : ''}`">
		<div class="margin-bottom-20" v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''">
			<el-button type="primary" @click="addForm"
				class="global-btn-main">
			  	<i class="ri-add-line"></i>
			  	<span>表单</span>
			</el-button>
		</div>
		<y9Table :config="formListTableConfig" ></y9Table>
	</y9Card>
	<y9Dialog v-model:config="dialogConfig" :class="dialogConfig.type == 'formMaking' ? 'formMakingDialog':''">
		<newOrModify ref="newOrModifyRef" v-if="dialogConfig.type == 'newOrModify'" :currInfo="currInfo" :formdata="formdata" />
		<formMaking ref="formMakingRef" v-if="dialogConfig.type == 'formMaking'" :formInfo="formInfo" />
	</y9Dialog>
</template>

<script lang="ts" setup>
	import { $deepAssignObject, } from '@/utils/object.ts'
	import newOrModify from './newOrModify.vue';
	import formMaking from './formMaking.vue';
	import {newOrModifyForm,getFormList,removeForm} from '@/api/itemAdmin/y9form';
	const props = defineProps({
		currTreeNodeInfo: {//当前tree节点信息
			type: Object,
			default:() => { return {} }
		},
	})
	
	const data = reactive({
		//当前节点信息
		currInfo:props.currTreeNodeInfo,
		formListTableConfig: {//人员列表表格配置
			columns: [
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{
					title: "表单名称",
					key: "formName",
				},{
					title: "表单类型",
					key: "formType",
					width: '100',
					render:(row) =>{
						if(row.formType == 1){
							return '主表单';
						}else if(row.formType == 2){
							return '前置表单';
						}
					}
				},
				{
					title: "系统英文名称",
					key: "systemName",
				},
				{
					title: "系统中文名称",
					key: "systemCnName",
				},
				{
					title: "修改时间",
					key: "updateTime",
					width: '165',
				},
				{
					title: "操作",
					render: (row) => {
						let button = [ 
							h('i', {
								class: 'ri-file-code-line',
								style:{
									marginRight:'10px',
									fontSize:'18px'
								},
								title:'表单设计',
								onClick: () => {
									showFormMaking(row);
								},
							}),
							h('i', {
								class: 'ri-edit-line',
								style:{
									marginRight:'10px',
									fontSize:'18px'
								},
								title:'编辑',
								onClick: () => {
									editForm(row);
								},
							}),
							h('i', {
								title:'删除',
								class: 'ri-delete-bin-line',
								style:{
									fontSize:'18px'
								},
								onClick: () => {
									ElMessageBox.confirm(
										`是否删除【${row.formName}】?`,
										'提示', {
										confirmButtonText: '确定',
										cancelButtonText: '取消',
										type: 'info',
									}).then(async () => {
										let result = {success:false,msg:''};
										result = await removeForm(row.id);
										ElNotification({
											title: result.success ? '成功' : '失败',
											message: result.msg,
											type: result.success ? 'success' : 'error',
											duration: 2000,
											offset: 80
										});
										if(result.success){
											formListTableConfig.value.tableData.forEach((item, index) => {
												if (item.id == row.id) {
													formListTableConfig.value.tableData.splice(index, 1);
												}
											})
										}
									}).catch(() => {
										ElMessage({
											type: 'info',
											message: '已取消删除',
											offset: 65
										});
									});
			
								},
							})
						]
						return button;
					}
				},
			],
			tableData: [],
			pageConfig:false,//取消分页
			height:'auto',
			maxHeight:'none'
		},
		//弹窗配置
		dialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
					let valid = await newOrModifyRef.value.validForm();
					if(!valid){
						reject();
						return;
					}
					let form = newOrModifyRef.value.form;
					let res = await newOrModifyForm(form);
					ElNotification({
						title: res.success ? '成功' : '失败',
						message: res.msg,
						type: res.success ? 'success' : 'error',
						duration: 2000,
						offset: 80
					});
					if(res.success){
						getFormData();
					}
					resolve()
				})
			},
			visibleChange:(visible) => {
				// console.log('visible',visible)
			}
		},
		newOrModifyRef:'',
		formdata:null,
		formMakingRef:'',
		formInfo:{},
	})
	
	let {
		currInfo,
		formListTableConfig,
		dialogConfig,
		newOrModifyRef,
		formdata,
		formMakingRef,
		formInfo
	} = toRefs(data);
	
	watch(() => props.currTreeNodeInfo,(newVal) => {
		currInfo.value = $deepAssignObject(currInfo.value, newVal);
		getFormData();
	})

	onMounted(()=>{
		getFormData();
	});

	async function getFormData(){
		let res = await	getFormList(props.currTreeNodeInfo.systemName,1,50);
		if(res.success){
			formListTableConfig.value.tableData = res.rows;
		}
	}

	function addForm() {
		formdata.value = null;
		Object.assign(dialogConfig.value,{
			show:true,
			width:'30%',
			title:'新增表单',
			type:'newOrModify',
			cancelText: '取消',
			fullscreen:false,
			showFooter:true,
		})
	}

	function editForm(row) {
		formdata.value = row;
		Object.assign(dialogConfig.value,{
			show:true,
			width:'30%',
			title:'编辑表单',
			type:'newOrModify',
			cancelText: '取消',
			fullscreen:false,
			showFooter:true,
		})
	}

	function showFormMaking(row){
		formInfo.value = row;
		Object.assign(dialogConfig.value,{
			show:true,
			fullscreen:true,
			type:'formMaking',
			title:'表单设计【'+row.formName+'】',
			showFooter:false,
			showHeaderFullscreen:false
		})
	}
</script>

<style lang="scss">
.formMakingDialog .y9-dialog-content{
	padding: 0px 0px !important;
}

.formMakingDialog .y9-dialog-content .y9-dialog-content{
	padding: 26px 26px !important;
}
</style>