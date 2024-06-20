<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2024-04-23 15:08:38
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-05-13 10:05:01
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\views\workForm\add.vue
-->
<template>
  <el-container>
    <y9Dialog v-model:config="dialogConfig">
      <fm-generate-form
        id="printTest"
        ref="generateForm"
        :data="formJson"
        :edit="edit"
        :remote="remoteFuncs"
        style="margin: auto;"
      >
      </fm-generate-form>
    </y9Dialog>
  </el-container>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive} from 'vue';
import {useFlowableStore} from "@/store/modules/flowableStore";
import {getFormJson,getFormInitData} from "@/api/flowableUI/form";
import {getBindPreFormByItemId,savePreFormData} from "@/api/flowableUI/preform";
import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
const { t } = useI18n();    
const router = useRouter()
const currentrRute = useRoute()
const flowableStore = useFlowableStore()

const data = reactive({
  generateForm: '',
  formJson: {"list": [], "config": {}},
  edit: true,//表单是否可编辑
  remoteFuncs: {},
  initDataUrl: "",//表单初始化数据路径
  formId:'',//表单id
  //弹窗配置
  dialogConfig: {
    show: false,
    title: '',
    onOkLoading: true,
    onOk: (newConfig) => {
      return new Promise(async (resolve, reject) => {
        generateForm.value.getData(true).then(data => {
          data.guid = "";
          let jsonData = JSON.stringify(data).toString();
          savePreFormData(flowableStore.itemId,formId.value, jsonData).then(res => {
            if (res.success) {
              if(res.data != ''){//保存成功直接打开主表页面
                ElMessage({type: 'success', message: t("保存表单成功"), appendTo: '.newForm-container'});
                let path = "/index/edit";
                if(currentrRute.path.indexOf("workIndex") > -1){
                  path = "/workIndex/edit";
                }
                router.push({path:path,query:{itemId:flowableStore.getItemId,itembox:"add",processSerialNumber:res.data,formType:'preform'}});
              }else{
                ElMessage({type: 'success', message: t("保存表单发生异常"), appendTo: '.newForm-container'});
                reject();
              }
            } else {
              ElMessage({type: 'error', message: t("保存表单失败"), appendTo: '.newForm-container'});
              reject();
            }
            resolve();
          }).catch(() => {
              reject(new Error(t("保存表单发生异常")).message);
          });
        }).catch(() => {
          ElMessage({type: 'error', message: t("表单验证不通过"), appendTo: '.newForm-container'});
          reject(new Error(t("表单验证不通过")).message);
        });
      });
    },
    visibleChange: (visible) => {},
  },
});

let { 
  generateForm,
  formJson,
  edit,
  remoteFuncs,
  initDataUrl,
  formId,
  dialogConfig
} = toRefs(data);

onMounted(() => {
  openForm();
});
 
 watch(()=> router,(newVal,oldVal)=>{
  openForm();
 },{deep:true});
  
async function openForm(){
  let itemId = flowableStore.getItemId;
  let res = await getBindPreFormByItemId(itemId);
  if(res.success && res.data.formId != ''){//配置有前置表单
    Object.assign(dialogConfig.value, {
      show: true,
      width: '65%',
      title: computed(() => t('前置表单预填')),
      showFooter: true,
      cancelText: '取消',
      okText: '确定'
    });
    formId.value = res.data.formId;
    showPreForm();
    return;
  }
  //let itemId = currentrRute.query.itemId;

  let path = "/index/edit";
  if(currentrRute.path.indexOf("workIndex") > -1){
    path = "/workIndex/edit";
  }

  router.push({path:path,query:{itemId:itemId,itembox:"add"}});
}


function showPreForm() {
  getFormJson(formId.value).then(res => {//表单显示
    if (res.success) {
      if (res.data != null) {
        formJson.value = JSON.parse(res.data);
      }
      initDataUrl.value = formJson.value?.config.initDataUrl != undefined ? formJson.value.config.initDataUrl : "";
      nextTick(() => {
        generateForm.value.refresh();
        let Promise = generateForm.value.getData(false);
        Promise.then(function (value) {
          getFormInitData(initDataUrl.value, "").then(res => {//表单初始化数据：1.在这里初始化数据，2.在表单设计里通过表单数据源初始化数据
            let initData = res.data;
            for (let key of Object.keys(value)) {
              if (value[key] != undefined && value[key].toString().indexOf("$_") > -1) {
                let key0 = value[key].toString().slice(2);
                value[key] = initData[key0] == undefined ? "" : initData[key0];
              }
            }
            generateForm.value.setData(value);
          });
        }).catch(() => {
        });
      });
  }
    });
}
</script>
<style> 
</style>
