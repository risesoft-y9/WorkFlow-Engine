<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-24 17:05:04
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-08-12 17:47:26
 * @FilePath: \workspace-y9boot-9.5.x-vue\y9vue-itemAdmin\src\components\formMaking\views\SecondDev\selectOption.vue
-->
<template>
	<div
		v-loading="loading"
		element-loading-text="拼命加载中"
		element-loading-spinner="el-icon-loading"
		element-loading-background="rgba(0, 0, 0, 0.8)"
		class="selectOption_class"
		>
		<el-table border style="width: 100%;" height="400" :data="dataList" highlight-current-row @current-change="currentOption">
			<el-table-column label="序号" type="index" align="center" width="60"></el-table-column>
			<el-table-column prop="name" label="字典名称" align="center" width="auto"></el-table-column>
			<el-table-column prop="type" label="字典标识" align="center" width="180"></el-table-column>
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
</template>

<script lang="ts" setup>
import {getOptionClassList,getOptionValueList} from "@/api/itemAdmin/optionClass";

const data = reactive({
	loading:false,
	dataList:[],
	optionValueList:[],
	currentOptionRow: null,
});
let {
	loading,
	dataList,
	optionValueList,
	currentOptionRow,
} = toRefs(data);

onMounted(()=>{
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
});

async function	currentOption(val){
	currentOptionRow.value = val;
}

async function optionValue(row){
	optionValueList.value = [];
	let res = await getOptionValueList(row.type);
	if(res.success){
		optionValueList.value = res.data;
	}
}

defineExpose({
	currentOptionRow
})

</script>

<style>
	.selectOption_class{
		box-shadow:none !important;
	}
</style>