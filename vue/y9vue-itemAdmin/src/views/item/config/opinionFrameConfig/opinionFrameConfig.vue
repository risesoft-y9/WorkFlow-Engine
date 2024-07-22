<template>
  	<y9Card :title="`意见框配置${currInfo.name ? ' - ' + currInfo.name : ''}`">
		<div class="margin-bottom-20" v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''">
			<el-button type="primary" @click="formCopy" v-if="maxVersion != 1"
				class="global-btn-main">
			  	<i class="ri-file-copy-2-line"></i>
			  	<span>复制</span>
			</el-button>
		</div>
		<y9Table :config="opinionFrameListTableConfig" ></y9Table>

		<y9Dialog v-model:config="ofDialogConfig">
			<opinionFrameBind ref="opinionFrameBindRef" :currTreeNodeInfo="currTreeNodeInfo" :processDefinitionId="currTreeNodeInfo.processDefinitionId" :taskDefKey="taskDefKey"/>
		</y9Dialog>
	</y9Card>
</template>

<script lang="ts" setup>
  import { $deepAssignObject, } from '@/utils/object.ts'
  import opinionFrameBind from './opinionFrameBind.vue'
  import {getBpmList,copyBind} from "@/api/itemAdmin/item/opinionFrameConfig";
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
		opinionFrameListTableConfig: {//人员列表表格配置
			columns: [
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{
					title: "流程节点名称",
					key: "taskDefName",
				},
				{
					title: "意见框名称",
					key: "opinionFrameNames",
				},
				{
					title: "操作",
					render: (row) => {
						let button = [ 
							h('span',{
								style:{
									marginRight:'15px'
								},
								onClick: () => {
									opinionBind(row);
								}
							},[
								h('i',{
									class: 'ri-user-settings-line',
									style:{
										marginRight:'4px'
									}
								}),
							],'意见框配置'),
						]
						return button;
					}
				},
			],
			tableData: [],
			pageConfig:false,//取消分页
			height:'auto'
		},
		//弹窗配置
		ofDialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
					
				})
			},
			visibleChange:(visible) => {
				console.log('visible',visible)
				if(!visible){
					getOpinionFrameConfig();
				}
			}
		},
    taskDefKey:'',
	})
	
	let {
		currInfo,
		opinionFrameListTableConfig,
		ofDialogConfig,
        taskDefKey,
	} = toRefs(data);
	
	watch(() => props.currTreeNodeInfo,(newVal) => {
		currInfo.value = $deepAssignObject(currInfo.value, newVal);
    	getOpinionFrameConfig();
	},{deep:true,})

	onMounted(()=>{
		getOpinionFrameConfig();
	});

  async function getOpinionFrameConfig(){//权限配置
	opinionFrameListTableConfig.value.tableData = [];
    let res = await getBpmList(props.currTreeNodeInfo.processDefinitionId,props.currTreeNodeInfo.id);
    if(res.success){
      opinionFrameListTableConfig.value.tableData = res.data;
    }
  }

  async function opinionBind(row) {
    taskDefKey.value = row.taskDefKey;
		Object.assign(ofDialogConfig.value,{
			show:true,
			width:'50%',
			title:'意见框权限管理【'+row.taskDefName+'】',
			showFooter:false
		});
  }

  async function formCopy(){
		var tips = "确定复制当前版本绑定的配置到最新版本吗？";
		if(props.selectVersion === props.maxVersion){
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
			result = await copyBind(props.currTreeNodeInfo.id,props.currTreeNodeInfo.processDefinitionId);
			ElNotification({
				title: result.success ? '成功' : '失败',
				message: result.msg,
				type: result.success ? 'success' : 'error',
				duration: 2000,
				offset: 80
			});
			if(result.success){
				getOpinionFrameConfig();
			}
		}).catch(() => {
			ElMessage({
				type: 'info',
				message: '已取消复制',
				offset: 65
			});
		});
    }

</script>

<style>
  	.permconfig .el-dialog__body{
		  padding: 5px 10px;
  	}
</style>