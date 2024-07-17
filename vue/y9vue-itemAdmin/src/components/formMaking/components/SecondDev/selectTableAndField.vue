<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-24 17:05:04
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-07-16 16:02:36
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-flowable\vue\y9vue-itemAdmin\src\components\formMaking\components\SecondDev\selectTableAndField.vue
-->
<template>
   <y9Dialog v-model:config="dialogConfig">
	<div
		v-loading="loading"
		element-loading-text="拼命加载中"
		element-loading-spinner="el-icon-loading"
		element-loading-background="rgba(0, 0, 0, 0.8)">
		<el-container >
			<el-aside style="width:48%;">
				<el-divider>业务表</el-divider>
				<el-table ref="tableRef" border style="width: 100%;" height="400" :data="tableList"  highlight-current-row @current-change="currentTable">
					<el-table-column label="序号" type="index" align="center" width="60"></el-table-column>
					<el-table-column prop="tableCnName" label="中文名称" align="center" width="auto"></el-table-column>
					<el-table-column prop="tableName" label="表名称" align="center" width="auto"></el-table-column>
					<el-table-column prop="tableType" label="表类型" align="center" width="80">
						<template #default="tableType_cell">
						<font v-if="tableType_cell.row.tableType == 1">主表</font>
						<font v-if="tableType_cell.row.tableType == 2">子表</font>
						<font v-if="tableType_cell.row.tableType == 3">字典</font>
						</template>
					</el-table-column>
				</el-table>
			</el-aside>
			<el-main style="width:48%;margin-left: 4%;">
				<el-divider>表字段</el-divider>
				<el-table border style="width: 100%;" height="400" :data="fieldList" highlight-current-row @current-change="currentField"
					v-loading="loading1"
					element-loading-text="拼命加载中"
					element-loading-spinner="el-icon-loading"
					element-loading-background="rgba(0, 0, 0, 0.8)">
					<el-table-column label="序号" type="index" align="center" width="60"></el-table-column>
					<el-table-column prop="fieldName" label="字段名称" align="center" width="auto"></el-table-column>
					<el-table-column prop="fieldCnName" label="中文名称" align="center" width="auto"></el-table-column>
				</el-table>
			</el-main>
		</el-container>
	</div>
  </y9Dialog>
</template>

<script lang="ts" setup>
import {getTables,getTableFieldList} from "@/api/itemAdmin/y9form";
const props = defineProps({
	bindField: Function,
})
const data = reactive({
	loading:false,
	loading1:false,
	tableList:[],
	fieldList:[],
	tableRef:'',
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
	currentTableRow:null,
	currentFieldRow:null,
});
let {
	loading,
	loading1,
	fieldList,
	tableList,
	dialogConfig,
	currentTableRow,
	currentFieldRow,
	tableRef,
} = toRefs(data);

defineExpose({ show});

async function show(systemName){
	currentFieldRow.value = null;
	Object.assign(dialogConfig.value,{
		show:true,
		width:'60%',
		title:'字段绑定',
		cancelText: '取消',
	});
	setTimeout(async () => {
		if(tableList.value == undefined || tableList.value.length == 0){
			loading.value = true;
			let res = await getTables(systemName,1,50);
			loading.value = false;
			if(res.success){
				for(let item of res.rows){
					if(item.tableType == 1){
						tableList.value.push(item);
					}
				}
			}
		}else{
			if(currentTableRow != null){
				tableRef.value.setCurrentRow(currentTableRow.value);
			}
		}
	}, 500);
}

async function currentTable(val){
	if(currentTableRow.value == val){
		return;
	}
	currentTableRow.value = val;
	if(currentTableRow.value != null){
		loading1.value = true;
		let res = await getTableFieldList(currentTableRow.value.id);
		loading1.value = false;
		if(res.success){
			fieldList.value = res.data;
		}
	}
}

async function currentField(val){
	currentFieldRow.value = val;
}
</script>

<style>
  	.selectTableAndField .el-dialog__body{
		padding: 5px 10px;
  	}
	.selectTableAndField .el-divider__text{
		font-size: 18px;
	}
	.selectTableAndField .el-main{
		padding: 0;
	}
</style>