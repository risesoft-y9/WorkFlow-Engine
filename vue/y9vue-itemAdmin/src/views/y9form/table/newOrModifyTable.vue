<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2024-04-23 15:08:39
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-07-16 16:07:14
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-flowable\vue\y9vue-itemAdmin\src\views\y9form\table\newOrModifyTable.vue
-->
<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-07 14:26:45
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-09 18:02:11
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\views\y9form\table\newOrModifyTable.vue
-->
<template>
   <el-dialog
    :title="title"
    custom-class="newOrModifyTable"
    v-model="dialogVisible"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    width="55%"
    >
    <div style="margin: 8px;"
		v-loading="loading"
		element-loading-text="正在处理中"
		element-loading-spinner="el-icon-loading"
		element-loading-background="rgba(0, 0, 0, 0.8)"
	>
		<table border="0" cellpadding="0" cellspacing="1" class="layui-table" lay-skin="line row">
			<tbody>
				<tr>
					<td class="lefttd" style="width: 15%;">表英文名称<font style="color:red;">*</font></td>
					<td class="rigthtd">
						<font style="margin-left: 10px;color:red;">{{prefix}}</font>
						<el-input style="width:82%;" v-model="table.tableName"></el-input>
					</td>
					<td class="lefttd" style="width: 15%;">表中文名称<font style="color:red;">*</font></td>
					<td class="rigthtd" >
						<el-input v-model="table.tableCnName"></el-input>
					</td>
				</tr>
				<tr>
					<td class="lefttd" >表类型</td>
					<td class="rigthtd" >
						<span>主表</span>
					</td>
					<td class="lefttd" >所属系统</td>
					<td class="rigthtd"  >
						<span>{{table.systemCnName}}</span>
					</td>
				</tr>
				<tr>
					<td class="lefttd" >备注</td>
					<td class="rigthtd" colspan="3">
						<el-input type="textarea" :rows="4" placeholder="请输入内容" v-model="table.tableMemo"></el-input>
					</td>
				</tr>
			</tbody>
		</table>
		<el-divider>表字段</el-divider>
		<div style="margin-bottom: 15px;">
			<el-button type="primary" @click="addField"><i class="ri-add-line"></i><span>字段</span></el-button>
			<el-button type="primary" @click="copyField"><i class="ri-file-copy-2-line"></i><span>复制字段</span></el-button>
		</div>
		<el-table border style="width: 100%;" height="300" :data="fieldList">
			<el-table-column label="序号" type="index" align="center" width="60"></el-table-column>
			<el-table-column prop="fieldCnName" label="中文名称" align="center" width="130">
				<template #default="fieldCnName_cell">
					<span>
						<font color='red' title='数据库表中不存在此字段' v-if="fieldCnName_cell.row.state == 0">!</font>
						{{fieldCnName_cell.row.fieldCnName}}
					</span>
				</template>
			</el-table-column>
			<el-table-column prop="fieldName" label="字段名称" align="center" width="auto" min-width="180px"></el-table-column>
			<el-table-column prop="fieldType" label="字段类型" align="center" width="150"></el-table-column>
			<el-table-column prop="isMayNull" label="是否允许为空" align="center" width="130">
				<template #default="isMayNull_cell">
					<font v-if="isMayNull_cell.row.isMayNull == 0">非空</font>
					<font v-if="isMayNull_cell.row.isMayNull == 1">空</font>
				</template>
			</el-table-column>
			<el-table-column prop="optcell" label="操作" align="center" width="180">
				<template #default="scope">
					<template v-if="!(scope.row.opt == 'false' 
						|| scope.row.fieldName == 'guid' || scope.row.fieldName == 'GUID'
						|| scope.row.fieldName == 'parentProcessSerialNumber' || scope.row.fieldName == 'PARENTPROCESSSERIALNUMBER')">
						<i class="ri-edit-line" @click="editField(scope)" title="编辑" style="margin-right: 15px;font-size:18px"></i>
						<i class="ri-delete-bin-line" @click="delField(scope)" title="删除" style="font-size:18px"></i>
					</template>
				</template>
			</el-table-column>
		</el-table>
		<div slot="footer" class="dialog-footer" style="text-align: center;margin-top: 15px;">
			<el-button type="primary" @click="saveY9Table"><i class="ri-save-line"></i><span>保存</span></el-button>
			<el-button type="primary" @click="buildY9Table"><i class="ri-send-plane-line"></i><span>新生成表</span></el-button>
			<el-button type="primary" @click="updateY9Table"><i class="ri-edit-box-line"></i><span>修改表结构</span></el-button>
			<el-button type="primary" @click="dialogVisible = false"><i class="ri-close-line"></i><span>关闭</span></el-button>
		</div>
    </div>
	<y9Dialog v-model:config="dialogConfig">
		<newOrModifyField ref="newOrModifyFieldRef" v-if="dialogConfig.type == 'newOrModify'" :tableId="table.id" :fieldList="fieldList" :pushField="pushTableField" :fieldId="fieldId" :updateField="updateField"/>
		<copyTableField ref="copyTableFieldRef" v-if="dialogConfig.type == 'copyTableField'" :tableId="table.id" :tableList="tableList"/>
	</y9Dialog>
  </el-dialog>
