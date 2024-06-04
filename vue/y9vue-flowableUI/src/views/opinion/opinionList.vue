<!--
 * @Author: qinman
 * @Date: 2023-11-10 09:17:03
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-03-05 15:00:36
 * @Description: 
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-vue\y9vue-flowableUI\src\views\opinion\opinionList.vue
-->
<template>
  <el-container style="height: 100%; width: 100%;padding:0 5px;" class="opinionList-container">
    <el-header style="line-height: 25px;padding: 0;" :height="headerheight">
      <el-link v-if="historyShow" type="primary" icon="el-icon-time" :underline="false" @click="showOpinionHistory">{{ $t('意见留痕') }}</el-link>
    </el-header>
    <el-main style="background-color: #fff;padding: 0;">
      <div class="suggest" :style="suggeststyle">
        <ul :style="ulstyle">
          <template v-for="item in opinionList">
            <li :key="item.opinion.id" v-if="item.editable"><!-- 个人意见 -->
              <div v-html="item.opinion.showContent"></div><!-- 使用v-html -->
              <a style="color:#586cb1;"><i v-if="opinionOptShow" class="ri-edit-box-line pencil2" :style="{fontSize: fontSizeObj.largeFontSize,marginRight:'0.3vw'}" :title="$t('编辑个人意见')" @click="editOpinion(item.opinion)"></i>
                <i v-if="opinionOptShow" class="ri-delete-bin-line pencil2" @click="deleteOpinion(item.opinion.id)" :style="{fontSize: fontSizeObj.largeFontSize,marginRight:'0.3vw',color: 'red'}" :title="$t('删除个人意见')"></i>
                <font style="margin-right:0.5vw">{{item.opinion.deptName}}</font>
                <font style="margin-right:0.5vw">
                  <font v-if="item.opinion.positionName == ''">{{item.opinion.userName}}</font>
                  <font v-else-if="item.opinion.positionName != '' && item.opinion.positionName.indexOf(item.opinion.userName) > -1">{{item.opinion.positionName}}</font>
                  <font v-else>{{item.opinion.userName}}[{{item.opinion.positionName}}]</font>
                </font>
                {{item.opinion.modifyDate}}
                <!-- <font v-if="item.isEdit">&nbsp;于&nbsp;{{item.opinion.modifyDate}}修改</font> -->
              </a>
            </li>
            <li :key="item.opinion.id" v-else><!-- 只读意见 -->
              <div v-html="item.opinion.showContent"></div>
              <a style="color:#586cb1;">
                <font style="margin-right:0.5vw">{{item.opinion.deptName}}</font>
                <font style="margin-right:0.5vw">
                  <font v-if="item.opinion.positionName == ''">{{item.opinion.userName}}</font>
                  <font v-else-if="item.opinion.positionName != '' && item.opinion.positionName.indexOf(item.opinion.userName) > -1">{{item.opinion.positionName}}</font>
                  <font v-else>{{item.opinion.userName}}[{{item.opinion.positionName}}]</font>
                </font>
                {{item.opinion.modifyDate}}
                <!-- <font v-if="item.isEdit">&nbsp;于&nbsp;{{item.opinion.modifyDate}}修改</font> -->
                
              </a>
            </li>
          </template>
          <template v-if="addable.addable"><!-- 新建个人意见 -->
            <li v-if="opinionImgShow"><div><img @click="addOpinion()" :title="$t('新建个人意见')" class="pencil"  src="@/assets/addOpinion.png" /></div></li>
          </template>
          
          <template v-if="opinionShow">
            <el-input
              ref="opinionInput"
              v-model="opinionContent"
              maxlength="500"
              rows="4"
              :placeholder="$t('请输入意见')"
              :style="{ fontSize: fontSizeObj.smallFontSize }"
              show-word-limit
              type="textarea"
              class="textareaCss"
            />
            <div style="text-align:right;">
              <el-select @change="selectComment" style="margin-right:10px;width: 100px;"
               :placeholder="$t('常用语选择')" class="opinionSelectCss">
                <el-option
                  v-for="item in commonList"
                  :label="item.content"
                  :style="{ fontSize: fontSizeObj.smallFontSize }"
                  :value="item.id"
                />
              </el-select>
              <el-button type="primary" @click="saveOrUpdateOpinion" size="small"
                :style="{ fontSize: fontSizeObj.smallFontSize }">{{ $t('保存') }}</el-button>
              <el-button @click="cancel" size="small" :style="{ fontSize: fontSizeObj.smallFontSize }">{{ $t('取消') }}</el-button>
            </div>
          </template>
        </ul>
      </div>
    </el-main>
    <y9Dialog v-model:config="dialogConfig">
      <opinionHistory ref="opinionHistoryRef" :processSerialNumber="processSerialNumber" :opinionframemark="opinionframemark"/>
    </y9Dialog>
    
  </el-container>
</template>

