<template>
	<el-tabs v-model="activeName" style="height:40px" @tab-click="tabclick">
		<el-tab-pane v-if="viewType!='draft'" label="草稿箱" name="draft" mark="11111"></el-tab-pane>
		<el-tab-pane v-if="viewType!='todo'" label="待办件" name="todo"></el-tab-pane>
		<el-tab-pane v-if="viewType!='doing'" label="在办件" name="doing"></el-tab-pane>
		<el-tab-pane v-if="viewType!='done'" label="办结件" name="done"></el-tab-pane>
		<template v-for="view in viewTypeList">
			<el-tab-pane :key="view.mark" :label="view.name" :name="view.mark" v-if="viewType!=view.mark"></el-tab-pane>
		</template>
	</el-tabs>
	<y9Table :config="viewTableConfig" @select-all="handlerSelectData" @select="handlerSelectData">
		<template #opt_button="{row, column, index}">
			<span @click="editView(row)"><i class="ri-edit-line"></i>编辑</span>
		</template>
	</y9Table>
	<div slot="footer" class="dialog-footer" style="text-align: center;margin-top: 15px;">
		<el-button type="primary" @click="saveCopy"><span>复制所选</span></el-button>
		<el-button type="primary" @click="closePage"><span>取消</span></el-button>
	</div>
</template>

<script lang="ts" setup>
  import { $deepAssignObject, } from '@/utils/object.ts'
  import {getViewList,saveView,saveOrder,getViewTypeList,copyView} from "@/api/itemAdmin/item/viewConfig";
  const props = defineProps({
      currTreeNodeInfo: {//当前tree节点信息
        type: Object,
        default:() => { return {} }
      },
	  vcDialogConfig: {
		type: Object,
	  },
	  viewType:String
    })
	
	const data = reactive({
		viewTypeList:[],
		viewTypes:['draft','todo','doing','done'],
		activeName:'',
		//当前节点信息
		currInfo:props.currTreeNodeInfo,
		viewTableConfig: {//人员列表表格配置
			columns: [
				{ title: '', type: 'selection', fixed: 'left' },
				{
					title: "序号",
					type:'index',
					width: '60',
				},
				{
					title: "表名",
					key: "tableName",
				},
				{
					title: "字段名",
					key: "columnName",
					width: '130',
				},
				{
					title: "显示名称",
					key: "disPlayName",
					width: '100',
				},
				{
					title: "显示宽度",
					key: "disPlayWidth",
					width: '100',
				},
				{
					title: "显示位置",
					key: "disPlayAlign",
					width: '100',
					render:(row) =>{
						let alignStr = '';
						switch (row.disPlayAlign) {
							case 'left':
								alignStr = '靠左';
								break;
							case 'center':
								alignStr = '居中';
								break;
							case 'right':
								alignStr = '靠右';
								break;
							default:
								alignStr = '居中';
								break;
						}
						return alignStr;
					}
				},
			],
			tableData: [],
			pageConfig:false,//取消分页
		},
		//弹窗配置
		cvdialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
					const viewFormInstance = addOrEditRef.value.viewFormRef;
					viewFormInstance.validate(async valid => {
						if(valid){
							let result = {success:false,msg:''};
							let formData = addOrEditRef.value.viewFormData;
							console.log(formData);
							
							result = await saveView(formData);
							ElNotification({
								title: result.success ? '成功' : '失败',
								message: result.msg,
								type: result.success ? 'success' : 'error',
								duration: 2000,
								offset: 80
							});
							resolve()
						}else {
							ElMessage({
								type: 'error',
								message: '验证不通过，请检查',
								offset: 65
							});
							reject()
						}
					});
				})
			},
			visibleChange:(visible) => {
				if(!visible){
					cvdialogConfig.value.onOkLoading = false;
					getViewConfigList();
				}
			}
		},
    	addOrEditRef:'',
		viewIdArr:[],
	})
	
	let {
		viewTypes,
		activeName,
		currInfo,
		viewTableConfig,
		cvdialogConfig,
		viewIdArr,
		viewTypeList,
	} = toRefs(data);
	
  async function initTable(){//权限配置
  	let result = await getViewTypeList();
    if(result.success){
      viewTypeList.value = result.data;
	  viewTypeList.value.forEach(element => {
			viewTypes.value.push(element.mark);
	  });
    }

	for (let i = 0; i < viewTypes.value.length; i++) {
		if(props.viewType==viewTypes.value[i]){
			viewTypes.value.splice(i,1);
		}
	}
	activeName.value = viewTypes.value[0];
	console.log(viewTypes.value);
	
    let res = await getViewList(props.currTreeNodeInfo.id,activeName.value);
    if(res.success){
      viewTableConfig.value.tableData = res.data;
    }
  }

  initTable();

  function tabclick(tab,event){//页签切换
	activeName.value = tab.props.name;
    getViewConfigList();
  }

  async function getViewConfigList(){
	let res = await getViewList(props.currTreeNodeInfo.id,activeName.value);
    if(res.success){
      viewTableConfig.value.tableData = res.data;
    }
  }

  function handlerSelectData(id, data){
	viewIdArr.value = id;
  }

  function saveCopy(){
	if(viewIdArr.value.length == 0 ){
		ElNotification({title: '操作提示',message: '请勾选要复制的数据',type: 'error',duration: 2000,offset: 80});
		return;
	}
	let ids = [];
	for(let obj of viewIdArr.value){
		ids.push(obj.id);
	}
	const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
        copyView(ids.toString(),props.viewType).then(res => {
          loading.close();
		  ElNotification({title: '操作提示',message: res.msg,type: res.success?'success':'error',duration: 2000,offset: 80});
		  if (res.success) {
			props.vcDialogConfig.show = false;
		  } 
      });
  }

  function closePage(){
	props.vcDialogConfig.show = false;
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