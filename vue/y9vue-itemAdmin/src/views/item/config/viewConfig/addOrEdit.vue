<template>
	<div v-if="tableList.length == 0 && optType!='custom'" style="margin-bottom: 10px;text-align: center;color: red;">如无数据表可选择，表单请先绑定数据库字段！</div>
	<el-form ref="viewFormRef" :model="viewFormData" :rules="rules" :inline-message="true" :status-icon="true" label-width="120px">
		<el-form-item v-if="optType!='custom'" prop="tableName" label="数据库表">
			<el-select v-model="viewFormData.tableName" placeholder="请选择" @change="tableChange">
				<el-option v-for="table in tableList" :key="table.id" :label="table.tableCnName+'('+table.tableName+')'" :value="table.tableName">
				</el-option>
			</el-select>
		</el-form-item>
		<el-form-item v-if="optType!='custom'" prop="columnName" label="数据库字段">
			<el-select v-model="viewFormData.columnName" placeholder="请选择"  @change="columnChange">
				<el-option v-for="column in columnList" :key="column.id" :label="column.fieldName+'('+column.fieldCnName+')'" :value="column.fieldName">
				</el-option>
			</el-select>
		</el-form-item>
		<el-form-item v-if="optType=='custom'" prop="columnName" label="字段名称">
			<el-input v-model="viewFormData.columnName" ></el-input>
		</el-form-item>
		<el-form-item prop="disPlayName" label="显示名称">
			<el-input v-model="viewFormData.disPlayName" ></el-input>
		</el-form-item>
		<el-form-item prop="disPlayWidth" label="显示宽度">
			<el-input v-model="viewFormData.disPlayWidth" ></el-input>
		</el-form-item>
		<el-form-item prop="disPlayAlign" label="显示位置">
			<el-select v-model="viewFormData.disPlayAlign" placeholder="请选择">
				<el-option key="left" label="靠左" value="left"></el-option>
				<el-option key="center" label="居中" value="center"></el-option>
				<el-option key="right" label="靠右" value="right"></el-option>
			</el-select>
		</el-form-item>

		<el-form-item v-if="optType=='table'" prop="openSearch" label="开启搜索条件">
			<el-switch v-model="viewFormData.openSearch" inline-prompt active-text="开启" inactive-text="关闭" @change="switchchange" :active-value=1 :inactive-value=0 />
		</el-form-item>

		<el-form-item v-if="optType=='table' && viewFormData.openSearch == '1'" prop="inputBoxType" label="输入框类型">
			<el-select v-model="viewFormData.inputBoxType" placeholder="请选择">
				<el-option key="search" label="文本输入框(带搜索图标)" value="search"></el-option>
				<el-option key="input" label="文本输入框" value="input"></el-option>
				<el-option key="select" label="下拉框" value="select"></el-option>
				<el-option key="date" label="日期" value="date"></el-option>
			</el-select>
		</el-form-item>

		<el-form-item v-if="optType=='table' && viewFormData.openSearch == '1'" prop="spanWidth" label="搜索框宽度">
			<el-input v-model="viewFormData.spanWidth" ></el-input>
		</el-form-item>

		<el-form-item v-if="optType=='table' && viewFormData.openSearch == '1'" prop="labelName" label="搜索名称">
			<el-input v-model="viewFormData.labelName" ></el-input>
		</el-form-item>

		<el-form-item v-if="optType=='table' && viewFormData.inputBoxType == 'select' && viewFormData.openSearch == '1'" prop="optionClass" label="数据字典">
			<el-input v-model="viewFormData.optionClass" :readonly="true" @click="selectOption"></el-input>
		</el-form-item>
	</el-form>

	<y9Dialog v-model:config="dialogConfig">
		<selectOptionDialog ref="selectOptionRef" />
	</y9Dialog>
</template>

<script lang="ts" setup>
  import { $deepAssignObject } from '@/utils/object.ts';
  import {getColumns,getViewInfo} from "@/api/itemAdmin/item/viewConfig";
  import selectOptionDialog from './selectOption.vue'
  const props = defineProps({
      currTreeNodeInfo: {//当前tree节点信息
        type: Object,
        default:() => { return {} }
      },
      id:String,
	  optType:String,
	  viewType:String
    })
	
	const data = reactive({
		//当前节点信息
		currInfo:props.currTreeNodeInfo,
		viewFormRef:'',
		viewFormData:{
			itemId:props.currTreeNodeInfo.id,
			viewType:props.viewType,
			tableName:'',
			columnName:'',
			disPlayName:'',
			disPlayWidth:'',
			disPlayAlign:'',
			openSearch:'0',
			inputBoxType:'',
			spanWidth:'',
			labelName:'',
			optionClass:'',
		},
		tableList:[],
		columnList:[],
		tablefield:[],
		rules: {
			tableName:{ required: true,message:'请选择业务表' },
			columnName:{ required: true,message:'请输入或者选择字段名称' },
			disPlayName:{ required: true,message:'请输入显示名称' },
			disPlayWidth:{ required: true,message:'请输入显示宽度' },
			disPlayAlign:{ required: true,message:'请选择显示位置' }
		},
		//弹窗配置
		dialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
					console.log(selectOptionRef.value.currentOptionRow);
					if(selectOptionRef.value.currentOptionRow == null){
						ElMessage({type: 'error',message: '请选择数据字典',offset: 65});
						reject();
					}else{
						let optionClass = selectOptionRef.value.currentOptionRow;
						viewFormData.value.optionClass = optionClass.type + "("+optionClass.name+")";
						resolve();
					}
				})
			},
			visibleChange:(visible) => {
			}
		},
		selectOptionRef:'',
	})
	
	let {
		currInfo,
		selectOptionRef,
		dialogConfig,
		viewFormRef,
		viewFormData,
		tableList,
		columnList,
		rules,
		tablefield,
	} = toRefs(data);
	
	watch(() => props.id,(newVal) => {
    	getViewInfoDetail();
	})

  	onMounted(()=>{
		getViewInfoDetail();
	});

	function getViewInfoDetail(){
		getViewInfo(props.id,props.currTreeNodeInfo.id).then(res => {
			tableList.value = res.data.tableList;
			tablefield.value = res.data.tablefield;
			if(props.id != ''){
				viewFormData.value = res.data.itemViewConf;
				if(viewFormData.value.tableName != null){
					tableChange(viewFormData.value.tableName);
				}
			}
      	});
	}

	function tableChange(val){
		// getColumns(val,props.currTreeNodeInfo.id).then(res => { 
		// 	columnList.value = res.data;
      	// });
		tablefield.value.forEach(element => {
			if(element.tableName == val){
				columnList.value = element.fieldlist;
			}
		});
	}

	function columnChange(val){
   		let obj = {};
		obj = columnList.value.find((item)=>{
			return item.fieldName === val;
		});
   		viewFormData.value.disPlayName = obj.fieldCnName;
	}

	function switchchange(val) {
		if(val == 1){
			rules.value.inputBoxType = { required: true,message:'请选择输入框类型'};
		}else{
			rules.value.inputBoxType = {};
		}
	}

	function selectOption() {
		Object.assign(dialogConfig.value,{
			show:true,
			width:'30%',
			title:'字典选择',
			showFooter:true
		});
	}

	defineExpose({
		viewFormRef,
		viewFormData,
	})

</script>

<style>
	
</style>