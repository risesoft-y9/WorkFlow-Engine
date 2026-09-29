<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-24 17:05:04
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-06-27 15:57:02
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\components\formMaking\components\SecondDev\selectOption.vue
-->
<template>
   <y9Dialog v-model:config="dialogConfig">
	<div
		v-loading="loading"
		element-loading-text="拼命加载中"
		element-loading-spinner="el-icon-loading"
		element-loading-background="rgba(0, 0, 0, 0.8)">
		<el-table border style="width: 100%;" height="400" :data="dataList" highlight-current-row @current-change="currentOption">
			<el-table-column label="序号" type="index" align="center" width="60"></el-table-column>
			<el-table-column prop="name" label="字典名称" align="center" width="auto"></el-table-column>
			<el-table-column prop="type" label="字典标识" align="center" width="auto"></el-table-column>
			<el-table-column prop="opt" label="数据字典" align="center" width="150">
				<template #default="opt_cell">
					<el-button type="primary" link @click="optionValue(opt_cell.row)"><i class="ri-price-tag-2-line"></i>数据字典</el-button>
				</template>
			</el-table-column>
		</el-table>
	</div>
	<el-dialog
        width="25%"
        title="数据字典"
        custom-class="optionValue"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        v-model="innerVisible"
        append-to-body>
          <div>
            <el-table border style="width: 100%;" height="400" :data="optionValueList">
				<el-table-column label="序号" type="index" align="center" width="60"></el-table-column>
				<el-table-column prop="name" label="数据名称" align="center" width="auto"></el-table-column>
				<el-table-column prop="code" label="数据代码" align="center" width="auto"></el-table-column>
			</el-table>
          </div>
        </el-dialog>
  </y9Dialog>
</template>

<script lang="ts" setup>
import {getOptionClassList,getOptionValueList} from "@/api/itemAdmin/optionClass";

const props = defineProps({
	bindOption: Function,
})

const data = reactive({
	loading:false,
	innerVisible:false,
	dataList:[],
	optionValueList:[],
	currentOptionRow: null,
	//弹窗配置
	dialogConfig: {
		show: false,
		title: "",
		onOkLoading: true,
		onOk: (newConfig) => {
			return new Promise(async (resolve, reject) => {
				if(currentOptionRow.value == null){
					ElNotification({title: '失败',message: '请选择数据字典',type: 'error',duration: 2000,offset: 80});
					reject();
					return;
				}
				props.bindOption(currentOptionRow.value);
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
	innerVisible,
	dialogConfig,
	dataList,
	optionValueList,
	currentOptionRow,
} = toRefs(data);

defineExpose({ show});

async function show(){
	currentOptionRow.value = null;
	Object.assign(dialogConfig.value,{
		show:true,
		width:'35%',
		title:'数据字典绑定',
		cancelText: '取消',
	});
	setTimeout(async () => {
		if(dataList.value == undefined || dataList.value.length == 0){
			loading.value = true;
			let res = await getOptionClassList();
			loading.value = false;
			if(res.success){
				dataList.value = res.data;
			}
		}
	}, 500);
}

async function	currentOption(val){
	currentOptionRow.value = val;
}

async function optionValue(row){
	innerVisible.value = true;
	optionValueList.value = [];
	let res = await getOptionValueList(row.type);
	if(res.success){
		optionValueList.value = res.data;
	}
}
</script>

<style>
  	.selectOption .el-dialog__body{
		padding: 5px 10px;
  	}
	.selectOption .el-divider__text{
		font-size: 18px;
	}
	.selectOption .el-main{
		padding: 0;
	}
	.optionValue .el-dialog__body{
		padding: 5px 10px;
  	}
</style>