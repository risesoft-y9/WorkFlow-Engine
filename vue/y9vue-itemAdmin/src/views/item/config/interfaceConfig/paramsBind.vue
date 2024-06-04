<template>
	<el-form ref="formRef" :model="formData" :rules="rules" :inline-message="true" :status-icon="true" label-width="120px">
		<el-form-item prop="tableName" label="数据库表">
			<el-select v-model="formData.tableName" placeholder="请选择" @change="tableChange">
				<el-option v-for="table in tableList" :key="table.id" :label="table.tableCnName+'('+table.tableName+')'" :value="table.tableName">
				</el-option>
			</el-select>
		</el-form-item>
		<el-form-item prop="columnName" label="数据库字段">
			<el-select v-model="formData.columnName" placeholder="请选择">
				<el-option v-for="column in columnList" :key="column.id" :label="column.fieldName+'('+column.fieldCnName+')'" :value="column.fieldName">
				</el-option>
			</el-select>
		</el-form-item>
		<el-form-item prop="parameterName" label="参数名称">
			<el-select v-model="formData.parameterName" placeholder="请选择" @change="paramsChange">
				<el-option v-for="params in paramsList" :key="params.id" :label="params.parameterName" :value="params.parameterName"></el-option>
			</el-select>
		</el-form-item>
	</el-form>
</template>

<script lang="ts" setup>
  import { $deepAssignObject } from '@/utils/object.ts';
  import {getBindInfo} from "@/api/itemAdmin/item/interfaceConfig";
  import {findRequestParamsList,findResponseParamsList} from "@/api/itemAdmin/interface";
  const props = defineProps({
		interface:{
			type: Object,
			default:() => { return {} }
		},
		row:{
			type: Object,
			default:() => { return {} }
		},
		activeName:String,
	})
	
	const data = reactive({
		formRef:'',
		formData:{
			id:'',
			itemId:props.interface.itemId,
			tableName:'',
			columnName:'',
			parameterName:'',
			parameterType:'',
			bindType:props.activeName,
			interfaceId:props.interface.interfaceId
		},
		tableList:[],
		columnList:[],
		tablefield:[],
		rules: {
			tableName:{ required: true,message:'请选择业务表' },
			columnName:{ required: true,message:'请输入或者选择字段名称' },
			parameterName:{ required: true,message:'请选择参数' },
		},
		paramsList:[],
	})
	
	let {
		formRef,
		formData,
		tableList,
		columnList,
		rules,
		tablefield,
		paramsList,
	} = toRefs(data);
	
  	onMounted(async ()=>{
		getInfoDetail();
		if(props.activeName == 'Request'){
			let res = await findRequestParamsList("","",props.interface.interfaceId);
			paramsList.value = res.data;
		}else if(props.activeName == 'Response'){
			let res = await findResponseParamsList("",props.interface.interfaceId);
			paramsList.value = res.data;
		}
		if(formRef != null){
			formRef.value.clearValidate();
		}
	});

	function getInfoDetail(){
		getBindInfo(props.interface.itemId,props.row.id).then(res => {
			tableList.value = res.data.tableList;
			tablefield.value = res.data.tablefield;
			if(props.row.id != undefined){
				formData.value = res.data.info;
				if(formData.value.tableName != null){
					tableChange(formData.value.tableName);
				}
			}
      	});
	}

	function tableChange(val){
		tablefield.value.forEach(element => {
			if(element.tableName == val){
				columnList.value = element.fieldlist;
			}
		});
	}

	function paramsChange(val) {
		paramsList.value.forEach(element => {
			if(element.parameterName == val && element.parameterType != undefined){
				formData.value.parameterType = element.parameterType;
			}
		});
	}

	defineExpose({
		formRef,
		formData,
	})

</script>

<style>
	
</style>