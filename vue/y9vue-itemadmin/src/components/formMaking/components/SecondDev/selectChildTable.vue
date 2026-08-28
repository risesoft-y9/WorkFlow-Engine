<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-24 17:05:04
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-08-05 16:29:31
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\components\formMaking\views\SecondDev\selectChildTable.vue
-->
<template>
    <y9Dialog v-model:config="dialogConfig">
		<div
			v-loading="loading"
			element-loading-text="拼命加载中"
			element-loading-spinner="el-icon-loading"
			element-loading-background="rgba(0, 0, 0, 0.8)">
			<el-table border ref="tableRef" style="width: 100%;" height="400" :data="tableList"  highlight-current-row @current-change="currentTable">
				<el-table-column label="序号" type="index" align="center" width="60"></el-table-column>
				<el-table-column prop="tableCnName" label="中文名称" align="center" width="auto"></el-table-column>
				<el-table-column prop="tableName" label="表名称" align="center" width="auto"></el-table-column>
				<el-table-column prop="tableType" label="表类型" align="center" width="100">
					<template #default="tableType_cell">
					<font v-if="tableType_cell.row.tableType == 1">主表</font>
					<font v-if="tableType_cell.row.tableType == 2">子表</font>
					</template>
				</el-table-column>
			</el-table>
		</div>
  	</y9Dialog>
</template>

<script lang="ts" setup>
import {getTables} from "@/api/itemAdmin/y9form";
const props = defineProps({
	bindTable: Function,
})

const data = reactive({
	loading:false,
	tableList:[],
	//弹窗配置
	dialogConfig: {
		show: false,
		title: "",
		onOkLoading: true,
		onOk: (newConfig) => {
			return new Promise(async (resolve, reject) => {
				if(currentTableRow.value == null){
					ElNotification({title: '失败',message: '请选择子表',type: 'error',duration: 2000,offset: 80});
					reject();
					return;
				}
				props.bindTable(currentTableRow.value);
				resolve()
			})
		},
		visibleChange:(visible) => {
			// console.log('visible',visible)
		}
	},
	currentTableRow:null,
});
let {
	loading,
	tableList,
	dialogConfig,
	currentTableRow,
} = toRefs(data);

defineExpose({ show});

async function show(systemName){
	currentTableRow.value = null;
	Object.assign(dialogConfig.value,{
		show:true,
		width:'35%',
		title:'字表绑定',
		cancelText: '取消',
	});
	setTimeout(async () => {
		if(tableList.value == undefined || tableList.value.length == 0){
			loading.value = true;
			let res = await getTables(systemName,1,50);
			loading.value = false;
			if(res.success){
				for(let item of res.rows){
					if(item.tableType == 2){
						tableList.value.push(item);
					}
				}
			}
		}
	}, 500);
}

async function currentTable(val){
	currentTableRow.value = val;
}

</script>

<style>
  	.selectChildTable .el-dialog__body{
		padding: 5px 10px;
  	}
	.selectChildTable .el-divider__text{
		font-size: 18px;
	}
	.selectChildTable .el-main{
		padding: 0;
	}
</style>