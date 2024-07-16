<template>
	<el-select v-model="seltableId" style="margin-bottom: 10px;width: 50%;" @change="tableChange">
		<template v-for="item in tableList">
			<el-option v-if="item.id != tableId" :key="item.id" :label="item.tableName+'('+item.tableCnName+')'" :value="item.id">
			</el-option>
		</template>
	</el-select>
	<y9Table :config="tableConfig" @select-all="handlerSelectData" @select="handlerSelectData">
	</y9Table>
</template>

<script lang="ts" setup>
  import { $deepAssignObject, } from '@/utils/object.ts'
  import {getTableFieldList} from '@/api/itemAdmin/y9form';
  const props = defineProps({
	  tableList:Array,
	  tableId:String
    })
	
	const data = reactive({
		seltableId:'',
		tableConfig: {
			columns: [
				{ title: '', type: 'selection', fixed: 'left',width: '50' },
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{
					title: "字段名称",
					key: "fieldName",
				},
				{
					title: "中文名称",
					key: "fieldCnName",
				},
				{
					title: "字段类型",
					key: "fieldType",
					width: '150',
				},
				{
					title: "是否允许为空",
					key: "isMayNull",
					width: '140',
					render:(row) =>{
						if(row.isMayNull == 1){
							return '空';
						}else if(row.isMayNull == 0){
							return '非空';
						}
					}
				}
			],
			tableData: [],
			pageConfig:false,//取消分页
		},
		fieldArr:[]
	})
	
	let {
		seltableId,
		tableConfig,
		fieldArr,
	} = toRefs(data);
	
  async function tableChange(val){
	let result = await getTableFieldList(val);
    if(result.success){
		tableConfig.value.tableData = result.data;
    }
  }

  defineExpose({
	fieldArr
  })

  function handlerSelectData(id, data){
	fieldArr.value = id;
  }
	
</script>

<style>
  	.permconfig .el-dialog__body{
		  padding: 5px 10px;
  	}

	.el-popper.is-customized {
	/* Set padding to ensure the height is 32px */
	padding: 6px 12px;
	background: linear-gradient(90deg, rgb(159, 229, 151), rgb(204, 229, 129));
	}

	.el-popper.is-customized .el-popper__arrow::before {
	background: linear-gradient(45deg, #b2e68d, #bce689);
	right: 0;
	}
</style>