<template>
  <el-button :size="fontSizeObj.buttonSize" :style="{ fontSize: fontSizeObj.baseFontSize }" v-if="showButton" @click="openTree">{{ $t('选择') }}</el-button>
  <y9Dialog v-model:config="dialogConfig" class="y9selecttree">
    <selectTree ref="selectTreeRef" :treeApiObj="treeApiObj" :selectField="selectField" @onCheckChange="onCheckChange" :showCheckbox="true"/>
  </y9Dialog>
</template>

<script lang="ts" setup>
import {inject,computed} from 'vue';
import {getOrgList,getOrgTree,treeSearch} from "@/api/flowableUI/entrustManage";
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo')||{}; 
const props = defineProps({
    tableField:String,//关联表单字段
})

const emits = defineEmits(['update_person']);
const ruleForm = ref<FormInstance>();
const data = reactive({
  showButton:false,
  selectTreeRef:'',
  treeApiObj:{//tree接口对象
    topLevel: getOrgList,
    childLevel:{//子级（二级及二级以上）tree接口
      api:getOrgTree,
      params:{treeType:'tree_type_person'}
    },
    search:{
      api:treeSearch,
      params:{
        key:'',
        treeType:'tree_type_org_person'
      }
    },
  },
  selectField: [
    //设置需要选择的字段
    {
      fieldName: 'orgType',
      value: ['Person'],
    },
  ],
  //弹窗配置
  dialogConfig: {
    show: false,
    title: "",
    onOkLoading: true,
    onOk: (newConfig) => {
      return new Promise(async (resolve, reject) => {
        if(treeSelectedData.value.length == 0){
          ElMessage({type: 'error', message: t('请选择人员'),offset:65, appendTo: '.margin-bottom-20'});
          reject();
          return;
        }
        let name = [];
        for(let item of treeSelectedData.value){
          name.push(item.name);
        }
        let data = {};
        data.tableField = props.tableField;
        data.value = name;
        emits("update_personName",data);//向父组件GenerateElementItem传值
        resolve();
      })
    },
    visibleChange:(visible) => {
      
    }
  },
  treeSelectedData:[],
});

let {
  showButton,
  dialogConfig,
  selectTreeRef,
  treeApiObj,
  selectField,
  treeSelectedData,
} = toRefs(data);

 defineExpose({
  initPersonTree
 });

  function initPersonTree(data){
    console.log("加载人员树");
    showButton.value = true;
  }

  //tree点击选择框时触发
	const onCheckChange = (node,isChecked) => {
		//已经选择的节点
		treeSelectedData.value = selectTreeRef.value?.y9TreeRef?.getCheckedNodes(true);
	}

  function openTree() {
    Object.assign(dialogConfig.value, {
      show: true,
      width: '50%',
      title: computed(() => t('人员选择')),
      showFooter: true
    });
  }

  
</script>

<style scoped lang="scss">
 
</style>

<style lang="scss">
 .y9selecttree .y9-dialog-content{
    max-height: 600px;
  }
</style>