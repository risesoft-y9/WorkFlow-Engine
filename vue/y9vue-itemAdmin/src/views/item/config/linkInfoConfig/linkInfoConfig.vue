<template>
  	<y9Card :title="`链接配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
		<div class="margin-bottom-20" v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''">
      		<el-button type="primary" @click="bindLink()" 
				class="global-btn-main">
			  	<i class="ri-link"></i>
			  	<span>绑定链接</span>
			</el-button>
		</div>
		<y9Table :config="tableConfig" >
			<template #opt_button="{row, column, index}">
				<span style="margin-right:15px;" @click="delBind(row)"><i class="ri-delete-bin-line"></i>删除绑定</span>
				<span style="margin-right:15px;" @click="addRole(row)"><i class="ri-add-line"></i>绑定角色</span>
				<span @click="delRole(row)" v-if="row.roleIds.length > 0"><i class="ri-delete-bin-line"></i>删除角色</span>
			</template>
		</y9Table>
		<el-drawer class="eldrawer" v-model="tableDrawer" direction='rtl' title="绑定链接" @close="closeDrawer">
			<div style="margin-bottom: 10px;text-align: left;">
				<el-input style="width:200px;margin-right: 5px;" v-model="linkName" placeholder="链接名称" clearable></el-input>
				<el-input style="width:200px;margin-right: 5px;" v-model="linkUrl" placeholder="链接地址" clearable></el-input>
				<el-button type="primary" size="small" @click="getLinkInfoList"><i class="ri-search-2-line"></i>搜索</el-button>
			</div>
			<y9Table :config="linkTableConfig" @select-all="handlerGetData" @select="handlerGetData"></y9Table>
			<div slot="footer" class="dialog-footer" style="text-align: center;margin-top: 15px;">
				<el-button type="primary" @click="saveBind"><span>保存</span></el-button>
				<el-button @click="tableDrawer = false"><span>取消</span></el-button>
			</div>
		</el-drawer>
		<el-drawer class="eldrawer" v-model="treeDrawer" direction='rtl' title="绑定角色">
     		<permTree ref="permTreeRef" :treeApiObj="treeApiObj" :showHeader="false" :selectField="selectField" @onCheckChange="onCheckChange"/>
			<div slot="footer" class="dialog-footer" style="text-align: center;margin-top: 15px;">
				<el-button type="primary" @click="saveRoleBind"><span>保存</span></el-button>
				<el-button  @click="treeDrawer = false"><span>取消</span></el-button>
			</div>
		</el-drawer>
		<el-drawer class="eldrawer" v-model="roleDrawer" direction='rtl' title="删除角色">
     		<y9Table :config="roleTableConfig" @select-all="handlerData" @select="handlerData"></y9Table>
			<div slot="footer" class="dialog-footer" style="text-align: center;margin-top: 15px;">
				<el-button type="primary" @click="delRoleBind"><span>确定</span></el-button>
				<el-button  @click="roleDrawer = false"><span>取消</span></el-button>
			</div>
		</el-drawer>
	</y9Card>
</template>

