<template>
  <y9Card :title="`正文模板配置${currInfo.name ? ' - ' + currInfo.name : ''}`" class="wordConfig">
	<el-descriptions
		title=""
		direction="vertical"
		:column="4"
		:size="size"
		border
		>
		<el-descriptions-item label="已绑定模板名称" label-align="center" align="center">
			<span v-if="wordInfo.tempName!=''">{{wordInfo.tempName}}</span>
			<span v-else>未绑定模板</span>
		</el-descriptions-item>
		<el-descriptions-item label="模板选择">
			 <el-form ref="wordConfigForm" :model="formData" :rules="rules">
				<el-form-item prop="templateName">
					<el-select v-model="formData.templateId" placeholder="请选择正文模板" @change="tableChange">
						<el-option v-for="item in templateList" :key="item.id" :label="item.fileName" :value="item.id">
					</el-option>
				</el-select>
				</el-form-item>
			</el-form>
		</el-descriptions-item>
		<el-descriptions-item label="操作" align="center" :span="6" >
			<el-button type="primary" @click="templateBind()">模板绑定</el-button>
			<el-button v-if="wordInfo.tempName!=''" type="primary" @click="delTemplate">删除</el-button>
		</el-descriptions-item>
	</el-descriptions>
  </y9Card>
</template>

<script lang="ts" setup>
  import { $deepAssignObject, } from '@/utils/object.ts'
  import {getTemplateBind,deleteBind,saveBind} from "@/api/itemAdmin/item/wordConfig";
  const props = defineProps({
      currTreeNodeInfo: {//当前tree节点信息
        type: Object,
        default:() => { return {} }
      },
    })
	
	const rules = reactive({
        templateName:{ required: true,message: '请选择正文模板', trigger: 'blur' },
      });
	const data = reactive({
		//当前节点信息
		currInfo:props.currTreeNodeInfo,
		wordInfo:{
			tempName:'',
			bindId:''
		},
		formData:{
			templateId:'',
			templateName:''
		},
		templateList:[],
		wordConfigForm:''
	})
	
	let {
		currInfo,
		wordInfo,
		formData,
		templateList,
		wordConfigForm
	} = toRefs(data);
	
	watch(() => props.currTreeNodeInfo,(newVal) => {
		currInfo.value = $deepAssignObject(currInfo.value, newVal);
    	getTemplateBindInfo();
	},{deep:true,})

	watch(() => wordInfo.value.bindId,(newVal) => {
    	getTemplateBindInfo();
	})

	onMounted(()=>{
		getTemplateBindInfo();
	});

  async function getTemplateBindInfo(){
    let res = await getTemplateBind(props.currTreeNodeInfo.id);
    if(res.success){
		wordInfo.value.tempName = res.data.tempName;
		wordInfo.value.bindId = res.data.bindId;
        templateList.value = res.data.templateList;
    }
	if(wordConfigForm.value){
		setTimeout(() => {
			wordConfigForm.value.clearValidate();
		}, 200);
	}
  }

   function tableChange(val) {
		formData.value.templateId = val;
		for(let i = 0; i < templateList.value.length; i++){
			if(templateList.value[i].id == val){
				formData.value.templateName = templateList.value[i].fileName;
				break;
			}
      }
  }
  function templateBind(){
	if(!wordConfigForm.value) return;
	wordConfigForm.value.validate(valid => {
		if (valid) {
			const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
			saveBind(props.currTreeNodeInfo.id,formData.value.templateId,props.currTreeNodeInfo.processDefinitionId).then(res => {
				loading.close();
				ElNotification({
					title: res.success ? '成功' : '失败',
					message: res.msg,
					type: res.success ? 'success' : 'error',
					duration: 2000,
					offset: 80
				});
				if (res.success) {
					getTemplateBindInfo();
					wordInfo.value.tempName = formData.value.templateName;
					formData.value.templateId = '';
				}
			});
		}
	});
  }

  async function delTemplate(){
	  	ElMessageBox.confirm(
			"你确定要删除绑定的正文模板",
			'提示', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'info',
		}).then(async () => {
			let result = {success:false,msg:''};
			result = await deleteBind(wordInfo.value.bindId);
			ElNotification({
				title: result.success ? '成功' : '失败',
				message: result.msg,
				type: result.success ? 'success' : 'error',
				duration: 2000,
				offset: 80
			});
			if(result.success){
				wordInfo.value.tempName = '';
				formData.value.templateId = '';
				getTemplateBindInfo();
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

<style>
  	.wordConfig .el-form-item {
		display: flex;
		--font-size: 14px;
		margin-bottom: 0px;
	}
</style>