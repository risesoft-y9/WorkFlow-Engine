<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2024-05-24 15:07:00
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-27 15:36:38
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6.4-xxx\y9-vue\y9vue-itemAdmin\src\views\item\config\interfaceConfig\taskBind.vue
-->
<template>
	<div class="margin-bottom-20" style="margin-bottom: 10px;" v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''">
		流程定义版本<el-select v-model="selectVersion" style="width: 90px;margin-right: 15px;margin-left: 15px;" @change="pIdchange">
			<el-option v-for="pd in processDefinitionList" :key="pd.id" :label="pd.version" :value="pd.version">
			</el-option>
		</el-select>
		<el-button type="primary" @click="copyData" v-if="maxVersion != 1"
			class="global-btn-main">
			<i class="ri-file-copy-2-line"></i>
			<span>复制</span>
		</el-button>
	</div>
	<y9Table :config="tableConfig" class="taskBind">
		<template #type="{row,column,index}">
			<span v-if="row.type == 'UserTask'">用户任务</span>
			<span v-if="row.type == 'SequenceFlow'">路由</span>
			<span v-if="row.type == 'Process'"></span>
		</template>
		<template #opt_button="{row,column,index}">
			<template v-if="row.type == 'UserTask'">
				<el-checkbox-group v-model="row.condition" @change="checkChange(row)">
					<el-checkbox label="创建" value="创建" />
					<el-checkbox label="完成" value="完成" />
				</el-checkbox-group>
			</template>
			<template v-if="row.type == 'SequenceFlow'">
				<el-checkbox-group v-model="row.condition" @change="checkChange(row)">
					<el-checkbox label="经过" value="经过" />
				</el-checkbox-group>
			</template>
			<template v-if="row.type == 'Process'">
				<el-checkbox-group v-model="row.condition" @change="checkChange(row)">
					<el-checkbox label="启动" value="启动" />
					<el-checkbox label="办结" value="办结" />
				</el-checkbox-group>
			</template>
		</template>
	</y9Table>
</template>

<script lang="ts" setup>
  import { $deepAssignObject, } from '@/utils/object.ts'
  import {getBpmList,saveTaskBind,copyBind} from "@/api/itemAdmin/item/interfaceConfig";
  import {getProcessDefinitionList} from "@/api/itemAdmin/item/itemAdminConfig";
  const props = defineProps({
      currTreeNodeInfo: {//当前tree节点信息
        type: Object,
        default:() => { return {} }
      },
	  interface: {
        type: Object,
        default:() => { return {} }
      },
      maxVersion:Number,
      selectVersion:Number,
    })
	
	const data = reactive({
		//当前节点信息
		currInfo:props.currTreeNodeInfo,
		tableConfig: {//人员列表表格配置
			columns: [
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{
					title: "流程节点名称",
					key: "elementName",
				},
				{
					title: "流程节点key",
					key: "elementKey",
				},
				{
					title: "节点类型",
					key: "type",
					width: '120',
					slot: 'type'
				},
				{
					title: "执行条件",
					width: '200',
					slot: 'opt_button'
				},
			],
			tableData: [],
			pageConfig:false,//取消分页
			height:'auto'
		},
		processDefinitionList:[],
		selectVersion:'',
		maxVersion:'',
		processDefinitionId:'',
	})
	
	let {
		currInfo,
		tableConfig,
		processDefinitionList,
		selectVersion,
		maxVersion,
		processDefinitionId,
	} = toRefs(data);
	

	onMounted(async ()=>{
		processDefinitionId.value = props.currTreeNodeInfo.processDefinitionId;
		getTaskConfig();
		let res = await getProcessDefinitionList(props.currTreeNodeInfo.workflowGuid);
		if(res.success){
			processDefinitionList.value = res.data;
			if(processDefinitionList.value.length > 0){
				maxVersion.value = processDefinitionList.value[0].version;
				selectVersion.value = processDefinitionList.value[0].version;
				processDefinitionId.value = processDefinitionList.value[0].id;
			}
		}
	});

  	async function getTaskConfig(){
		tableConfig.value.tableData = [];
		let res = await getBpmList(props.currTreeNodeInfo.id,props.interface.interfaceId,processDefinitionId.value);
		if(res.success){
			tableConfig.value.tableData = res.data;
			for(let item of tableConfig.value.tableData){
				if(item.bind){
					item.condition = item.condition.split(",");
				}
			}
		}
	}

	async function copyData(){
		var tips = "确定复制当前版本绑定的配置到最新版本吗？";
		if(selectVersion.value === maxVersion.value){
			tips = "确定复制上一个版本绑定的配置到最新版本吗？";
		}
	  	ElMessageBox.confirm(
			tips,
			'提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'info',
		}).then(async () => {
			let result = {success:false,msg:''};
			result = await copyBind(props.currTreeNodeInfo.id,props.interface.interfaceId,props.currTreeNodeInfo.processDefinitionId);
			ElNotification({
				title: result.success ? '成功' : '失败',
				message: result.msg,
				type: result.success ? 'success' : 'error',
				duration: 2000,
				offset: 80
			});
			if(result.success){
				getTaskConfig();
			}
		}).catch(() => {
			ElMessage({
				type: 'info',
				message: '已取消复制',
				offset: 65
			});
		});
    }

	async function checkChange(row) {
		console.log(111,row.condition);
		let condition = [];
		for(let item of row.condition){
			condition.push(item)
		}
		let result = await saveTaskBind(props.currTreeNodeInfo.id,props.interface.interfaceId,processDefinitionId.value,row.elementKey,condition.join(","));
		ElNotification({
			title: result.success ? '成功' : '失败',
			message: result.msg,
			type: result.success ? 'success' : 'error',
			duration: 2000,
			offset: 80
		});
	}

	async function pIdchange(val){
		let pId = '';
		for(let pd of processDefinitionList.value){
			if(pd.version == val){
				pId = pd.id;
				break;
			}
		}
		processDefinitionId.value = pId;
		getTaskConfig();
    }


</script>

<style>
  	.permconfig .el-dialog__body{
		  padding: 5px 10px;
  	}
	
</style>
<style lang="scss" scoped>
	.y9-table-div .el-checkbox{
		margin-right: 10px !important;
	}
</style>