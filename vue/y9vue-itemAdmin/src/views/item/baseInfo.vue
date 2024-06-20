<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-05 16:21:46
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2022-07-11 18:19:18
 * @FilePath: \workspace-y9boot-9.5-vuee:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\views\item\baseInfo.vue
-->
<template>
	<y9Card :headerPadding="false">
		<template #header>
			<div class="slot-header">
				<span>事项信息{{ currInfo.name ? ' - ' + currInfo.name : '' }}</span>
			</div>
		</template>
		<div class="oth-btns">
			<div v-if="isEditState">
				<el-button type="primary" class="global-btn-main" :loading="saveFormBtnLoading" @click="onActions('save')">
					<i class="ri-save-line"></i>
					<span>保存</span>
				</el-button>
				<el-button @click="isEditState = false" class="global-btn-second">
					<i class="ri-close-line"></i>
					<span>取消</span>
				</el-button>
			</div>
			<div v-else style="display: flex;justify-content: space-between;text-align: right;">
				<el-button type="primary" @click="onActions('edit')" class="global-btn-main">
					<i class="ri-edit-box-line"></i>
					<span>编辑</span>
				</el-button>
			</div>
		</div>
		<div>
			<itemForm ref="itemFormRef" :currInfo="currInfo" :isEditState="isEditState" :itemList="itemList"></itemForm>
		</div>
	</y9Card>
	 
	<!-- 弹窗 -->
	<y9Dialog v-model:config="infoDialogConfig">
		
	</y9Dialog>
	
	
</template>

<script lang="ts" setup>
	import { $dataType, $keyNameAssign, $deepAssignObject, } from '@/utils/object.ts'
	import y9_storage from "@/utils/storage";
	import settings from '@/settings.ts';
	import { $dictionaryFunc } from '@/utils/data'
	import { ref } from '@vue/reactivity';
	import itemForm from './itemForm.vue';
	import {saveItem} from '@/api/itemAdmin/item/item';
	const props = defineProps({
		currTreeNodeInfo: {//当前tree节点信息
			type: Object,
			default:() => { return {} }
		},
		itemList:Array,
		updateItem:Function
	})
	const data = reactive({
		
		//当前节点信息
		currInfo:props.currTreeNodeInfo,
		
		//表单
		isEditState: false,//是否为编辑状态
		saveFormBtnLoading:false,//保存按钮加载状态

		//弹窗配置
		infoDialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
					let result = {success:false,msg:''};
					ElNotification({
						title: result.success ? '成功' : '失败',
						message: result.msg,
						type: result.success ? 'success' : 'error',
						duration: 2000,
						offset: 80
					});
					resolve()
				})
			},
			visibleChange:(visible) => {
				console.log('visible',visible)
			}
		},
		itemFormRef:'',
	})
	
	let {
		currInfo,
		isEditState,
		saveFormBtnLoading,
		infoDialogConfig,
		itemFormRef
	} = toRefs(data);
	
	watch(() => props.currTreeNodeInfo,(newVal) => {
			currInfo.value = $deepAssignObject(currInfo.value, newVal);
			isEditState.value = false;
		},
	)
	
	//操作按钮
	async function onActions(type,title) {
		if (type == 'edit') {//编辑
			isEditState.value = true;
		} else if (type == 'save') {//保存
			saveFormBtnLoading.value = true;
			let result = {success:false,msg:''};
			let valid = await itemFormRef.value.validForm();
			if(!valid){
				saveFormBtnLoading.value = false;
				return;
			}
			let formData = itemFormRef.value.itemForm;
			result = await saveItem(JSON.stringify(formData).toString());
			ElNotification({
				title: result.success ? '成功' : '失败',
				message: result.msg,
				type: result.success ? 'success' : 'error',
				duration: 2000,
				offset: 80
			});
			if (result.success) {
				props.currTreeNodeInfo = formData;
				currInfo.value = formData;
				isEditState.value = false;//取消编辑状态 
				props.updateItem();
			}
			saveFormBtnLoading.value = false;
		} else if (type == 'addDepartment') {//点击按钮显示弹窗
			Object.assign(infoDialogConfig.value,{
				show:true,
				title:title,
				resetText:type == 'addPosition' || type == 'addGroup' || type == 'sync'?"重置":false,
				cancelText: type == 'sync'?false:'取消',
				type:type
			})
			
		} else if(type == 'disabled'){
			ElMessageBox.confirm(
				'确定要'+title,
				'提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'info',
			}).then(async () => {
				let res = {success:false,msg:''};
				ElNotification({
					title: res.success ? '成功' : '失败',
					message: res.msg,
					type: res.success ? 'success' : 'error',
					duration: 2000,
					offset: 80
				});
			}).catch(() => {
				ElMessage({
					type: 'info',
					message: '已取消'+title,
					offset: 65
				});
			});
			
		}
	}
	
</script>

<style lang="scss" scoped>
	.slot-header{
		display: flex;
		justify-content: space-between;
		padding: 16px;
		.expand-btns-div {
			display: flex;
			flex-wrap: wrap;
			max-width: 70%;
			.ri-add-box-line,
			.ri-checkbox-indeterminate-line {
				margin-left: 10px;
				color: var(--el-color-primary);
				font-size: 18px;
				cursor: pointer;
				line-height: 30px;
			}
			& > .expand-btns {
				animation: v-bind(expandAddBtnsAnimation) 1.5s;
				animation-fill-mode: forwards;
				flex: 1;
				text-align: right;
				:deep(.el-button){
					margin-bottom: 10px;
					
				}
			}
			.ri-checkbox-indeterminate-line,.ri-add-box-line{
				margin-bottom: 16px;
			}
		}
	}
	
	.oth-btns{
		margin-bottom: 10px;
		:deep(.el-button){
			margin-bottom: 10px;
		}
	}
	
	:deep(.el-descriptions__label){
		width: 14%;
		text-align: center !important;  
	
	}
	
	
	:deep(.el-descriptions__content){
		width: 19.3%;
		// width: 36%;
		.el-select,.el-date-editor{
			width: 100%;
		}
	}
	
	:deep(.el-descriptions__title){
		font-weight: normal;
		text-align: center;
	}
	

</style>