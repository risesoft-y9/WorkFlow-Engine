<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-05-24 17:05:04
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-06-12 10:21:56
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-flowable\vue\y9vue-itemAdmin\src\components\formMaking\components\SecondDev\permissionConfig.vue
-->
<template>
    <y9Dialog v-model:config="dialogConfig" 
		v-loading="loading"
		class="permissionConfig"
		ent-loading-spinner="el-icon-loading"
		elem element-loading-text="拼命加载中"
		element-loading-background="rgba(0, 0, 0, 0.8)">
		<el-table ref="nodetable" border style="width: 100%;" height="350" :data="listData">
			<el-table-column prop="taskDefName" label="流程节点名称" align="center" width="200"></el-table-column>
			<el-table-column prop="id" label="绑定权限" align="center" width="150">
				<template #default="cell">
					<font v-if="cell.row.id != ''">写权限</font>
				</template>
			</el-table-column>
			<el-table-column prop="writeRoleName" label="绑定角色" align="center" width="auto"></el-table-column>
			<el-table-column prop="opt1" label="操作" align="center" width="300">
				<template #default="opt1_cell">
					<el-button-group>
						<el-button size="mini" @click="saveFieldPerm(opt1_cell.row)">写权限</el-button>
						<el-button size="mini" v-if="opt1_cell.row.id != ''" @click="delPerm(opt1_cell.row)">删除</el-button>
						<el-button size="mini" v-if="opt1_cell.row.id != ''" @click="bindRole(opt1_cell.row)">绑定角色</el-button>
						<el-button size="mini" v-if="opt1_cell.row.writeRoleName != '' && opt1_cell.row.writeRoleName != null" @click="delRole(opt1_cell.row)">删除角色</el-button>
					</el-button-group>
				</template>
			</el-table-column>
		</el-table> 
		<el-drawer v-model="treeDrawer" direction='rtl' title="角色选择">
     		<permTree ref="permTreeRef" :showHeader="false" :treeApiObj="treeApiObj" :selectField="selectField" @onCheckChange="onCheckChange"/>
			<div slot="footer" class="dialog-footer" style="text-align: center;margin-top: 15px;">
				<el-button type="primary" @click="saveInfo"><span>保存</span></el-button>
				<el-button  @click="treeDrawer = false"><span>取消</span></el-button>
			</div>
		</el-drawer>
  </y9Dialog>
</template>

<script lang="ts" setup>
import {getBpmList,saveRoleChoice,deleteRole,saveNodePerm,delNodePerm} from "@/api/itemAdmin/y9form_fieldPerm";
import {getRole,getRoleById} from "@/api/itemAdmin/item/permConfig";
const props = defineProps({
	bindOption: Function,
})
const emits = defineEmits(['refresh'])
const data = reactive({
	permTreeRef:'',
	loading:false,
	loading1:false,
	treeDrawer:false,
	listData:[],
	formId:'',
	fieldName: '',
	taskDefKey:'',
	//弹窗配置
	dialogConfig: {
		show: false,
		title: "",
		onOkLoading: true,
		onOk: (newConfig) => {},
		visibleChange:(visible) => {
			if(!visible){
				emits("refresh");
			}
		}
	},
	treeApiObj:{//tree接口对象
		topLevel: getRole,
		childLevel:{//子级（二级及二级以上）tree接口
			api:getRoleById,
			params:{treeType:'Role'}
		},
		search:{
			api:'',
			params:{
				key:'',
				treeType:''
			}
		},
	},
	treeSelectedData:[],
	selectField: [
		//设置需要选择的字段
		{
			fieldName: 'orgType',
			value: ['role'],
		},
	],
});
let {
	loading,
	treeDrawer,
	dialogConfig,
	listData,
	formId,
	fieldName,
	taskDefKey,
	treeApiObj,
	treeSelectedData,
	permTreeRef,
	selectField
} = toRefs(data);

defineExpose({ show});

async function show(form_Id,field_Name){
	formId.value = form_Id;
	fieldName.value = field_Name
	Object.assign(dialogConfig.value,{
		show:true,
		width:'50%',
		title:'字段权限配置',
		cancelText: '取消',
		showFooter:false
	});
	setTimeout(async () => {
		reloadTable();
	}, 500);
}
async function reloadTable(){//获取列表
	loading.value = true;
	let res = await getBpmList(formId.value,fieldName.value);
	loading.value = false;
	listData.value = res.data;
	if(listData.value[0] && listData.value[0].taskDefName == "流程"){
		listData.value.shift();
	}
}
async function bindRole(row){//绑定角色
	taskDefKey.value = row.taskDefKey;
	treeDrawer.value = true;
	treeSelectedData.value = [];
	setTimeout(() => {
		permTreeRef.value.onRefreshTree();
	}, 500);
	
}
async function delRole(row){//删除角色
	loading.value = true;
	let res = await deleteRole(formId.value,fieldName.value,row.taskDefKey);
	loading.value = false;
	ElNotification({
      title: res.success ? '成功' : '失败',
      message: res.msg,
      type: res.success ? 'success' : 'error',
      duration: 2000,
      offset: 80
    });
	if(res.success){
		reloadTable();
	}
}
async function delPerm(row){//删除权限
	loading.value = true;
	let res = await delNodePerm(formId.value,fieldName.value,row.taskDefKey);
	ElNotification({
      title: res.success ? '成功' : '失败',
      message: res.msg,
      type: res.success ? 'success' : 'error',
      duration: 2000,
      offset: 80
    });
	loading.value = true;
	if(res.success){
		reloadTable();
	}
}

//tree点击选择框时触发
const onCheckChange = (node,isChecked) => {
	//已经选择的节点
	treeSelectedData.value = permTreeRef.value?.y9TreeRef?.getCheckedNodes(true);
}

async function saveInfo(){//保存绑定角色
	if(treeSelectedData.value.length == 0){
		ElNotification({title: '提示',message: '请选择角色',type: 'info',duration: 2000,offset: 80});
		reject();
		return;
	}
	let roleIds = [];
	let roleNames = [];
	for(let obj of treeSelectedData.value){
		roleIds.push(obj.id);
		roleNames.push(obj.name);
	}
	let res = await saveRoleChoice(formId.value,fieldName.value,taskDefKey.value,roleNames.join(","),roleIds.join(","));
	ElNotification({
      title: res.success ? '成功' : '失败',
      message: res.msg,
      type: res.success ? 'success' : 'error',
      duration: 2000,
      offset: 80
    });
	if(res.success){
		reloadTable();
		treeDrawer.value = false;
	}
}
async function saveFieldPerm(row){//保存权限
	loading.value = true;
	let res = await saveNodePerm(formId.value,fieldName.value,row.taskDefKey);
	loading.value = false;
	ElNotification({
      title: res.success ? '成功' : '失败',
      message: res.msg,
      type: res.success ? 'success' : 'error',
      duration: 2000,
      offset: 80
    });
	if(res.success){
		reloadTable();
	}
}
</script>

<style>
  	.permissionConfig .el-dialog__body{
		padding: 5px 10px;
  	}
	.permissionConfig .el-header{
		padding: 10px 10px;
	}
	.permissionConfig .el-main{
		padding: 0;
	}
	.permissionConfig .el-drawer__header{
		margin-bottom:0;
		padding-bottom:16px;
		border-bottom: 1px solid #eee;
	}
</style>