<template>
    <el-container style="height:600px;"
      v-loading="loading"
      :element-loading-text="$t('正在发送中')"
      element-loading-spinner="el-icon-loading"
      element-loading-background="rgba(0, 0, 0, 0.8)"
      class="csUserChoise">
      <el-container>
        <el-aside width="50%" style="height:525px;border:0;">
            <csTree ref="csTreeRef" :basicData="basicData" @onTreeDbClick="selectedNode"/>
        </el-aside>
        <div style="height: 525px;padding-top: 0px;text-align: center;width: 11.8%;display: flex;
        flex-direction: column;justify-content: center;align-items: center;">
           <!-- <el-button type="primary" size="small" style="width:80%;margin-left:10%;height:35px;" @click="selectedToRight">右移<i class="el-icon-d-arrow-right"></i></el-button> -->
           <el-divider direction="vertical" />
            <div class="moveRight" @click="selectedToRight">{{ $t('右移') }}</div>
          <el-divider direction="vertical" />
         </div>
        <el-main width="38.2%" style="height:525px;">
          <el-tabs v-model="activeName" class="usertab" @tab-click="tabclick">
            <el-tab-pane :label="$t('收件人')" name="addressee" style="height: 480px;">
              <div style="height: 94%;">
                <el-table header-row-class-name="table-header" style="width: 100%;" :data="userChoice" height="480" @row-dblclick="delPerson">
                  <el-table-column prop="name" :label="$t('名称')" width="auto">
                    <template #default="name_cell">
                      <i v-if="name_cell.row.type=='Person' && name_cell.row.sex=='0'" style="vertical-align: middle;" class="ri-women-line" />
                      <i v-else-if="name_cell.row.type=='Person' && name_cell.row.sex=='1'" style="vertical-align: middle;" class="ri-men-line" />
                      <i v-else-if="name_cell.row.type=='Position'" style="vertical-align: middle;" class="ri-shield-user-line" />
                      <i v-else-if="name_cell.row.type=='customGroup'" style="vertical-align: middle;" class="ri-shield-star-line" />
                      <i v-else-if="name_cell.row.type=='Department'" style="vertical-align: middle;" class="ri-slack-line" />
                      {{name_cell.row.name}}
                    </template>
                  </el-table-column>
                  <el-table-column prop="opt" align="center" :label="$t('操作')" width="85">
                    <template #default="opt_cell">
                      <i class="ri-close-line" @click="delPerson(opt_cell.row)" :style="{fontSize: fontSizeObj.largeFontSize,color:'#586cb1',cursor: 'pointer'}"></i>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>
            <el-tab-pane :label="$t('短信提醒')" name="SMSReminder" style="height: 525px;">
              <el-checkbox v-model="awoke">{{ $t('短信提醒') }}</el-checkbox>
              <el-checkbox v-model="awokeShuMing" @change="addShuMing">{{ $t('是否添加署名') }}</el-checkbox>
              <table style="width:100%;border-spacing: 0;" class="table-input">
                  <tr>
                    <td style="border: 1px solid rgb(220, 223, 230);">
                      <el-input type="textarea" resize="none" :rows="19" :placeholder="$t('请输入内容')" v-model="awokeText" maxlength="100"></el-input>
                    </td>
                  </tr>
                  <tr>
                    <td style="border: 1px solid rgb(220, 223, 230);border-top-width: 0px;">
                      <el-input style="box-shadow: 0 0 0 0px var(--el-input-border-color, var(--el-border-color)) inset;" :placeholder="$t('是否署名')" v-model="lastfixSmsContext" :readonly=true ></el-input>
                    </td>
                  </tr>
              </table>
            </el-tab-pane>
          </el-tabs>
        </el-main>
      </el-container>
      <el-footer style="text-align: right;padding: 10px 0px;">
            <el-button type="primary" @click="send()" :size="fontSizeObj.buttonSize"
              :style="{ fontSize: fontSizeObj.baseFontSize }"><i class="ri-share-forward-line" :style="{ fontSize: fontSizeObj.mediumFontSize}"></i>{{ $t('发送') }}</el-button>
            <el-button type="primary" plain @click="resetUser()" :size="fontSizeObj.buttonSize"
              :style="{ fontSize: fontSizeObj.baseFontSize }"><i class="ri-delete-bin-line"  :style="{ fontSize: fontSizeObj.mediumFontSize}"></i>{{$t('清空') }}</el-button>
        </el-footer>
    </el-container>
