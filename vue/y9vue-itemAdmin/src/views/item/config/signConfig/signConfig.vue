<template>
  <y9Card :title="`签收配置${currInfo.name ? ' - ' + currInfo.name : ''}`" class="wordConfig">
	<div class="margin-bottom-20" v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''">
			<el-button type="primary" @click="formCopy" v-if="maxVersion != 1" style="margin-right: 10px;"
				class="global-btn-main">
			  	<i class="ri-file-copy-2-line"></i>
			  	<span>复制</span>
			</el-button>
			<el-tooltip style="margin-left: 0px;" placement="right" effect="customized" content="单人节点如果配置抢占式办理，则选择岗位时可以选择多个岗位，多个岗位谁签收谁办理；并行节点配置抢占式时，多个岗位都可以发送，第一个人发送后，其他人被强制办结。">
				<el-button size="small"><i class="ri-questionnaire-line"></i>抢占式办理说明</el-button>
			</el-tooltip>
		</div>
	<y9Table :config="signTableConfig" >
		<template #switch="{ row, column, index }">
			<el-switch
                v-model="row.signTask"
                class="mb-2"
                active-text="是"
                inactive-text="否"
                @change="setHandlerMode(row)"
              />
		</template>
	</y9Table>
  </y9Card>
</template>

<script lang="ts" setup>
  import { $deepAssignObject, } from '@/utils/object.ts'
  import {getBpmList,saveBind,copyBind} from "@/api/itemAdmin/item/signConfig";
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
		signTableConfig: {
			columns: [
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{
					title: "任务节点名称",
					key: "taskDefName",
				},{
					title: "任务节点类型",
					key: "taskType",
				},{
					title: "抢占式办理",
					key: "signTask",
					slot:"switch",
				}
			],
			tableData: [],
			pageConfig: false,
			height:'auto'
		},
	})
	
	let {
		currInfo,
		signTableConfig,
	} = toRefs(data);
	
	watch(() => props.currTreeNodeInfo,(newVal) => {
		currInfo.value = $deepAssignObject(currInfo.value, newVal);
    	getProcessList();
	},{deep:true,})

	onMounted(()=>{
		getProcessList();
	});


  async function getProcessList(){//权限配置
	signTableConfig.value.tableData = [];
    let res = await getBpmList(props.currTreeNodeInfo.processDefinitionId,props.currTreeNodeInfo.id);
    if(res.success){
		signTableConfig.value.tableData = res.data;
    }
  }

  async function setHandlerMode(row){
	row.itemId = props.currTreeNodeInfo.id;
	row.processDefinitionId = props.currTreeNodeInfo.processDefinitionId;
	let result = {success:false,msg:''};
			result = await saveBind(row);
			ElNotification({
				title: result.success ? '成功' : '失败',
				message: result.msg,
				type: result.success ? 'success' : 'error',
				duration: 2000,
				offset: 80
			});
			if(result.success){
				getProcessList();
			}
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
  	.wordConfig .el-form-item {
		display: flex;
		--font-size: 14px;
		margin-bottom: 0px;
	}
</style>