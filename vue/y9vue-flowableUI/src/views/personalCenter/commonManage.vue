<template>
    <y9Dialog v-model:config="dialogConfig">
      <div style="text-align: right;">
        <el-input type="textarea" style="margin-top: 0px;" 
        :style="{ fontSize: fontSizeObj.baseFontSize }"
        resize="none" :rows="5" :placeholder="$t('请输入内容')" maxlength="50" v-model="commonContent" show-word-limit></el-input>
        <el-button type="primary" style="margin-top: 8px;" @click="saveCommon()" :size="fontSizeObj.buttonSize"
          :style="{ fontSize: fontSizeObj.baseFontSize }"><i class="ri-save-line"></i>{{ $t('保存') }}</el-button>
      </div>
    </y9Dialog>
    <div class="margin-bottom-20">
      <el-button type="primary" @click="addCommon()" :size="fontSizeObj.buttonSize"
        :style="{ fontSize: fontSizeObj.baseFontSize }"><i class="ri-add-line"></i>{{ $t('添加') }}</el-button>
    </div>
    <y9Table :config="tableConfig">
        <template #opt="{row,column,index}">
          <i class="ri-edit-line" @click="editCommon(row)"></i>
          <i style="margin-left:10px;" class="ri-delete-bin-line" @click="delCommon(row)"></i>
        </template>
    </y9Table>
</template>

<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive, inject} from 'vue';
import type {FormInstance,ElMessageBox, ElMessage,ElLoading } from 'element-plus';
import {commonSentencesList,saveCommonSentences,delCommonSentences,editCommonSentences} from "@/api/flowableUI/opinion";
import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
const { t } = useI18n();
const props = defineProps({
  commonSentencesData:Array
})

const emits = defineEmits(['update:commonSentencesData']);
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo')||{}; 
const data = reactive({
    commonContent:'',
    tabIndex:-1,
    tableConfig: {//表格配置
      columns: [
        { title: computed(() => t("序号")), type:'index', width: '60', },
        { title: computed(() => t("内容")), key: "content",width:'auto'},
        { title: computed(() => t("操作")), key: "opt", width: '100', slot:'opt'},
      ],
      tableData: [],
      height:'100%',
      pageConfig: false,
      border:0,
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
      commonContent,
      tabIndex,
      tableConfig,
      dialogConfig
	} = toRefs(data);

function saveCommon(){
  if(commonContent.value === ""){
    ElMessage({type: 'error', message: t("内容不能为空"),offset:65, appendTo: '.margin-bottom-20'});
    return;
  }
  if(tabIndex.value === -1){
    saveCommonSentences(commonContent.value).then(res => {
      if(res.success){
        ElMessage({type: 'success', message: res.msg,offset:65, appendTo: '.margin-bottom-20'});
        getCommonSentencesList();
        dialogConfig.value.show = false;
      }else{
        ElMessage({type: 'error', message: res.msg,offset:65, appendTo: '.margin-bottom-20'});
      }
    });
  }else{
    editCommonSentences(commonContent.value,tabIndex.value).then(res => {
      tabIndex.value = -1;
      if(res.success){
        ElMessage({type: 'success', message: res.msg,offset:65, appendTo: '.margin-bottom-20'});
        getCommonSentencesList();
        dialogConfig.value.show = false;
      }else{
        ElMessage({type: 'error', message: res.msg,offset:65, appendTo: '.margin-bottom-20'});
      }
    });
  }
}

getCommonSentencesList();
function getCommonSentencesList(){
  commonSentencesList().then(res => {
    tableConfig.value.tableData = res.data;
    emits('update:commonSentencesData', tableConfig.value.tableData);
  });
}

function addCommon(){
  commonContent.value = "";
  tabIndex.value = -1;
  Object.assign(dialogConfig.value,{
			show:true,
			width:'30%',
			title:computed(() => t('添加常用语')),
			showFooter:false
    });
}

function editCommon(row){
  commonContent.value = row.content;
  tabIndex.value = row.tabIndex;
  Object.assign(dialogConfig.value,{
    show:true,
    width:'30%',
    title:computed(() => t('修改常用语')),
    showFooter:false
  });
}

function delCommon(row){
  ElMessageBox.confirm(t("是否删除该常用语?"), t("提示"), {
        confirmButtonText: t("确定"),
        cancelButtonText: t("取消"),
        type: "info", 
        appendTo: '.margin-bottom-20'
      }).then(() => {
        delCommonSentences(row.tabIndex).then(res => {
        if(res.success){
            ElMessage({type: 'success', message: res.msg,offset:65, appendTo: '.margin-bottom-20'});
            getCommonSentencesList();
          }else{
            ElMessage({type: 'error', message: res.msg,offset:65, appendTo: '.margin-bottom-20'});
          }
        });
      }) .catch(() => {
        ElMessage({ type: "info", message: t("已取消删除") ,offset:65, appendTo: '.margin-bottom-20'});
      });
}

</script>

<style>
  /* .y9-table-div[data-v-105c0824] .el-table__body .cell .commonManage i{
    color: #586cb1; 
    cursor: pointer;
} */
.el-tabs__content {
    overflow: auto;
    position: relative;
    height: 600px;
}
</style>

<style scoped>
.margin-bottom-20 {
   /*message */
  :global(.el-message .el-message__content) {
      font-size: v-bind('fontSizeObj.baseFontSize');
  }

  /*messageBox */ 
  :global(.el-message-box .el-message-box__content) {
      font-size: v-bind('fontSizeObj.baseFontSize');
  }
  :global(.el-message-box .el-message-box__title) {
      font-size: v-bind('fontSizeObj.largeFontSize');
  }
  :global(.el-message-box .el-message-box__btns button) {
      font-size: v-bind('fontSizeObj.baseFontSize');
  }
}
</style>