<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive, inject, computed} from 'vue';
import opinionHistory from "@/views/opinion/opinionHistory.vue";
import {commonSentencesList,getOpinionList,countOpinionHistory,personalComment,saveOpinion,delOpinion,updateUseNumber} from "@/api/flowableUI/opinion";
import {changeChaoSongState} from "@/api/flowableUI/chaoSong";
import settings from '@/settings';
import { useRoute,useRouter } from 'vue-router';
import phoneImg from "@/assets/phone.png";
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo')||{}; 
const props = defineProps({
    opinionframemark:String,
    minHeight:String,
    opinionName:String
})

const currentrRute = useRoute()
const data = reactive({
      textareaStyle:{"padding":'10px'},
      commonList:[],
      opinionId:'',
      userId:'',
      userName:'',
      deptId:'',
      selectTime:'',
      opinionModel:{},
      opinionInput:'',
      processSerialNumber:'',
      opinionframemark:'',
      loading:false,
      opinionShow:false,
      opinionOptShow:true,
      opinionImgShow:true,
      addable:{},
      opinionName:'',
      opinionContent:'',
      oldContent:'',
      opinionList:[],
      basicData:{},
      historyShow:false,
      headerheight:'0px',
      phone: phoneImg,
      suggeststyle:'',
      ulstyle:'',
      opinionHistoryRef:'',
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
});

let {
  textareaStyle,opinionId,userId,userName,deptId,selectTime,opinionModel,opinionInput,processSerialNumber,opinionframemark,
  loading,opinionShow,opinionImgShow,addable,opinionName,opinionContent,oldContent,opinionList,basicData,opinionOptShow,
  historyShow,headerheight,phone,suggeststyle,ulstyle,opinionHistoryRef,dialogConfig,commonList
	} = toRefs(data);

  defineExpose({ initOpinion,addable,opinionName})
  
  function initOpinion(data){
      if(props.minHeight != undefined && props.minHeight != null && props.minHeight != ""){//设置意见框最小高度
        suggeststyle.value = "min-height:"+props.minHeight;
        ulstyle.value = "min-height:"+props.minHeight;
      }
      basicData.value = data;
      opinionList.value = [];
      addable.value = {};
      opinionName.value = props.opinionName;
      getOpinionList(basicData.value.processSerialNumber,basicData.value.taskId,basicData.value.itembox,props.opinionframemark,
      basicData.value.itemId,basicData.value.taskDefKey,basicData.value.activitiUser).then(res => {
          if(res.success){
            let resdata = res.data;
            if(resdata.length == 1){
              addable.value = resdata[0];
            }else{
              addable.value = resdata[resdata.length - 1];
              for(let i = 0; i < resdata.length - 1; i++){
                resdata[i].opinion.showContent = resdata[i].opinion.content;
                if(resdata[i].opinion.tenantId != 'null' && resdata[i].opinion.tenantId != null && resdata[i].opinion.tenantId.indexOf('mobile') > -1){
                  resdata[i].opinion.showContent += '<img class="pencil2" style="height: 23px;width: 25px;" :title="$t(`移动端签阅`)" src="'+phone.value+'" />';
                }
                opinionList.value.push(resdata[i]);
              }
            }
          }
      });
      if(currentrRute.path.indexOf("/print") == -1 && settings.opinion_History){//打印表单不获取,opinion_History开启
        countOpinionHistory(basicData.value.processSerialNumber,props.opinionframemark).then(res => {
          if(res.success && res.data > 0){
            historyShow.value = true;
            headerheight.value = "25px";
          }
        });
      }
    }

    getCommonSentencesList();
    function getCommonSentencesList(){
      commonSentencesList().then(res => {
        commonList.value = res.data;
      });
    }

    function selectComment(id){
      let common = commonList.value.filter(item => item.id == id);
      opinionContent.value = opinionContent.value + common[0].content;
      updateUseNumber(common[0].id);
    }

    const emits = defineEmits(['opinion_click']);
    function addOpinion(type){
      opinionShow.value = true;
      opinionImgShow.value = false;
    }

    function editOpinion(item,type){
      let content = item.content.replaceAll('<br>','\n');
      opinionId.value = item.id;
      opinionContent.value = content;
      opinionShow.value = true;
      opinionImgShow.value = false;
      opinionOptShow.value  = false;
      personalComment(opinionId.value).then(res => {
        //opinionInput.value.focus();
        if(res.success){
          opinionModel.value = res.data.opinion;
          //opinionContent.value = opinionModel.value.content;
          userName.value = opinionModel.value.userName;
          userId.value = opinionModel.value.userId;
          deptId.value = opinionModel.value.deptId;
          selectTime.value = res.data.date;
          oldContent.value = res.data.opinion.content;
        }
      });
    }

    function cleardata(){
      opinionId.value = "";
      opinionContent.value = "";
      opinionModel.value = {};
      userName.value = "";
      userId.value = "";
      deptId.value = "";
      oldContent.value = "";
    }

    function deleteOpinion(id){
      ElMessageBox.confirm(t("确定删除该意见?"), t("提示"), {
        confirmButtonText: t("确定"),
        cancelButtonText: t("取消"),
        type: "info",
        appendTo: '.opinionList-container'
      }).then(() => {
        delOpinion(id).then(res => {
          if(res.success){
            ElMessage({type: 'success', message: res.msg,offset:65, appendTo: '.opinionList-container'});
            if(id == opinionId.value){
              cleardata();
              opinionImgShow.value = true;
            }
            initOpinion(basicData.value);
            if(basicData.value.itembox == "yuejian"){//抄送件删除意见后，更新批阅件状态
              // changeChaoSongState(basicData.value.chaosongId,"delete");
            }
          }else{
            ElMessage({type: 'error', message: res.msg,offset:65, appendTo: '.opinionList-container'});
          }
        });
      }) .catch(() => {
        ElMessage({ type: "info", message: t("已取消删除") ,offset:65, appendTo: '.opinionList-container'});
      }); 
    }

    function cancel(){
      opinionShow.value = false;
      opinionImgShow.value = true;
      opinionOptShow.value  = true;
    }

    function showOpinionHistory(){
      processSerialNumber.value = basicData.value.processSerialNumber;
      opinionframemark.value = props.opinionframemark;
      Object.assign(dialogConfig.value,{
        show:true,
        width:'75%',
        title:computed(() => t('意见留痕')),
        showFooter:false
      });
    }

    function saveChange(){//保存未保存的意见内容
      if(opinionContent.value != oldContent.value){
        saveOrUpdateOpinion();
      }
    }

    function saveOrUpdateOpinion(){
      if(opinionId.value == ""){
        opinionModel.value.id = opinionId.value;
        opinionModel.value.opinionFrameMark = props.opinionframemark;
        opinionModel.value.processSerialNumber = basicData.value.processSerialNumber;
        opinionModel.value.processInstanceId = basicData.value.processInstanceId;
        opinionModel.value.taskId = basicData.value.taskId;
        opinionModel.value.userId = userId.value;
        opinionModel.value.deptId = deptId.value;
        opinionModel.value.createDate = selectTime.value;
      }
      opinionModel.value.userId = userId.value;
      opinionModel.value.deptId = deptId.value;
      opinionModel.value.createDate = selectTime.value;
      opinionModel.value.content = opinionContent.value;
      if(opinionContent.value == ""){
        ElMessage({type: 'error', message: t("内容不能为空"),offset:65, appendTo: '.opinionList-container'});
        return;
      }
      let jsonData = JSON.stringify(opinionModel.value).toString();
      
      saveOpinion(jsonData).then(res => {
        if(res.success){
          ElMessage({type: 'success', message: res.msg, appendTo: '.opinionList-container'});
          //emits("childFunction",basicData.value);
          initOpinion(basicData.value);
          if(opinionId.value == ""){
            opinionId.value = res.data.id;
            opinionModel.value = res.data;
          }
          opinionShow.value = false;
          opinionImgShow.value = false;
          opinionOptShow.value  = true;
          oldContent.value = opinionModel.value.content;
          if(basicData.value.itembox == "yuejian"){//抄送件填写意见后，更新批阅件状态
            // changeChaoSongState(basicData.value.chaosongId,"add");
          }
        }else{
          ElMessage({type: 'error', message: res.msg,offset:65, appendTo: '.opinionList-container'});
        }
      });
  }
