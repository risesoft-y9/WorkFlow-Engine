<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-24 17:05:04
 * @LastEditors: qinman
 * @LastEditTime: 2023-12-01 18:20:10
 * @FilePath: \y9-vue\y9vue-itemAdmin\src\components\formMaking\components\SecondDev\selectOpinionFrame.vue
-->
<template>
   <y9Dialog v-model:config="dialogConfig">
	<div
		v-loading="loading"
		element-loading-text="拼命加载中"
		element-loading-spinner="el-icon-loading"
		element-loading-background="rgba(0, 0, 0, 0.8)">
		<div style="margin-bottom: 10px;text-align: left;">
			<el-input style="width:200px;margin-right: 5px;" v-model="keyword" placeholder="意见框名称"></el-input>
			<el-button type="primary" @click="reloadTable"><i class="ri-search-line"></i>搜索</el-button>
		</div>
		<y9Table :config="tableConfig" @on-curr-page-change="onCurrPageChange" @on-page-size-change="onPageSizeChange" @on-current-change="onCurrentChange"></y9Table>
	</div>
  </y9Dialog>
</template>

<script lang="ts" setup>
import {searchOpinionFrame} from "@/api/itemAdmin/opinionFrame";

const props = defineProps({
	bindOpinionFrame: Function,
})

const data = reactive({
	loading:false,
	currentRow: null,
	total:0,
	keyword:"",
	//弹窗配置
	dialogConfig: {
		show: false,
		title: "",
		onOkLoading: true,
		onOk: (newConfig) => {
			return new Promise(async (resolve, reject) => {
				if(currentRow.value == null){
					ElNotification({title: '失败',message: '请选择意见框',type: 'error',duration: 2000,offset: 80});
					reject();
					return;
				}
				props.bindOpinionFrame(currentRow.value);
				resolve()
			})
		},
		visibleChange:(visible) => {
			// console.log('visible',visible)
		}
	},
	tableConfig: {//表格配置
		columns: [
			{
				title: "序号",
				type:"index",
				width: '80'
			},
			{
				title: "意见框名称",
				key: "name",
			},
			{
				title: "意见框标识",
				key: "mark",
			}
		],
		tableData: [],
		pageConfig: {
          currentPage: 1, //当前页数，支持 v-model 双向绑定
          pageSize: 15, //每页显示条目个数，支持 v-model 双向绑定
          total: 0, //总条目数
        },
	},
});
let {
	loading,
	dialogConfig,
	tableConfig,
	currentRow,
	total,
	keyword,
} = toRefs(data);

defineExpose({ show});

async function show(){
	currentRow.value = null;
	Object.assign(dialogConfig.value,{
		show:true,
		width:'25%',
		title:'意见框绑定',
		cancelText: '取消',
	});
	setTimeout(async () => {
		reloadTable();
	}, 500);
}

async function reloadTable(){//获取列表
	loading.value = true;
	let page = tableConfig.value.pageConfig.currentPage;
    let rows = tableConfig.value.pageConfig.pageSize;
	let res = await searchOpinionFrame(page,rows,keyword.value);
	loading.value = false;
	if(res.success){
		tableConfig.value.tableData = res.rows;
    	tableConfig.value.pageConfig.total = res.total;
	}
}

//当前页改变时触发
function onCurrPageChange(currPage) {
    tableConfig.value.pageConfig.currentPage = currPage;
    reloadTable();
}
//每页条数改变时触发
function onPageSizeChange(pageSize) {
    tableConfig.value.pageConfig.pageSize = pageSize;
    reloadTable();
}

async function onCurrentChange(val){
	currentRow.value = val;
}
</script>

<style>
  	.selectOpinionFrame .el-dialog__body{
		padding: 5px 10px;
  	}
	.selectOpinionFrame .el-divider__text{
		font-size: 18px;
	}
	.selectOpinionFrame .el-main{
		padding: 0;
	}
</style>