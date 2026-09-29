<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-24 17:05:04
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-08-05 16:37:22
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\components\formMaking\views\SecondDev\selectField.vue
-->
<template>
    <y9Dialog v-model:config="dialogConfig">
		<div
			v-loading="loading"
			element-loading-text="拼命加载中"
			element-loading-spinner="el-icon-loading"
			element-loading-background="rgba(0, 0, 0, 0.8)">
			<el-table border style="width: 100%;" height="400" :data="fieldList" highlight-current-row @current-change="currentField">
				<el-table-column label="序号" type="index" align="center" width="60"></el-table-column>
				<el-table-column prop="fieldName" label="字段名称" align="center" width="auto"></el-table-column>
				<el-table-column prop="fieldCnName" label="中文名称" align="center" width="150"></el-table-column>
			</el-table>
		</div>
  	</y9Dialog>
</template>

<script lang="ts" setup>
import {getTableFieldList} from "@/api/itemAdmin/y9form";

const props = defineProps({
	bindField: Function,
})

const data = reactive({
	loading:false,
	fieldList:[],
	//弹窗配置
	dialogConfig: {
		show: false,
		title: "",
		onOkLoading: true,
		onOk: (newConfig) => {
			return new Promise(async (resolve, reject) => {
				if(currentFieldRow.value == null){
					ElNotification({title: '失败',message: '请选择表字段',type: 'error',duration: 2000,offset: 80});
					reject();
					return;
				}
				props.bindField(currentFieldRow.value);
				resolve()
			})
		},
		visibleChange:(visible) => {
			// console.log('visible',visible)
		}
	},
	currentFieldRow:null,
});
let {
	loading,
	fieldList,
	dialogConfig,
	currentFieldRow,
} = toRefs(data);

defineExpose({ show});

async function show(tableId){
	currentFieldRow.value = null;
	Object.assign(dialogConfig.value,{
		show:true,
		width:'25%',
		title:'字段绑定',
		cancelText: '取消',
	});
	setTimeout(async () => {
		if(fieldList.value == undefined || fieldList.value.length == 0){
			loading.value = true;
			let res = await getTableFieldList(tableId);
			loading.value = false;
			if(res.success){
				fieldList.value = res.data;
			}
		}
	}, 500);
}

async function	currentField(val){
	currentFieldRow.value = val;
}
</script>

<style>
  	.selectField .el-dialog__body{
		padding: 5px 10px;
  	}
	.selectField .el-divider__text{
		font-size: 18px;
	}
	.selectField .el-main{
		padding: 0;
	}
</style>