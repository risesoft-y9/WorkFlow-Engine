<template>
	<el-tabs v-model="activeName" style="height:40px" @tab-click="tabclick">
		<el-tab-pane label="请求参数" name="Request"></el-tab-pane>
		<el-tab-pane label="响应参数" name="Response"></el-tab-pane>
	</el-tabs>
	<div class="margin-bottom-20" style="margin-top:10px;margin-bottom:10px;" v-if="Object.keys(currTreeNodeInfo).length > 0 && currTreeNodeInfo.systemName != ''">
		<el-button type="primary" @click="bindParams()" 
			class="global-btn-main">
			<i class="ri-link"></i>
			<span>绑定参数</span>
		</el-button>
	</div>
	<y9Table :config="tableConfig" >
		<template #opt_button="{row, column, index}">
			<span style="margin-right:15px;" @click="editParams(row)"><i class="ri-edit-line"></i>修改</span>
			<span @click="delParams(row)"><i class="ri-delete-bin-line"></i>删除</span>
		</template>
	</y9Table>
	<y9Dialog v-model:config="dialogConfig">
		<ParamsBind ref="ParamsBindtRef" :interface="interface" :row="row" :activeName="activeName"/>
	</y9Dialog>
</template>

<script lang="ts" setup>
  import { $deepAssignObject, } from '@/utils/object.ts'
  import {getParamsBindList,saveParamsBind,removeParamsBind} from "@/api/itemAdmin/item/interfaceConfig";
  import ParamsBind from './paramsBind.vue'
  const props = defineProps({
      currTreeNodeInfo: {//当前tree节点信息
        type: Object,
        default:() => { return {} }
      },
      interface:{
        type: Object,
        default:() => { return {} }
      },
    })
	
	const data = reactive({
		tableConfig: {//人员列表表格配置
			columns: [
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{
					title: "参数名称",
					key: "parameterName",
					width: '180',
				},
				{
					title: "参数类型",
					key: "parameterType",
					width: '100',
				},
				{
					title: "表名",
					key: "tableName",
				},
				{
					title: "字段名称",
					width: '220',
					key: "columnName",
				},
				{
					title: "操作",
					width: '160',
					slot: 'opt_button'
				},
			],
			tableData: [],
			pageConfig:false,//取消分页
			height:'auto'
		},
		//弹窗配置
		dialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
					ParamsBindtRef.value.formRef.validate(async valid => {
						if(valid){
							let res = await saveParamsBind(ParamsBindtRef.value.formData);
							ElNotification({
								title: res.success ? '成功' : '失败',
								message: res.msg,
								type: res.success ? 'success' : 'error',
								duration: 2000,
								offset: 80
							});
							if(res.success){
								getBindList();
							}
							resolve();
						}else{
							reject();
						}
					});
				})
			},
			visibleChange:(visible) => {
				if(!visible){
				}
			}
		},
		activeName:'Request',
		row:{},
		ParamsBindtRef:'',
	})
	
	let {
		tableConfig,
		activeName,
		dialogConfig,
		row,
		ParamsBindtRef,
	} = toRefs(data);
	
	onMounted(()=>{
		getBindList();
	});

  async function getBindList(){//权限配置
	tableConfig.value.tableData = [];
    let res = await getParamsBindList(props.currTreeNodeInfo.id,props.interface.interfaceId,activeName.value);
    if(res.success){
      tableConfig.value.tableData = res.data;
    }
  }

  function tabclick(tab,event){//页签切换
	activeName.value = tab.props.name;
	if(activeName.value == 'Request'){
		tableConfig.value.columns.splice(2,0,{title: "参数类型",key: "parameterType",width: '100'})
	}else{
		tableConfig.value.columns.splice(2,1);
	}
    getBindList();
  }

  function bindParams() {
	row.value = [];
	Object.assign(dialogConfig.value,{
		show:true,
		width:'30%',
		title:'绑定参数',
		showFooter:true,
	});
  }

  function editParams(rows) {
	row.value = rows;
	Object.assign(dialogConfig.value,{
		show:true,
		width:'30%',
		title:'绑定参数',
		showFooter:true,
	});
  }

  function delParams(row){
	ElMessageBox.confirm(
		'你确定要删除绑定的参数吗？',
		'提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'info',
	}).then(async () => {
		let result = {success:false,msg:''};
		result = await removeParamsBind(row.id);
		ElNotification({
			title: result.success ? '成功' : '失败',
			message: result.msg,
			type: result.success ? 'success' : 'error',
			duration: 2000,
			offset: 80
		});
		if(result.success){
			getBindList();
		}
	}).catch(() => {
		ElMessage({
			type: 'info',
			message: '已取消删除',
			offset: 65
		});
	});
}
 
</script>
