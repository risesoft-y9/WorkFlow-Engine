<template>
	<el-dialog 
	:title="dialogTitle" 
	custom-class="addOrEditEntrust" 
	v-model="dialogVisible" 
	width="600px" 
	:close-on-click-modal="false"
	destroy-on-close>
		<div>
			<el-form ref="entrustForm" :model="entrust" :rules="rules" label-width="80px" >
				<el-form-item prop="assigneeName" label="受托人">
					<el-input v-model="entrust.assigneeName" :readonly="true" style="width:82%;"></el-input>
					<el-button type="primary" @click="treeDrawer= true" ><i class="ri-add-line"></i>选择</el-button>
				</el-form-item>
				<el-form-item prop="itemName" label="委托事项">
					<el-select v-model="entrust.itemName" placeholder="请选择委托事项" style="width:100%;" @change="setItem">
						<el-option v-for="item in itemList" :key="item.id" :label="item.name" :value="item.id">
						</el-option>
					</el-select>
				</el-form-item>	
				<el-form-item  label="委托日期">
				  <el-col :span="11" prop="startTime">
        			<el-date-picker :disabledDate="startPicker" v-model="entrust.startTime" type="date" placeholder="开始日期" value-format="YYYY-MM-DD"></el-date-picker>
      			  </el-col>
				<el-col :span="2" class="text-center">
					<span class="text-gray-500">-</span>
				</el-col>
				<el-col :span="11" prop="endTime">
					<el-date-picker :disabledDate="endPicker" v-model="entrust.endTime" type="date" placeholder="结束日期" value-format="YYYY-MM-DD"></el-date-picker>
				</el-col>
				</el-form-item>
			</el-form>
			<div style="text-align: center;">
				<span slot="footer" class="dialog-footer">
					<el-button type="primary" @click="saveEntrust(entrustForm)">提交</el-button>
					<el-button @click="dialogVisible = false">取消</el-button>
				</span>
			</div>
		</div>
	</el-dialog>
	  <el-drawer v-model="treeDrawer" :direction="direction" :modal="false">
		<template #title>
		<h4>选择人员</h4>
		</template>
		<template #default>
		<div>
			<PersonTree ref="personTree"  @org-click="onOrgClick"/>
		</div>
		</template>
		<template #footer>
		<!-- <div style="flex: auto;text-align:center;">
			<el-button @click="cancelClick">cancel</el-button>
			<el-button type="primary" @click="confirmClick">confirm</el-button>
		</div> -->
		</template>
	</el-drawer>
</template>
<script lang="ts" setup>
import {ref, defineProps, defineExpose, onMounted, reactive, watch, defineEmits} from 'vue';
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus';
import {saveOrUpdate,getEntrustInfo} from '@/api/itemAdmin/entrust';
import PersonTree from '@/views/tree/tree.vue';

const dialogVisible = ref(false);
const innerVisible = ref(false);
const dialogTitle = ref('');
const entrust = ref({id:'',assigneeId:'',assigneeName:'',itemId:'',itemName:'',startTime:'',endTime:''});
const itemList = ref([]);
const entrustForm = ref<FormInstance>();
const rules = reactive<FormRules>({
			assigneeName:{ required: true, message:'请点击‘选择’按钮添加受托人',trigger: 'blur' },
			itemId:{ required: true, message:'请选择委托事项',trigger: 'blur' },
			startTime:{ required: true, message:'请选择开始日期和结束日期',trigger: 'blur' },
			endTime:{ required: true, message:'请选择结束日期',trigger: 'blur' }
      });
const treeDrawer = ref(false)
const direction = ref('rtl')

const props = defineProps({
	reloadTable : Function
});

defineExpose({ show });
async function show(id){
	dialogVisible.value = true;
	dialogTitle.value = "添加出差委托";
	if(id!=''){
		dialogTitle.value = "编辑出差委托";
	}
	entrust.value = {id:'',assigneeId:'',assigneeName:'',itemId:'',itemName:'',startTime:'',endTime:''};
	getEntrustInfo(id).then(res => {
		if(res.data.entrust != undefined){
			entrust.value = res.data.entrust;
		}
		itemList.value = res.data.itemList;
	});
}

function onOrgClick(id,name) {
	entrust.value.assigneeId = id;
	entrust.value.assigneeName = name;
	treeDrawer.value = false;
}

function setItem(val){
	entrust.value.itemId = val;
}

const startPicker = (time) => {
	if (entrust.endTime != "" && entrust.endTime != undefined) {
		let date = new Date(entrust.endTime);
		return time.getTime() < Date.now() - 8.64e7 || time.getTime() > date.getTime();
	} else {
		return time.getTime() < Date.now() - 8.64e7;
	}
}

const endPicker = (time) => {
	if (entrust.startTime != "" && entrust.startTime != undefined) {
		let date = new Date(entrust.startTime);
		return time.getTime() < date.getTime() - 8.64e7 || time.getTime() < Date.now() - 8.64e7;
	} else {
		return time.getTime() < Date.now() - 8.64e7;
	}
}

const saveEntrust = (refForm) => {
	if(!refForm) return;
	refForm.validate(valid => {
		if (valid) {
			const loading = ElLoading.service({ lock: true, text: '正在处理中', background: 'rgba(0, 0, 0, 0.3)' });
			saveOrUpdate(entrust.value).then(res => {
				loading.close();
            if(res.success){
              ElMessage({type: 'success', message: res.msg,offset: 65});
			  props.reloadTable();
			  dialogVisible.value = false;
            }else{
              ElMessage({type: 'error', message: res.msg, offset: 65});
            }
          });
		}
	});
}
	
</script>
<style lang="scss">
.addOrEditEntrust .text-center {
	text-align:center;
}
</style>
