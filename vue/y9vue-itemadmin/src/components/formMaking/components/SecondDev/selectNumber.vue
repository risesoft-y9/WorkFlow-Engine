<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-24 17:05:04
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-09-27 15:25:13
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\components\formMaking\views\SecondDev\selectNumber.vue
-->
<template>
  <y9Dialog v-model:config="dialogConfig">
	<div
		v-loading="loading"
		element-loading-text="拼命加载中"
		element-loading-spinner="el-icon-loading"
		element-loading-background="rgba(0, 0, 0, 0.8)">
		<!-- <div style="margin: 8px 0;text-align: left;">
			<el-input size="small" style="width:200px;margin-right: 5px;" v-model="keyword" placeholder="编号名称"></el-input>
			<el-button type="primary" size="small" icon="el-icon-search" @click="reloadTable">搜索</el-button>
		</div> -->
		<el-table border style="width: 100%;" height="400" :data="dataList" highlight-current-row @current-change="handleCurrentChange">
			<el-table-column label="序号" type="index" align="center" width="60"></el-table-column>
			<el-table-column prop="name" label="编号名称" align="center" width="auto"></el-table-column>
			<el-table-column prop="custom" label="编号标识" align="center" width="auto"></el-table-column>
		</el-table>
	</div>
  </y9Dialog>
</template>

<script lang="ts" setup>
import {organWordApi} from "@/api/itemAdmin/organWord";
const props = defineProps({
	bindNumber: Function,
})

const data = reactive({
	loading:false,
	dataList:[],
	currentRow: null,
	//弹窗配置
	dialogConfig: {
		show: false,
		title: "",
		onOkLoading: true,
		onOk: (newConfig) => {
			return new Promise(async (resolve, reject) => {
				if(currentRow.value == null){
					ElNotification({title: '失败',message: '请选择编号',type: 'error',duration: 2000,offset: 80});
					reject();
					return;
				}
				props.bindNumber(currentRow.value);
				resolve()
			})
		},
		visibleChange:(visible) => {
			// console.log('visible',visible)
		}
	},
});
let {
	loading,
	dialogConfig,
	dataList,
	currentRow,
} = toRefs(data);

defineExpose({ show});

async function show(){
	currentRow.value = null;
	Object.assign(dialogConfig.value,{
		show:true,
		width:'25%',
		title:'编号绑定',
		cancelText: '取消',
	});
	setTimeout(async () => {
		if(dataList.value == undefined || dataList.value.length == 0){
			loading.value = true;
			let res = await organWordApi.organWordList();
			loading.value = false;
			if(res.success){
				dataList.value = res.data;
			}
		}
	}, 500);
}

async function handleCurrentChange(val){
	currentRow.value = val;
}

</script>

<style>
  	.selectNumber .el-dialog__body{
		padding: 5px 10px;
  	}
	.selectNumber .el-divider__text{
		font-size: 18px;
	}
	.selectNumber .el-main{
		padding: 0;
	}
</style>