</template>

<script lang="ts" setup>
	import { $deepAssignObject, } from '@/utils/object.ts';
	import newOrModifyField from './newOrModifyField.vue';
	import copyTableField from './copyTableField.vue';
	import {getTable,saveTable,getTableFieldList,checkTableExist,buildTable,updateTable} from '@/api/itemAdmin/y9form';
	const props = defineProps({
		updateList: Function,
		tableList:Array
	})
	const data = reactive({
		dialogVisible:false,
		loading:false,
		title:'',
		table:{tableType:1},
		tableOldName:'',
		prefix:'y9_form_',
		tableNames:'',
		databaseName:'',
		fieldList:[],
		newOrModifyFieldRef:'',
		//弹窗配置
		dialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
					if(dialogConfig.value.type == 'copyTableField'){
						if(copyTableFieldRef.value.fieldArr.length == 0){
							ElNotification({title: '操作提示',message: '请勾选要复制的字段',type: 'error',duration: 2000,offset: 80});
							reject();
						}
						for(let item of copyTableFieldRef.value.fieldArr){
							let add = true;
							for(let field of fieldList.value){
								if(field.fieldName == item.fieldName){
									add = false;
									break;
								}
							}
							if(add){
								item.id = '';
								item.tableId = table.value.id;
								item.tableName = table.value.tableName;
								item.state = 0;
								fieldList.value.push(item);
							}
						}
						resolve();
					}else{
						let valid = await newOrModifyFieldRef.value.validForm();
						if(!valid){
							reject();
							return;
						}
						let res = await newOrModifyFieldRef.value.saveOrModifyField();
						if(res == undefined){
							reject();
							return;
						}
						ElNotification({
							title: res.success ? '成功' : '失败',
							message: res.msg,
							type: res.success ? 'success' : 'error',
							duration: 2000,
							offset: 80
						});
						if(res.success && res.type == undefined){
							let res1 = await getTableFieldList(table.value.id);
							fieldList.value = res1.data;
						}
						resolve()
					}
				})
			},
			visibleChange:(visible) => {
				// console.log('visible',visible)
			}
		},
		updateField:null,
		fieldId:'',
		editIndex:'',
		copyTableFieldRef:''
	});
	let {
		dialogVisible,
		loading,
		title,
		table,
		tableOldName,
		prefix,
		tableNames,
		databaseName,
		fieldList,
		newOrModifyFieldRef,
		dialogConfig,
		updateField,
		fieldId,
		editIndex,
		copyTableFieldRef
	} = toRefs(data);

	defineExpose({ show})
	async function show(y9table,systemCnName,systemName){
		dialogVisible.value = true;
		fieldList.value = [];
		tableOldName.value = '';
		table.value.tableName = "";
		loading.value = true;
		if(y9table == null){
			title.value = "创建业务表";
			table.value = {id:"",systemCnName:systemCnName,systemName:systemName,tableType:1};
		}else{
			title.value = "编辑业务表【"+y9table.tableCnName+"】";
			table.value = y9table;
			tableOldName.value = y9table.tableName;
			let res = await getTableFieldList(table.value.id);
			fieldList.value = res.data;
		}
	 	let res = await getTable(table.value.id);
		loading.value = false;
		if(res.success){
			if(y9table != null){
				table.value = res.data.y9Table;
				if(y9table.tableName.indexOf("y9_form_") > -1 || y9table.tableName.indexOf("Y9_FORM_") > -1){
					prefix.value = y9table.tableName.substring(0,8);
					table.value.tableName = y9table.tableName.substring(8);
				}
			}
			databaseName.value = res.data.databaseName;
			tableNames.value = res.data.tableNames;
		}
		if(y9table == null){
			let type = "varchar";
			if(databaseName.value == "oracle" || "kingbase" == databaseName.value || "dm" == databaseName.value){
				type = "VARCHAR2";
			}
			let field = {id:"",state:0,tableId:"",fieldCnName:"主键",fieldName:"guid",fieldLength:38,fieldType:type + "("+38+")",
							isMayNull:0,isSystemField:1,oldFieldName:"",opt:"false"};
			fieldList.value.push(field);
		}
	};

	function tableTypeChange(val) {
		if(val == 2){//子表
			let has = false;
			for(let item of fieldList.value){
				if(item.fieldName.toUpperCase() == "parentProcessSerialNumber".toUpperCase()){
					has = true;
				}
			}
			if(!has){
				let type = "varchar";
				if(databaseName.value == "oracle" || "kingbase" == databaseName.value || "dm" == databaseName.value){
					type = "VARCHAR2";
				}
				let field = {id:"",state:0,tableId:"",fieldCnName:"主表编号",fieldName:"parentProcessSerialNumber",fieldLength:64,fieldType:type + "("+64+")",
							isMayNull:0,isSystemField:0,oldFieldName:"",opt:"false"};
				fieldList.value.splice(1, 0, field);
			}
		}else if(val == 1){
			fieldList.value.forEach((element,index) => {
				if(element.fieldName.toUpperCase() == "parentProcessSerialNumber".toUpperCase()){
					fieldList.value.splice(index,1);
				}
			});
		}
	}

	async function validForm() {
		let tableName = table.value.tableName;
		let tableName0 = prefix.value + table.value.tableName;
		if (!table.value.tableName) {
			ElNotification({title: '失败',message: '表单验证不通过',type: 'error',duration: 2000,offset: 80});
			return false;
		}
		if (!table.value.tableCnName) {
			ElNotification({title: '失败',message: '表单验证不通过',type: 'error',duration: 2000,offset: 80});
			return false;
		}
		
		let pattern = new RegExp("\\s+");
		let b = pattern.test(tableName);
		if (b) {
			ElNotification({title: '失败',message: '表英文名称不能包含空格',type: 'error',duration: 2000,offset: 80});
			return false;
		}
		pattern = new RegExp("\"|'");
		b = pattern.test(tableName);
		if (b) {
			ElNotification({title: '失败',message: '表英文名称不能包含单双引号',type: 'error',duration: 2000,offset: 80});
			return false;
		}
		pattern = new RegExp("^[a-z|A-Z|_]+");
		b = pattern.test(tableName);
		if (!b) {
			ElNotification({title: '失败',message: '表英文名称必须以字母开头',type: 'error',duration: 2000,offset: 80});
			return false;
		}
		pattern = new RegExp("^[a-z|A-Z|0-9|_|-]+$");
		b = pattern.test(tableName);
		if (!b) {
			ElNotification({title: '失败',message: '表英文名称必须全为字母或者数字',type: 'error',duration: 2000,offset: 80});
			return false;
		}
		let y9TableName = tableNames.value.split(",");
		if(table.value.id == ""){//新增时需要验证表名称是否已存在
		    for(let i = 0; i < y9TableName.length; i++){
				if(tableName0 == y9TableName[i]){
					ElNotification({title: '失败',message: '表英文名称已存在记录，请修改',type: 'error',duration: 2000,offset: 80});
					return false;
				}
			}
		}else{//修改时，如果修改的名称与旧名称一样则不验证,不一样则需验证表名称是否已存在
			if(tableName0 != tableOldName.value){
				for(let i = 0; i < y9TableName.length; i++){
					if(tableName0 == y9TableName[i]){
						ElNotification({title: '失败',message: '表英文名称已存在记录，请修改',type: 'error',duration: 2000,offset: 80});
						return false;
					}
				}
			}
		}
		return true;
	}

	async function saveY9Table(){
		let valid = await validForm();
		if(!valid){
			return;
		}
		let fields = JSON.stringify(fieldList.value).toString();
		table.value.tableName = prefix.value + table.value.tableName;
		let tables = JSON.stringify(table.value).toString();
		loading.value = true;
		let res = await saveTable(tables,fields);
		if(res.success){
			dialogVisible.value = false;
			loading.value = false;
			props.updateList();
		}else{
			loading.value = false;
			table.value.tableName = table.value.tableName.substring(8);
		}
		ElNotification({
			title: res.success ? '成功' : '失败',
			message: res.msg,
			type: res.success ? 'success' : 'error',
			duration: 2000,
			offset: 80
		});
	}
	function addField(){
		fieldId.value = '';
		updateField.value = null;
		Object.assign(dialogConfig.value,{
			show:true,
			width:'35%',
			type:'newOrModify',
			title:'添加字段',
			cancelText: '取消',
		})
	}

	function copyField(){
		fieldId.value = '';
		updateField.value = null;
		Object.assign(dialogConfig.value,{
			show:true,
			width:'45%',
			type:'copyTableField',
			title:'复制字段',
			cancelText: '取消',
		})
	}

	function pushTableField(obj,type) {
		if(type == 'update'){
			fieldList.value.forEach((element,index) => {
				if(index == editIndex.value){
					fieldList.value[index] = obj;
				}
			});
		}else{
			fieldList.value.push(obj);
		}
	}

	function editField(scope){
		editIndex.value = scope.$index;
		fieldId.value = scope.row.id;
		updateField.value = scope.row;
		Object.assign(dialogConfig.value,{
			show:true,
			width:'35%',
			type:'newOrModify',
			title:'编辑字段',
			cancelText: '取消',
		})
	}

	function delField(scope) {
		fieldList.value.splice(scope.$index, 1);
	}

	async function buildY9Table() {
		let valid = await validForm();
		if(!valid){
			return;
		}
		let res = await checkTableExist(prefix.value + table.value.tableName);
		let msg = "将创建新的数据库表，继续吗？";
		if(res.data == "exist"){
			msg = "数据库已存在该表，将删除重新创建，继续吗？";
		}
		ElMessageBox.confirm(
			msg,
			'提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'info',
		}).then(async () => {
			let result = {success:false,msg:''};
			let fields = JSON.stringify(fieldList.value).toString();
			table.value.tableName = prefix.value + table.value.tableName;
			let tables = JSON.stringify(table.value).toString();
			loading.value = true;
			result = await buildTable(tables,fields);
			ElNotification({
				title: result.success ? '成功' : '失败',
				message: result.msg,
				type: result.success ? 'success' : 'error',
				duration: 2000,
				offset: 80
			});
			if(result.success){
				dialogVisible.value = false;
				loading.value = false;
				props.updateList();
			}else{
				loading.value = false;
				table.value.tableName = table.value.tableName.substring(8);
			}
		}).catch(() => {
			ElMessage({
				type: 'info',
				message: '已取消操作',
				offset: 65
			});
		});
	}

	async function updateY9Table() {
		let valid = await validForm();
		if(!valid){
			return;
		}
		ElMessageBox.confirm(
			'确认修改物理数据库表追加字段吗?',
			'提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'info',
		}).then(async () => {
			let result = {success:false,msg:''};
			let fields = JSON.stringify(fieldList.value).toString();
			table.value.tableName = prefix.value + table.value.tableName;
			let tables = JSON.stringify(table.value).toString();
			loading.value = true;
			result = await updateTable(tables,fields);
			ElNotification({
				title: result.success ? '成功' : '失败',
				message: result.msg,
				type: result.success ? 'success' : 'error',
				duration: 2000,
				offset: 80
			});
			if(result.success){
				dialogVisible.value = false;
				loading.value = false;
				props.updateList();
			}else{
				loading.value = false;
				table.value.tableName = table.value.tableName.substring(8);
			}
		}).catch(() => {
			ElMessage({
				type: 'info',
				message: '已取消操作',
				offset: 65
			});
		});
	}
</script>
<style lang="scss">
	.newOrModifyTable .el-dialog__body{
		padding-bottom: 10px;
	}
	.newOrModifyTable .el-divider--horizontal{
		margin: 25px 0;
	}
</style>
<style lang="scss" scoped>
	.layui-table {
		width: 100%;
		border-collapse: collapse;
		border-spacing: 0;

		td {
			position: revert;
			padding: 5px 10px;
			min-height: 32px;
			line-height: 32px;
			font-size: 14px;
			border-width: 1px;
			border-style: solid;
			border-color: #e6e6e6;
			display: table-cell;
			vertical-align: inherit;
		}

		.lefttd {
			
			
			background: #f5f7fa;
			text-align: center;
			// margin-right: 4px;
			width: 14% ;
		}
		
		.rightd{
			display: flex;
			flex-wrap: wrap;
			word-break: break-all;
			white-space: pre-wrap;
		}
		
	}
</style>