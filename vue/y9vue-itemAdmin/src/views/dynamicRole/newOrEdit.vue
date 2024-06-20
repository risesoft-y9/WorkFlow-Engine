<template>
	<el-form ref="dynamicRoleForm" :model="dynamicRole" :rules="rules" label-width="80px" >
		<el-form-item prop="name" label="角色名称">
			<el-input v-model="dynamicRole.name" clearable></el-input>
		</el-form-item>
		<el-form-item prop="classPath" label="类路径">
			<el-input v-model="dynamicRole.classPath" clearable></el-input>
		</el-form-item>	
		<el-form-item prop="useProcessInstanceId"  label="流程相关">
			<el-radio style="margin-left: 15px;" v-model="dynamicRole.useProcessInstanceId" :label="true">是</el-radio>
			<el-radio v-model="dynamicRole.useProcessInstanceId" :label="false">否</el-radio>
		</el-form-item>
		<el-form-item prop="description" label="描述">
			<el-input v-model="dynamicRole.description" :rows="2" type="textarea" maxlength="500" resize="none" show-word-limit></el-input>
		</el-form-item>	
	</el-form>
</template>
<script lang="ts" setup>
import {ref, defineProps, defineExpose, onMounted, reactive, watch, defineEmits} from 'vue';
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus';
import {getDynamicRole} from '@/api/itemAdmin/dynamicRole';

const rules = reactive<FormRules>({
	name:{ required: true, message:'请输入角色名称',trigger: 'blur' },
	classPath:{ required: true, message:'请输入类路径',trigger: 'blur' },
});
const props = defineProps({
	row: {
      type: Object,
      default:() => { return {} }
    }
});

const data = reactive({
	dynamicRole:{id:'',name:'',classPath:'',useProcessInstanceId:false,description:''},
	dynamicRoleForm:''
})
	
let {
	dynamicRole,
	dynamicRoleForm,
} = toRefs(data);

onMounted(()=>{
	if(props.row.id != undefined){
		getDynamicRole(props.row.id).then(res => {
			dynamicRole.value = res.data;
		});
	}
});

defineExpose({
	dynamicRole,
	validForm
})

async function validForm(){
  	let valid = await dynamicRoleForm.value.validate(valid => {
		return valid;
	});
	return valid;
}
	
</script>
<style lang="scss">
.addOrEditDynamicRole .text-center {
	text-align:center;
}
</style>
