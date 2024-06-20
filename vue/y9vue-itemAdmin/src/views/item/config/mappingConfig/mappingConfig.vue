<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-13 09:49:46
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-22 09:08:50
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-itemAdmin\src\views\item\config\mappingConfig\mappingConfig.vue
-->
<template>
  <y9Card :title="`表单字段映射${currInfo.name ? ' - ' + currInfo.name : ''}`">
    <el-tabs v-model="activeName" style="height:40px" @tab-click="tabclick" v-if="dockingItemId || dockingSystem">
      <el-tab-pane label="系统字段映射" name="system" v-if="dockingSystem"></el-tab-pane>
      <el-tab-pane label="事项字段映射" name="item" v-if="dockingItemId"></el-tab-pane>
    </el-tabs>
    <div style="margin: 8px 0;" v-if="dockingItemId || dockingSystem">
      <el-button-group>
        <el-button type="primary" size="small" @click="newMapping()"><i class="ri-add-line"></i>新增</el-button>
        <el-button type="primary" size="small" @click="delMapping"><i class="ri-delete-bin-line"></i>删除</el-button>
      </el-button-group>
    </div>
    <div v-else style="color:red;margin: 10px 0;">暂无对接系统或事项</div>
    <y9Table v-if="dockingItemId || dockingSystem" :config="tableConfig" @select-all="handlerSelectData"
		@select="handlerSelectData">
		<template #opt_button="{row, column, index}">
			<span @click="editMapping(row)"><i class="ri-edit-line"></i>编辑</span>
		</template>
	</y9Table>
    <y9Dialog v-model:config="mappingDialogConfig">
      <newOrModify ref="newOrModifyRef" :currTreeNodeInfo="currInfo" :id="editId" :activeName="activeName" :dockingItemName="dockingItemName"/>
	</y9Dialog>
  </y9Card>
</template>

<script lang="ts" setup>
 import { $deepAssignObject, } from '@/utils/object.ts'