</template>

<script lang="ts" setup>
import {inject} from 'vue';
import csTree from "@/views/chaoSong/csPersonTree.vue";
import {chaoSongSave} from "@/api/flowableUI/chaoSong";
import {useFlowableStore} from "@/store/modules/flowableStore";
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo'); 
const props = defineProps({
  basicData: {
    type: Object,
    default:() => { return {} }
  },
  dialogConfig: {
    type: Object,
    default:() => { return {} }
  },
})

const emits = defineEmits(['csRefreshCount','update-BasicData']);
const flowableStore = useFlowableStore()
const data = reactive({
      csTreeRef:'',
      loading:false,
      activeName:'addressee',
      awoke:'false',
      awokeShuMing:'false',
      awokeText:'',
      lastfixSmsContext:'',
      smsPersonId:"",
      userChoice:[],
    });

    let {
      csTreeRef,
      loading,
      activeName,
      awoke,
      awokeShuMing,
      awokeText,
      lastfixSmsContext,
      smsPersonId,
      userChoice,
    } = toRefs(data);

    function tabclick(){
      awokeText.value = t("您已收到一份标题为【")+flowableStore.getDocumentTitle+t("】的阅件，请及时办理。");
    }

    function addShuMing(val){
      if(val){
        lastfixSmsContext.value = "-- 姓名";
      }else{
        lastfixSmsContext.value = "";
      }
    }

    function selectedToRight(){
      // let checkNode = csTreeRef.value.checkNodes;
      let checkNode = csTreeRef.value?.y9TreeRef?.getCheckedNodes();
      if(checkNode.length === 0){
        ElMessage({type: 'error', message: t("请勾选科室或收件人"),offset:65, appendTo: '.csUserChoise'});
        return;
      }
      for(let i = 0; i < checkNode.length; i++){
        selectedNode(checkNode[i]);
      }
    }

    function selectedNode(checkNode){
      let user = {};
      user.name = checkNode.name;
      user.id = checkNode.id;
      user.type = checkNode.orgType;
      user.sex = checkNode.sex;
      user.opt = "";
      let ischeck = true;
      for(let j = 0; j < userChoice.value.length; j++){
          if(userChoice.value[j].id === user.id){
            ischeck = false;
          }
        }
        if(ischeck){
          userChoice.value.push(user);
        }
    }

    function resetUser(){
      userChoice.value = [];
      csTreeRef.value.checkNodes = [];
    }

    function delPerson(row){
      let newuserChoice = [];
      for(let j = 0; j < userChoice.value.length; j++){
        if(userChoice.value[j].id != row.id){
          newuserChoice.push(userChoice.value[j]);
        }
      }
      userChoice.value = newuserChoice;
    }
    function send(){
      if(userChoice.value.length == 0){
        ElMessage({type: 'error', message: t("请选择收件人"),offset:65, appendTo: '.csUserChoise'});
        return;
      }
      let userChoiceId = [];
      for(let item of userChoice.value){
        let id = "";
        if(item.type == "Person"){
          id = "3:" + item.id;
        }else if(item.type == "Department"){
          id = "2:" + item.id;
        }else if(item.type == "customGroup"){
          id = "7:" + item.id;
        }else if(item.type == "Position"){
          id = "6:" + item.id;
        }
        userChoiceId.push(id);
      }
      loading.value = true;
      chaoSongSave(props.basicData.processInstanceId,props.basicData.itemId,props.basicData.processSerialNumber,props.basicData.processDefinitionKey,
        userChoiceId.join(";"),awoke.value,awokeShuMing.value,awokeText.value,smsPersonId.value).then(res => {
        loading.value = false;
        if (res.success) {
          ElMessage({ type: "success", message: res.msg ,offset:65, appendTo: '.csUserChoise'});
          if(props.basicData.processInstanceId == "" || props.basicData.processInstanceId == undefined){//新建就抄送，处理启动流程后返回的流程实例id
            let baseData = {
              processInstanceId: res.data.processInstanceId,
              taskId: res.data.taskId,
              itembox:"todo",
            };
            emits("update-BasicData",baseData);
          }
          emits("csRefreshCount");
          props.dialogConfig.show = false;
        } else {
          ElMessage({ type: "error", message: res.msg ,offset:65, appendTo: '.csUserChoise'});
        }
      }).catch(err => {
        loading.value = false;
        ElMessage({ type: "info", message: t("发生异常") ,offset:65, appendTo: '.csUserChoise'});
      });
    }