</script>

<style>
  .suggest {
    display: inline-block;
    font-size: v-bind('fontSizeObj.baseFontSize');
    height: auto;
    margin: 0;
    min-height: 150px;
    resize: none;
    width: 99.9%;
    border: 0;
  }
  .suggest ul {
    height: auto;
    margin: 0 auto;
    min-height: 150px;
    width: 100%;
    padding: 0;
  }
  .suggest ul li {
    font-size: v-bind('fontSizeObj.baseFontSize');
    list-style: none;
  }
   .suggest ul li div{
    line-height: 25px;
  }
  .suggest ul li a {
    float: right;
    overflow: hidden;
    text-align: right;
    text-overflow: ellipsis;
    /* white-space: nowrap; */
    width: 100%;
    border-bottom: 1px dashed #aaa;
    color: blue;
    margin-bottom: 5px;
    line-height:18px;
  }

  .textareaCss .el-textarea__inner{
    padding:5px !important;
    margin-bottom:5px;
    box-shadow: 0 0 0 1px var(--el-input-border-color, var(--el-border-color)) inset !important;
  }

  .textareaCss .el-input__count {
    color: var(--el-color-primary) !important;
  }

  .opinionSelectCss .el-input--small .el-input__inner {
    height: 24px !important;
    line-height: 24px !important;
  }
  
  .opinionSelectCss .el-input--small .el-input__wrapper {
    box-shadow: 0 0 0 1px var(--el-input-border-color, var(--el-border-color)) inset !important;
  }

  .pencil {
    border: 0 none;
    cursor: pointer;
    display: inline-block;
    height: 16px;
    margin-right: 0.3vw;
    vertical-align: top;
    width: 15px;
  }
  .pencil2 {
    border: 0 none;
    cursor: pointer;
    height: 15px;
    margin-right: 0.5vw;
    vertical-align: middle;
    width: 15px;
  }
</style>

<style scoped lang="scss">
.opinionList-container {
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