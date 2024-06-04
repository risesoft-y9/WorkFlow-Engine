<template>
    <div
     class="task-list"
      v-loading="loading"
      :element-loading-text="$t('正在处理中')"
      element-loading-spinner="el-icon-loading"
      element-loading-background="rgba(0, 0, 0, 0.8)">
      <el-divider content-position="left">{{ $t('任务列表') }}</el-divider>
      <y9Table :config="tableConfig"></y9Table>
      <el-divider content-position="left">{{$t(title)}}{{ $t('原因') }}</el-divider>
      <el-input style="margin-bottom: 15px;" type="textarea" :placeholder="$t('请输入内容')" 
      :style="{ fontSize: fontSizeObj.baseFontSize }"
      v-model="reason" maxlength="50" rows="4" resize="none" show-word-limit></el-input>
      <div style="text-align: right;margin-bottom: 10px;">
        <el-button type="primary" @click="submit" :size="fontSizeObj.buttonSize"
        :style="{ fontSize: fontSizeObj.baseFontSize }"><i class="ri-check-line"></i>{{ $t('提交') }}</el-button>
      </div>
    </div>
</template>

<script lang="ts" setup>
import {inject, computed} from 'vue';
import { useRoute,useRouter } from 'vue-router'
import {buttonApi} from "@/api/flowableUI/buttonOpt";
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo')||{}; 
const router = useRouter()
const currentrRute = useRoute()
const props = defineProps({
  basicData: {
    type: Object,
    default:() => { return {} }
  },
  optType: String
})

const data = reactive({
      title:computed(() => t('退回')),
      loading:false,
      multiInstance:'',
      reason:'',
      tableConfig: {
        columns: [
          { title: computed(() => t("序号")), type:'index', width: '60', },
          { title: computed(() => t("办理人")), key: "user", width: '180',},
          { title: computed(() => t("任务类型")), key: "multiInstance",width: '100' },
          { title: computed(() => t("任务状态")), key: "status", width: '90', },
          { title: computed(() => t("完成时间")), key: "endTime", width: 'auto', },
        ],
        tableData: [],
        pageConfig: false,
        border:0,
      }
});

let {
      title,
      loading,
      multiInstance,
      reason,
      tableConfig,
} = toRefs(data);

show();

 function show(){
      reason.value == "";
      if(props.optType == 'takeback'){
        title.value = computed(() => t('收回'));
      }
      buttonApi.getTaskList(props.basicData.taskId).then(res => {
        multiInstance.value = res.data.multiInstance;
        if(multiInstance.value=="串行"){
          tableConfig.value.columns.splice(3,0,{ title: computed(() => t("办理顺序")), key: "order", width: '150', });
        }else if(multiInstance.value=="并行"){
          tableConfig.value.columns.splice(3,0,{ title: computed(() => t("主协办")), key: "parallelSponsor", width: '150', });
        }
        tableConfig.value.tableData = res.data.rows;
      });
  }
    
  function submit(){
      if(reason.value == ""){
        ElMessage({type: 'error', message: t("请输入")+title.value+t("原因"),offset:65, appendTo: '.task-list'});
        return;
      }
      loading.value = true;
      if(props.optType == "rollback"){
        buttonApi.rollback(props.basicData.taskId,reason.value).then(res => {
          loading.value = false;
          if(res.success){
            ElMessage({type: 'success', message: res.msg,offset:65, appendTo: '.task-list'});
            let link = currentrRute.matched[0].path;
            let query = {
              itemId:props.basicData.itemId,
              refreshCount:true
            };
            router.push({  //核心语句
              path:link+'/todo',   //跳转的路径
              query: query          //路由传参时push和query搭配使用 ，作用时传递参数
            });
          }else{
            ElMessage({type: 'error', message: res.msg,offset:65, appendTo: '.task-list'});
          }
        });
      }else if(props.optType == "takeback"){
        buttonApi.takeback(props.basicData.taskId,reason.value).then(res => {
          loading.value = false;
          if(res.success){
            ElMessage({type: 'success', message: res.msg,offset:65, appendTo: '.task-list'});
            let link = currentrRute.matched[0].path;
            let query = {
              itemId:props.basicData.itemId,
              refreshCount:true
            };
            router.push({  //核心语句
              path:link+'/doing',   //跳转的路径
              query: query          //路由传参时push和query搭配使用 ，作用时传递参数
            });
          }else{
            ElMessage({type: 'error', message: res.msg,offset:65, appendTo: '.task-list'});
          }
        });
      }
    }
</script>

<style scoped>
:deep(.el-divider__text.is-left) {
  font-size: v-bind('fontSizeObj.baseFontSize');
}
/*message */
.task-list {
  :global(.el-message .el-message__content) {
    font-size: v-bind('fontSizeObj.baseFontSize');
  }
}

</style>