</script>

<style >
  .csUserChoise .el-tabs__header{
    width: 60%;
    background-color: #fff;
    margin:0px;
  }
  
  .csUserChoise .moveRight{
        width: 50px;
        height: 50px;
        background: url('@/assets/youyi.png') center center no-repeat;
        background-size: 60px auto;
        line-height: 45px;
        font-size: v-bind('fontSizeObj.baseFontSize');
        color: #586cb1;
        margin-left: -0.5vw;
    }
    .csUserChoise .el-divider--vertical {
      height: 220px;
  }
  .csUserChoise .el-main{
    background-color: #fff;
    border: 0px solid #ccc;
    padding: 0;
    overflow: hidden;
  }
   .csUserChoise .el-aside{
    background-color: #fff;
    border: 1px solid #ccc;
    padding: 0;
  }

  .csUserChoise .el-tabs__nav-scroll{
    padding-left: 5px;
  }

  .csUserChoise .el-tabs__item {
    padding: 0 10px;
    font-size: v-bind('fontSizeObj.baseFontSize');
  }

  .el-tabs__item {
    color: #c0c4cc;;
}

  /* .csUserChoise .el-button--small, .el-button--small.is-round{
    padding: 0px;
  } */

  .csUserChoise .el-checkbox {
    height: 40px;
  }
  .csUserChoise .el-menu--horizontal > .el-menu-item{
    height: 40px;
    line-height: 40px;
    font-size: v-bind('fontSizeObj.baseFontSize');
  }
  
  .csUserChoise .el-tabs__nav-wrap::after{
    height: 0px;
    background-color: #ccc;
  }

  .csUserChoise .el-button--primary.is-plain {
    --el-button-bg-color: white;
 }
  .csUserChoise .table-header .el-table__cell{
    background-color: #ebeef5;
    color:#9ba7d0;
  }

  .csUserChoise .el-table__cell{
    font-size: v-bind('fontSizeObj.baseFontSize');
  }

  .csUserChoise .el-table td, .el-table th{
    padding: 6px 0;
  }
  .csUserChoise .el-input{
    display: block;
    width: 100%;
  }
  .csUserChoise .el-textarea__inner{
    box-shadow: 0 0 0 0px var(--el-input-border-color, var(--el-border-color)) inset;
    padding: 0px 11px;
  }
  .csUserChoise .el-input__wrapper {
    /* box-shadow: 0 0 0 0px var(--el-input-border-color,var(--el-border-color)) inset; */
    width: 100%;
    height: 30px;
    border-radius: 50px;
    font-size: v-bind('fontSizeObj.baseFontSize');
  }
  .table-input .el-input__wrapper{
    box-shadow: 0 0 0 0px var(--el-input-border-color, var(--el-border-color)) inset;
  }
  .csUserChoise .el-textarea{
    display: block;
  }
  .csUserChoise .el-table::before{
    height: 0;
  }
  .send_button:hover{
    background-color: #409eff;
    color: #fff;
  }
  .reset_button:hover{
    background-color: #e6a23c;
    color: #fff;
  }

  #tab-SMSReminder{
    display: none;
  }
</style>

<style lang="scss" scoped>
:deep(.el-table__empty-text) {
  font-size: v-bind('fontSizeObj.baseFontSize');
}

.csUserChoise {
  :global(.el-message .el-message__content) {
    font-size: v-bind('fontSizeObj.baseFontSize');
  }

}
</style>