<script lang="ts" setup>
  import { $deepAssignObject, } from '@/utils/object.ts'
  import {getBindList,saveItemLinkBind,saveBindRole,removeBind,getBindRoleList,removeRole} from "@/api/itemAdmin/item/linkInfoConfig";
  import {getLinkList} from '@/api/itemAdmin/linkInfo';
  import {getRole,getRoleById} from "@/api/itemAdmin/item/permConfig"; 
  const props = defineProps({
      currTreeNodeInfo: {//当前tree节点信息
        type: Object,
        default:() => { return {} }
      },
      maxVersion:Number,
      selectVersion:Number,
    })
	
	const data = reactive({
		//当前节点信息
		currInfo:props.currTreeNodeInfo,
		permTreeRef:"",//tree实例
		tableDrawer: false,
		roleDrawer:false,
		treeDrawer:false,
		treeApiObj:{//tree接口对象
			topLevel: '',
			childLevel:{//子级（二级及二级以上）tree接口
				api:'',
				params:{treeType:''}
			},
			search:{
				api:'',
				params:{
					key:'',
					treeType:''
				}
			},
		},
		tableConfig: {//人员列表表格配置
			columns: [
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{
					title: "链接名称",
					key: "linkName",
					width: '160',
				},
				{
					title: "链接地址",
					key: "linkUrl",
				},
				{
					title: "角色名称",
					width: '200',
					key: "roleNames",
				},
				{
					title: "操作",
					width: '320',
					slot: 'opt_button'
				},
			],
			tableData: [],
			pageConfig:false,//取消分页
			height:'auto'
		},
		linkTableConfig: {
			columns: [
				{ title: '', type: 'selection', fixed: 'left' ,width: '60' },
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{ title: "链接名称", key: "linkName", width: '160'},
          		{ title: "链接地址", key: "linkUrl"},
			],
			tableData: [],
			pageConfig: false,
			height:'auto'
		},
		linkName:'',
		linkUrl:'',
		selectData:[],
		selectField: [
			//设置需要选择的字段
			{
				fieldName: 'orgType',
				value: ['role'],
			},
		],
		treeSelectedData: [],//tree选择的数据
		bindRoleLink:null,
		roleTableConfig: {
			columns: [
				{ title: '', type: 'selection', fixed: 'left', width: '60' },
				{ title: "序号", type:'index', width: '60', },
				{ title: "角色名称", key: "roleName", }
			],
			tableData: [],
			pageConfig: false,
		},
		roleIdArr:[],
	})
	
	let {
		permTreeRef,
		treeSelectedData,
		currInfo,
		tableDrawer,
		roleDrawer,
		treeDrawer,
		tableConfig,
		linkTableConfig,
        linkName,
		linkUrl,
		selectData,
		treeApiObj,
		selectField,
		bindRoleLink,
		roleTableConfig,
		roleIdArr,
	} = toRefs(data);
	
	watch(() => props.currTreeNodeInfo,(newVal) => {
		currInfo.value = $deepAssignObject(currInfo.value, newVal);
    	getLinkConfig();
	},{deep:true,})

	onMounted(()=>{
		getLinkConfig();
	});

  async function getLinkConfig(){//权限配置
	tableConfig.value.tableData = [];
    let res = await getBindList(props.currTreeNodeInfo.id);
    if(res.success){
      tableConfig.value.tableData = res.data;
    }
  }

 // 表格 选择框 选择后获取数据
 function handlerGetData(id, data) {
	selectData.value = id;
 }

  function closeDrawer(){
	linkName.value = '';
	linkUrl.value = '';
 }

async function bindLink(){
	linkTableConfig.value.tableData = [];
	selectData.value = [];
	getLinkInfoList();
	tableDrawer.value = true;
 }

 async function getLinkInfoList(){
	linkTableConfig.value.tableData = [];
	let res = await getLinkList(linkName.value, linkUrl.value);
	if(res.success){
		for(let item of res.data){
			let add = true;
			for(let row of tableConfig.value.tableData){
				if(item.id == row.linkId){
					add = false;
					break;
				}
			}
			if(add){
				linkTableConfig.value.tableData.push(item);
			}
		}
	}
 }

 //tree点击选择框时触发
 const onCheckChange = (node,isChecked) => {
	//已经选择的节点
	treeSelectedData.value = permTreeRef.value?.y9TreeRef?.getCheckedNodes(true);
}

