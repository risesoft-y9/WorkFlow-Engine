<template>
   <y9Table :config="tableConfig" :filterConfig="filterConfig" @on-current-change="handleSelectionChange" @on-curr-page-change="onCurrPageChange"
            @on-page-size-change="onPageSizeChange">
      <template #tableFilter>
        <el-button v-if="type == 'my'" type="primary" @click="editReminder" :size="fontSizeObj.buttonSize"
          :style="{ fontSize: fontSizeObj.baseFontSize }"><i class="ri-edit-2-line"></i>{{ $t('修改') }}</el-button>
      </template>
  </y9Table>
      
    <y9Dialog v-model:config="dialogConfig">
        <el-input type="textarea" resize="none" :rows="5" :placeholder="$t('请输入内容')" 
        :style="{ fontSize: fontSizeObj.baseFontSize }"
        maxlength="50" v-model="msgContent" show-word-limit></el-input>
        <el-button type="primary" style="margin-top: 8px;float: right;" @click="dialogConfig.show = false" :size="fontSizeObj.buttonSize"
          :style="{ fontSize: fontSizeObj.baseFontSize }">{{ $t('取消') }}</el-button>
        <el-button type="primary" style="margin-top: 8px;margin-right: 8px;float: right;" @click="sendReminder()" :size="fontSizeObj.buttonSize"
          :style="{ fontSize: fontSizeObj.baseFontSize }">{{ $t('发送催办') }}</el-button>
     </y9Dialog>
</template>

<script lang="ts" setup>
import {inject} from 'vue';
import {reminderList,updateReminder} from "@/api/flowableUI/reminder";
import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo')||{}; 
const props = defineProps({
      processInstanceId:String,
      type:String
})

let total = ref(0);
const data = reactive({
    type:props.type,
    processInstanceId:'',
    msgContent:'',
    taskIds:[],
    currentRow:'',//当前行
		tableConfig: {
			columns: [
				{ title: computed(() => t("序号")), type:'index', width: '60', },
				{ title: computed(() => t("催办人")), key: "senderName", width: '120',},
				{ title: computed(() => t("内容")), key: "msgContent", },
				{ title: computed(() => t("催办时间")), key: "createTime", width: '180', },
				{ title: computed(() => t("办理环节")), key: "taskName", width: '150', },
        { title: computed(() => t("办理人")), key: "userName", width: '120', },
        { title: computed(() => t("查看时间")), key: "readTime", width: '180', },
			],
			tableData: [],
			pageConfig: {
				// 分页配置，false隐藏分页
				currentPage: 1, //当前页数，支持 v-model 双向绑定
				pageSize: 10, //每页显示条目个数，支持 v-model 双向绑定
				total: total.value, //总条目数
        pageSizeOpts:[10, 20, 30, 50, 100]
			},
      border:0,
		},
    filterConfig:{//过滤配置
			itemList:[
				{
					type:"slot",
					span:8,
					slotName:"tableFilter",
				},
			],
		},
    //弹窗配置
		dialogConfig: {
			show: false,
			title: "",
			onOkLoading: true,
			onOk: (newConfig) => {
				return new Promise(async (resolve, reject) => {
					
				})
			},
			visibleChange:(visible) => {
				
			}
		},
	})
	
	let {
    type,
    processInstanceId,
    msgContent,
    taskIds,
    filterConfig,
    currentRow,
		tableConfig,
    dialogConfig,
	} = toRefs(data);
	
	watch(() => props.taskId,(newVal) => {
    reloadTable();
	})

 	onMounted(()=>{
		reloadTable();
	});

	async function reloadTable(){
      let page = tableConfig.value.pageConfig.currentPage;
	    let rows = tableConfig.value.pageConfig.pageSize;
      reminderList(props.type,props.processInstanceId,page,rows).then(res => {
        if(res.success){
          tableConfig.value.tableData = res.rows;
          tableConfig.value.pageConfig.total = res.total;
        }
      });
    }

     //当前页改变时触发
    function onCurrPageChange(currPage) {
        tableConfig.value.pageConfig.currentPage = currPage;
        reloadTable();
    }
    //每页条数改变时触发
    function onPageSizeChange(pageSize) {
        tableConfig.value.pageConfig.pageSize = pageSize;
        reloadTable();
    }

    function handleSelectionChange(data) {
      currentRow.value = data;
    }

    function editReminder(){
      msgContent.value = '';
      if(currentRow.value.length == 0){
          ElMessage({type: 'error', message: t('请选中要修改的催办数据'),offset:65, appendTo: '.y9-table-div'});
      }else{
        msgContent.value = currentRow.value.msgContent;
        Object.assign(dialogConfig.value,{
          show:true,
          width:'40%',
          title:computed(() => t('修改催办信息')),
          showFooter:false
        });
      }
    }

    function sendReminder(){
      if(msgContent.value == ""){
        ElMessage({type: 'error', message: t("内容不能为空"),offset:65, appendTo: '.y9-table-div'});
        return;
      }
      updateReminder(currentRow.value.id,msgContent.value).then(res => {
        if(res.success){
            ElMessage({type: 'success', message: res.msg,offset:65, appendTo: '.y9-table-div'});
            reloadTable();
            dialogConfig.value.show = false;
          }else{
            ElMessage({type: 'error', message: res.msg,offset:65, appendTo: '.y9-table-div'});
          }
      });
    }   
</script>

<style scoped>
 /*message */
 :global(.el-message .el-message__content) {
    font-size: v-bind('fontSizeObj.baseFontSize');
}
</style>