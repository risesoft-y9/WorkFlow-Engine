<template>
  <div class="number-button">
    <el-button :size="fontSizeObj.buttonSize"
        :style="{ fontSize: fontSizeObj.baseFontSize }" v-if="showButton" @click="editNum">{{ $t('编号') }}</el-button>
    <y9Dialog v-model:config="dialogConfig">
      <el-form ref="ruleForm" class="numberForm" :model="form" :rules="rules">
        <div style="text-align:center;display: inline-flex;">
          <el-form-item>
            <el-select v-model="organWord" class="m-2" :placeholder="$t('请选择机关代字')" :size="fontSizeObj.buttonSize" @change="organWordChange">
              <el-option
                v-for="item in organWordList"
                :label="item.name"
                :style="{ fontSize: fontSizeObj.baseFontSize }"
                :value="item.name"
              />
            </el-select>
          </el-form-item>
          <el-form-item>〔</el-form-item>
          <el-form-item prop="year" style="margin: 0px 5px;">
            <el-input v-model.number="form.year" style="width: 100px;"></el-input>
          </el-form-item>
          <el-form-item>〕</el-form-item>
          <el-form-item prop="number">
            <el-input v-model.number="form.number"></el-input>
          </el-form-item>
          <el-form-item style="margin-left: 5px;">{{ $t('号') }}</el-form-item>
        </div>
      </el-form>
    </y9Dialog>
  </div>
</template>

<script lang="ts" setup>
import {inject} from 'vue';
import {findByCustom,checkNumber,getNumber} from "@/api/flowableUI/organWord";
import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo')||{}; 
const props = defineProps({
    numberCustom:String,//绑定编号标识
    tableField:String,//关联表单字段
    numberName:String//绑定编号名称
})

var checkNum = (rule, value, callback) => {
    if (!value) {
      return callback(new Error(t('编号不能为空')));
    }
    if (!Number.isInteger(value)) {
      return callback(new Error(t('请输入数字值')));
    }
    return callback();
  };
  var checkYear = (rule, value, callback) => {
    if (!value) {
      return callback(new Error(t('年份不能为空')));
    }
    if (!Number.isInteger(value)) {
      return callback(new Error(t('请输入数字值')));
    }
    return callback();
  };

const emits = defineEmits(['update_number']);
const ruleForm = ref<FormInstance>();
const data = reactive({
      editNumberRef:'',
      showButton:false,
      numberInfo:{},
      numberData:{},
      organWord:"",//机关代字
      organWordList:[],
      currentNumber:'',//当前编号
      form:{number:'',year:''},
      rules: {
        number: [
          {validator: checkNum, trigger: 'blur' }
        ],
        year: [
          {validator: checkYear, trigger: 'blur' }
        ]
      },
      //弹窗配置
      dialogConfig: {
        show: false,
        title: "",
        onOkLoading: true,
        onOk: (newConfig) => {
          return new Promise(async (resolve, reject) => {
            ruleForm.value.validate(async valid => {
              if(valid){
                console.log(currentNumber.value);
                
                if(currentNumber.value != (organWord.value+ "〔" +form.value.year +"〕"+form.value.number+"号")){
                  //检查编号是否使用
                  checkNumber(numberData.value.itemId, props.numberCustom,organWord.value,form.value.year,form.value.number,numberData.value.processSerialNumber).then(res => {
                    if(res.success){
                      if(res.data.status == 0){
                        form.value.number = res.data.newNumber
                        ElMessage({ type: "error", message: t("当前编号已被使用，您可以使用")+form.value.number+t("号"),offset:65, appendTo: '.number-button'});
                        dialogConfig.value.loading = false;
                        reject();
                        return;
                      }else if(res.data.status == 1){
                        let newNumber = organWord.value + "〔" +form.value.year +"〕"+ form.value.number + "号";
                        setNumber(newNumber);
                        currentNumber.value = newNumber;
                        resolve()
                      }else if(res.data.status == 2){
                        ElMessage({ type: "error", message: t("当前编号不存在"),offset:65, appendTo: '.number-button'});
                        dialogConfig.value.loading = false;
                        reject();
                        return;
                      }else if(res.data.status == 3){
                        ElMessage({ type: "error", message: t("发生异常"),offset:65, appendTo: '.number-button'});
                        dialogConfig.value.loading = false;
                        reject();
                        return;
                      }
                    }
                  });
                }else{
                  resolve()
                }
              }else{
                ElMessage({
                  type: 'error',
                  message: t('验证不通过，请检查'),
                  offset: 65, 
                  appendTo: '.number-button'
                });
                reject()
              }
            });
          })
        },
        visibleChange:(visible) => {
          if(!visible){
            dialogConfig.value.onOkLoading = false;
          }
        }
      },
});

let {
  form,
  rules,
  editNumberRef,
  showButton,
  numberInfo,
  numberData,
  organWord,
  organWordList,
  currentNumber,
  dialogConfig
} = toRefs(data);

 defineExpose({
  initNumber
 });

  function initNumber(data,formData){
    console.log("加载编号按钮：" + props.numberName + "(" + props.numberCustom + ")");
    if(formData != undefined){
      currentNumber.value = formData[props.tableField];
    }
    numberData.value = data;
    //获取编号按钮权限
    findByCustom(numberData.value.itemId, numberData.value.processDefinitionId, numberData.value.taskDefKey, props.numberCustom).then(res => {
      if(res.success){
        if(res.data.length > 0){
          if(res.data[0].hasPermission){
            showButton.value = true;
            organWord.value = res.data[0].name;
          }
        }
        organWordList.value = res.data;
      }
    });
  }

  function organWordChange(val) {
    getNumber(numberData.value.itemId, props.numberCustom, val, form.value.year).then(res => {
      if(res.success){
        form.value.number = res.data.numberTemp;
      }
    }).catch(err => {
      console.log('输出报错',err);
    });
  }

  function editNum(){//编辑编号
    let nowDate = new Date();
    form.value.year = nowDate.getFullYear();
    //numberName.value = organWord.value;
    if(currentNumber.value != "" && currentNumber.value != undefined && currentNumber.value != null){//编辑已有编号
      form.value.year = parseInt(currentNumber.value.split("〔")[1].split("〕")[0]);
      //numberName.value = currentNumber.value.split("〔")[0];
      form.value.number = parseInt(currentNumber.value.split("〕")[1].split("号")[0]);
    }else{//新增编号,获取默认编号
      getNumber(numberData.value.itemId, props.numberCustom, organWord.value, form.value.year).then(res => {
        if(res.success){
          form.value.number = res.data.numberTemp;
        }
      }).catch(err => {
        console.log('输出报错',err);
      });
    }

    Object.assign(dialogConfig.value,{
      show:true,
      width:'25%',
      title: computed(() => t('编号编辑')),
      showFooter:true
    });
  }

  function setNumber(number){//设置表单编号
    let data = {};
    data.tableField = props.tableField;
    
    data.value = number;
    emits("update_number",data);//向父组件GenerateElementItem传值
  }
</script>

<style scoped lang="scss">
  .number-button {
    /*message */
    :global(.el-message .el-message__content) {
        font-size: v-bind('fontSizeObj.baseFontSize');
    }
  }
</style>

<style lang="scss">
  .numberForm .el-form-item__content{
    margin: auto;
    line-height: 40px !important;
  }
  .numberForm .el-form-item{
    margin-bottom: 0 !important;
  }
  .numberForm .el-input__inner{
    height: 31px !important;
  }
</style>