function delBind(row){
	ElMessageBox.confirm(
		'你确定要删除绑定的链接吗？',
		'提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'info',
	}).then(async () => {
		let result = {success:false,msg:''};
		result = await removeBind(row.id);
		ElNotification({
			title: result.success ? '成功' : '失败',
			message: result.msg,
			type: result.success ? 'success' : 'error',
			duration: 2000,
			offset: 80
		});
		if(result.success){
			getLinkConfig();
		}
	}).catch(() => {
		ElMessage({
			type: 'info',
			message: '已取消删除',
			offset: 65
		});
	});
}

 async function saveBind(){
	let linkIds = [];
	if(selectData.value.length == 0){
		ElNotification({title: '操作提示',message: '请勾选要绑定的数据',type: 'error',duration: 2000,offset: 80});
		return;
	}
	for (let i = 0; i < selectData.value.length; i++) {
		linkIds.push(selectData.value[i].id);
	}
	let result = {success:false,msg:''};
	result = await saveItemLinkBind(props.currTreeNodeInfo.id,linkIds.toString());
	ElNotification({
		title: result.success ? '成功' : '失败',
		message: result.msg,
		type: result.success ? 'success' : 'error',
		duration: 2000,
		offset: 80
	});
	if(result.success){
		getLinkConfig();
		tableDrawer.value = false;
	}
}

function handlerData(rows){
	roleIdArr.value = [];
	for(let obj of rows){
		roleIdArr.value.push(obj.id);
	}
}

async function getRoleTable(){
	let res = await getBindRoleList(bindRoleLink.value.id);
	if(res.success){
		roleTableConfig.value.tableData = res.data;
	}
}

function addRole(row) {
	bindRoleLink.value = row;
	treeSelectedData.value = [];
	treeApiObj.value.topLevel = getRole;
	treeApiObj.value.childLevel.api = getRoleById;
	treeApiObj.value.childLevel.params.treeType = 'Role';
	treeDrawer.value = true;
	setTimeout(() => {
		permTreeRef.value.onRefreshTree();
	}, 500);
		
}

function delRole(row){
	bindRoleLink.value = row;
	if(row.roleIds.length == 1){
		ElMessageBox.confirm(
			'你确定要删除绑定的角色吗？',
			'提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'info',
		}).then(async () => {
			let result = {success:false,msg:''};
			result = await removeRole(row.roleIds.toString());
			ElNotification({
				title: result.success ? '成功' : '失败',
				message: result.msg,
				type: result.success ? 'success' : 'error',
				duration: 2000,
				offset: 80
			});
			if(result.success){
				getLinkConfig();
			}
		}).catch(() => {
			ElMessage({
				type: 'info',
				message: '已取消删除',
				offset: 65
			});
		});
	}else{
		roleDrawer.value = true;
		roleIdArr.value = [];
		getRoleTable();
	}
  }

  async function delRoleBind() {
	if(roleIdArr.value.length == 0){
		ElNotification({title: '操作提示',message: '请选择角色',type: 'error',duration: 2000,offset: 80});
		return;
	}
	ElMessageBox.confirm(
			'你确定要删除绑定的角色吗？',
			'提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'info',
		}).then(async () => {
			let result = {success:false,msg:''};
			result = await removeRole(roleIdArr.value.toString());
			ElNotification({
				title: result.success ? '成功' : '失败',
				message: result.msg,
				type: result.success ? 'success' : 'error',
				duration: 2000,
				offset: 80
			});
			if(result.success){
				roleIdArr.value = [];
				roleDrawer.value = false;
				getLinkConfig();
			}
		}).catch(() => {
			ElMessage({
				type: 'info',
				message: '已取消删除',
				offset: 65
			});
		});
  }
	
async function saveRoleBind(){
	if(treeSelectedData.value.length == 0){
		ElNotification({title: '操作提示',message: '请选择角色',type: 'error',duration: 2000,offset: 80});
		return;
	}
	let rIds = [];
	for (let i = 0; i < treeSelectedData.value.length; i++) {
		rIds.push(treeSelectedData.value[i].id);
	}
	
	let result = await saveBindRole(bindRoleLink.value.id,rIds.join(';'));
	ElNotification({title: '操作提示',message: result.msg,type: result.success?'success':'error',duration: 2000,offset: 80});
	treeDrawer.value = false;
	getLinkConfig();
}


</script>

<style>
  	.permconfig .el-dialog__body{
		  padding: 5px 10px;
  	}
</style>