import newOrModify from "@/views/item/config/mappingConfig/newOrModify.vue";
import {getList,saveOrUpdate,remove} from "@/api/itemAdmin/item/mappingConfig";
import { onMounted } from 'vue';
const props = defineProps({
  currTreeNodeInfo: {//当前tree节点信息
    type: Object,
    default:() => { return {} }
  },
  itemList: {
    type: Array,
    default:() => { return [] }
  },
})
const data = reactive({
		activeName:'system',
		//当前节点信息
		currInfo:props.currTreeNodeInfo,
		columns: [
			{title: '', type: 'selection', fixed: 'left',width: '60'},
			{title: "序号",type:'index',width: '60'},
			{title: "表名",key: "tableName",width: 'auto'},
			{title: "字段名",key: "columnName",width: '200'},
			{title: "映射字段",key: "mappingName",width: '200'},
			{title: "创建时间",key: "createTime",width: '180'},
			{title: "操作",width: '80',slot: 'opt_button'},
		],
		columns1: [
			{title: '', type: 'selection', fixed: 'left',width: '60'},
			{title: "序号",type:'index',width: '60'},
			{title: "表名",key: "tableName",width: 'auto'},
			{title: "字段名",key: "columnName",width: '200'},
			{title: "映射表名",key: "mappingTableName",width: 'auto'},
			{title: "映射字段",key: "mappingName",width: '200'},
			{title: "创建时间",key: "createTime",width: '180'},
			{title: "操作",width: '80',slot: 'opt_button'},
		],
		tableConfig: {//人员列表表格配置
			columns: [],
			tableData: [],
			pageConfig:false,//取消分页
		},
		//弹窗配置
		mappingDialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
					const mappingFormInstance = newOrModifyRef.value.mappingForm;
					mappingFormInstance.validate(async valid => {
						if(valid){
							let result = {success:false,msg:''};
							let formData = newOrModifyRef.value.mappingConf;
							formData.sysType = activeName.value == 'item' ? '1' : '2';
							formData.itemId = currInfo.value.id;
							formData.mappingId = activeName.value == 'item' ? currInfo.value.dockingItemId : currInfo.value.dockingSystem;
							console.log(formData);
							result = await saveOrUpdate(formData);
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
					mappingDialogConfig.value.onOkLoading = false;
					getMappingList();
				}
			}
		},
		newOrModifyRef:'',
		dockingItemId:false,
		dockingSystem:false,
		dockingItemName:'',
		editId:'',
		multipleSelection:[],
	})
	
	let {
		activeName,
		currInfo,
		tableConfig,
		mappingDialogConfig,
		dockingSystem,
		dockingItemId,
		dockingItemName,
		newOrModifyRef,
		editId,
		multipleSelection,
		columns,
		columns1
	} = toRefs(data);
	
	watch(() => props.currTreeNodeInfo,(newVal) => {
		currInfo.value = $deepAssignObject(currInfo.value, newVal);
		dockingItemId.value = false;
		dockingSystem.value = false;
		if(currInfo.value.dockingItemId != '' && currInfo.value.dockingItemId != null){
			activeName.value = 'item';
			dockingItemId.value = true;
		}
		if(currInfo.value.dockingSystem != '' && currInfo.value.dockingSystem != null){
			activeName.value = 'system';
			dockingSystem.value = true;
			tableConfig.value.columns = columns.value;
		}
		for(let item of props.itemList){
			if(item.id == currInfo.value.dockingItemId){
				dockingItemName.value = item.name;
			}
		}
		getMappingList();
	})

  onMounted(async ()=>{
    if(currInfo.value.dockingItemId != '' && currInfo.value.dockingItemId != null){
      activeName.value = 'item';
      dockingItemId.value = true;
    }
    if(currInfo.value.dockingSystem != '' && currInfo.value.dockingSystem != null){
      activeName.value = 'system';
      dockingSystem.value = true;
	  tableConfig.value.columns = columns.value;
    }
	for(let item of props.itemList){
		if(item.id == currInfo.value.dockingItemId){
			dockingItemName.value = item.name;
		}
	}
    getMappingList();
  });

  function tabclick(tab,event){//页签切换
	activeName.value = tab.props.name;
	if(activeName.value == "item"){
		tableConfig.value.columns = columns1.value;
	}else{
		tableConfig.value.columns = columns.value;
	}
    getMappingList();
  }

  async function getMappingList(){//权限配置
    if(!dockingItemId.value && !dockingSystem.value){
      return;
    }
    let mappingId = currInfo.value.dockingSystem;
    if(activeName.value == 'item'){
      mappingId = currInfo.value.dockingItemId;
    }
    let res = await  getList(currInfo.value.id,mappingId);
    if(res.success){
      tableConfig.value.tableData = res.data;
    }
  }

  function handlerSelectData(id, data){
	multipleSelection.value = id;
  }

  function newMapping(){//新增视图
    editId.value = '';
    Object.assign(mappingDialogConfig.value,{
      show:true,
      width:'40%',
      title:'新增',
      showFooter:true
    });
  }

  function editMapping(row){//修改视图
    editId.value = row.id;
    Object.assign(mappingDialogConfig.value,{
      show:true,
      width:'40%',
      title:'编辑',
      showFooter:true
    });
  }

  function delMapping(){//删除视图
      if(multipleSelection.value.length == 0){
		ElNotification({title: '操作提示',message: '请勾选要删除的数据',type: 'error',duration: 2000,offset: 80});
        return;
      }
      ElMessageBox.confirm(
		'你确定要删除的选中的数据吗？',
		'提示', {
		confirmButtonText: '确定',
		cancelButtonText: '取消',
		type: 'info',
	}).then(async () => {
		let result = {success:false,msg:''};
		let ids = [];
		for(let row of multipleSelection.value){
			ids.push(row.id);
		}
		result = await remove(ids.toString());
		ElNotification({
			title: result.success ? '成功' : '失败',
			message: result.msg,
			type: result.success ? 'success' : 'error',
			duration: 2000,
			offset: 80
		});
		if(result.success){
			getMappingList();
		}
      }) .catch(() => {
        ElMessage({
			type: 'info',
			message: '已取消删除',
			offset: 65
		});
      });
    }
</script>

<style>
  	.mappingConfig .el-dialog__body{
		  padding: 5px 10px;
  	}
</style>