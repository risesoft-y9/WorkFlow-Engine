
<template>
      <y9Table :config="processTableConfig">
          <template #status="{row,column,index}">
            <i v-if="row.newToDo == 1" :title="$t('未阅')"  class="ri-chat-poll-line" :style="{color:'green',fontSize: fontSizeObj.mediumFontSize}"></i>
            <i v-else-if="row.startTime == '未开始'" :title="$t('未开始')" class="ri-chat-history-line" :style="{color:'green',fontSize: fontSizeObj.mediumFontSize}"></i>
            <i v-else-if="row.endTime == ''" :title="$t('已阅，未处理')" class="ri-eye-line" :style="{color:'blue',fontSize: fontSizeObj.mediumFontSize}"></i>
            <i v-else-if="row.endTime != ''" :title="$t('已处理')" class="ri-checkbox-circle-line" :style="{fontSize: fontSizeObj.mediumFontSize}"></i>
          </template>
          <template #name="{row,column,index}">
            <font v-if="row.endFlag == '1'">{{row.name}}<i class="ri-check-double-line" style="color:red;" :title="$t('强制办结任务')"></i></font>
            <font v-else>{{row.name}}</font>
          </template>
        </y9Table>
</template>

<script lang="ts" setup>
import {inject} from 'vue';
import {historyList} from "@/api/flowableUI/process";
import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo');
const props = defineProps({
      processInstanceId:String,
})

const data = reactive({
		processTableConfig: {
			columns: [
				{ title: computed(() => t("序号")), type:'index', width: '55', },
        { title: computed(() => t("状态")), type:'status', width: '55',slot:'status' },
				{ title: computed(() => t("办件人")), key: "assignee", width: '170',},
				{ title: computed(() => t("办理环节")), key: "name",width: '120', slot:'name'},
				{ title: computed(() => t("意见内容")), key: "opinion", },
				{ title: computed(() => t("开始时间")), key: "startTime", width: '165', },
				{ title: computed(() => t("结束时间")), key: "endTime", width: '165', },
        { title: computed(() => t("办理时长")), key: "time", width: '155', },
        { title: computed(() => t("描述")), key: "description", width: '160', },
			],
			tableData: [],
			pageConfig:false,//取消分页
      border:0,
      height:550
		}
	})
	
	let {
		processTableConfig,
	} = toRefs(data);
	
	watch(() => props.processInstanceId,(newVal) => {
    reloadTable();
	})

 	onMounted(()=>{
		reloadTable();
	});

	async function reloadTable(){
      let res = await historyList(props.processInstanceId);
      if(res.success){
        processTableConfig.value.tableData = res.data.rows;
      }
  }

</script>

<style >
  .el-main-table{
    padding: 0px;
  }
</style>