<template>
  <el-container style="height:600px;"
    v-loading="loading"
    :element-loading-text="$t('正在发送中')"
    element-loading-spinner="el-icon-loading"
    element-loading-background="rgba(0, 0, 0, 0.8)"
    class="userChoise4Custom"
  >
    <el-header style="height:30px;padding:0;margin-bottom: 15px;">
      <el-tooltip effect="dark" :content="$t(remindContent)" placement="top" >
        <el-tag size="large" style="display: block;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;width: 100%;line-height: 3vh;text-indent: 0.5vw;" :style="{fontSize: fontSizeObj.baseFontSize}">
            <i class="ri-information-line" :style="{fontSize:fontSizeObj.mediumFontSize,verticalAlign: 'middle'}"></i>{{$t(remindContent)}}
          </el-tag>
        </el-tooltip>
    </el-header>
    <el-container>
      <el-aside width="50%" style="height:480px;border:0;">
        <personTree :basicData="basicData" ref="personTreeRef" @org-click="selectedNode"/>
      </el-aside>
      <div style="height: 480px;padding-top: 0px;text-align: center;background-color: fff;width: 11.8%;
      display: flex;flex-direction: column;align-items: center;justify-content: center;">
        <el-divider direction="vertical" />
          <el-checkbox :class="ishide" v-model="zhuxieban" :checked="true" @change="zhuxiebanChange">{{ $t('主协办') }}</el-checkbox>
          <div class="moveRight" @click="selectedToRight">{{ $t('右移') }}</div>
          <el-divider direction="vertical" />
      </div>
      <el-main width="38.2%" style="height:480px;">
        <div v-if="moveShow" style="color: #586cb1;width: 120px;float: right;position: relative;height: 35px;line-height: 35px;text-align: right;margin-right: 5px;">
          <el-link @click="moveTop" :underline="false"><i class="ri-arrow-up-line"></i>{{ $t('上移') }}</el-link>
          <el-link @click="moveBottom" :underline="false" style="margin-left:5px;"><i class="ri-arrow-down-line"></i>{{ $t('下移') }}</el-link>
        </div>
        <el-tabs v-model="activeName" class="usertab" @tab-click="tabclick">
          <el-tab-pane :label="$t('收件人')" name="addressee" style="height: 360px;">
            <div style="height: 94%;">
              <el-table header-row-class-name="table-header" style="width: 100%;" height="440" :data="userChoice" @row-dblclick="delPerson" highlight-current-row @current-change="handleCurrentChange">
                <el-table-column prop="name" :label="$t('名称')" width="auto">
                  <template #default="name_cell">
                    <i v-if="name_cell.row.type=='Person' && name_cell.row.sex=='0'" style="vertical-align: middle;" class="ri-women-line" />
                    <i v-else-if="name_cell.row.type=='Person' && name_cell.row.sex=='1'" style="vertical-align: middle;" class="ri-men-line" />
                    <i v-else-if="name_cell.row.type=='customGroup'" style="vertical-align: middle;" class="ri-shield-star-line" />
                    <i v-else-if="name_cell.row.type=='Department'" style="vertical-align: middle;" class="ri-slack-line" />
                    {{name_cell.row.name}}
                  </template>
                </el-table-column>
                <el-table-column v-if="userChoiseData.multiInstance == 'parallel' || userChoiseData.multiInstance == 'sequential'" prop="zxbSign" align="center" :label="$t(optlabel)" width="85">
                    <template #default="zxbSign_cell">
                        <el-link type="primary" :title="$t('点击设为主办')" v-if="zxbSign_cell.row.zxbSign == '主办' || zxbSign_cell.row.zxbSign == '协办'" @click="setSponsor(zxbSign_cell.row)" :underline="false">
                          <span v-if="zxbSign_cell.row.zxbSign == '主办'" style="color:red;">{{zxbSign_cell.row.zxbSign}}</span>
                          <span v-if="zxbSign_cell.row.zxbSign == '协办'" style="color:#586cb1;">{{zxbSign_cell.row.zxbSign}}</span>
                        </el-link>
                        <font v-else>{{zxbSign_cell.row.zxbSign}}</font>
                    </template>
                  </el-table-column>
                  <el-table-column prop="opt" align="center" :label="$t('操作')" width="85">
                    <template #default="opt_cell">
                      <i class="ri-close-line" @click="delPerson(opt_cell.row)" style="color:#586cb1;cursor: pointer;"
                      :style="{fontSize: fontSizeObj.largeFontSize}"></i>
                    </template>
                  </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>
          <el-tab-pane :label="$t('短信提醒')" name="SMSReminder" style="height: 360px;">
              <el-checkbox v-model="awoke">{{ $t('短信提醒') }}</el-checkbox>
              <el-checkbox v-model="awokeShuMing" @change="addShuMing">{{ $t('是否添加署名') }}</el-checkbox>
              <table style="width:100%;border-spacing: 0;" class="table-input">
                  <tr>
                    <td style="border: 1px solid rgb(220, 223, 230);">
                      <el-input type="textarea" resize="none" :rows="16" :placeholder="$t('请输入内容')" v-model="awokeText" maxlength="100"></el-input>
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
    <el-footer style="text-align: center;padding: 10px 0px;">
            <el-button :size="fontSizeObj.buttonSize"
              :style="{ fontSize: fontSizeObj.baseFontSize }" type="primary" @click="send()"><i class="ri-share-forward-line" :style="{fontSize: fontSizeObj.mediumFontSize}"></i>{{ $t('发送') }}</el-button>
            <el-button :size="fontSizeObj.buttonSize"
              :style="{ fontSize: fontSizeObj.baseFontSize }" type="primary" plain  @click="resetUser()"><i class="ri-delete-bin-line" :style="{fontSize: fontSizeObj.mediumFontSize}"></i>{{ $t('清空') }}</el-button>
        </el-footer>
  </el-container>
</template>

<script lang="ts" setup>
import {inject} from 'vue';
import personTree from "@/views/workForm/personTree.vue";
import {getUserChoiseData,getUserCount} from "@/api/flowableUI/userChoise";
import {useFlowableStore} from "@/store/modules/flowableStore";
import { useI18n } from 'vue-i18n';
const { t } = useI18n();
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo')||{}; 
const props = defineProps({
  basicData: {
    type: Object,
    default:() => { return {} }
  },
})

const emits = defineEmits(['set-UserChoise']);
const flowableStore = useFlowableStore();
const data = reactive({
    personTreeRef:'',
    loading:false,
    zhuxieban:true,
    processSerialNumber:'',
    processInstanceId:'',
    taskId:'',
    activeName:'addressee',
    routeToTask:'',
    awoke:'false',
    awokeShuMing:'false',
    awokeText:'',
    lastfixSmsContext:'',
    remindContent:'',
    userChoiseData:{},
    userChoice:[],
    ishide:'ishide',
    optlabel:'',
    moveShow:false,//上下移动显示
    currentRow:null,
  });   

  let {
    personTreeRef,loading,zhuxieban,processSerialNumber,processInstanceId,
    taskId,activeName,routeToTask,awoke,awokeShuMing,awokeText,lastfixSmsContext,
    remindContent,userChoiseData,userChoice,ishide,optlabel,moveShow,currentRow,
	} = toRefs(data);

  function handleCurrentChange(val) {
      currentRow.value = val;
  }

  function tabclick(){
      awokeText.value = "您已收到一份标题为【"+flowableStore!.getDocumentTitle!+"】的办件，请及时办理。";
  }

  function show(routeToTaskKey,orgList){
      routeToTask.value = routeToTaskKey;
      userChoice.value = [];
      currentRow.value = null;
      moveShow.value = false;
      getUserChoiseData(props.basicData.itemId,routeToTask.value,props.basicData.processDefinitionId,
      props.basicData.taskId,props.basicData.processInstanceId).then(res => {
          userChoiseData.value = res.data;
          ishide.value = "ishide";
          optlabel.value = "";
          if(userChoiseData.value.multiInstance === "parallel"){
            ishide.value = "";
            if(zhuxieban.value){
              optlabel.value = "主协办";
            }
            remindContent.value = "办理说明：当前办理方式为并行办理，下一任务的收件人为多个人，这些收件人可以同时办理当前任务。";
          }else if(userChoiseData.value.multiInstance === "sequential"){
            moveShow.value = true;
            optlabel.value = "办理顺序";
            remindContent.value = "办理说明：当前办理方式为串行办理，下一任务的收件人为多个人，这些收件人可以顺序办理当前任务。";
          }else if(userChoiseData.value.multiInstance === "subProcess"){
            remindContent.value = "办理说明：当前办理方式为子流程，下一任务的收件人为多个人，这些收件人可以各自办理当前任务。";
          }else{
            remindContent.value = "办理说明：当前办理方式为单人办理，下一任务的收件人若为多人，这些收件人可以进行抢占签收办理。";
          }

          if(orgList != null && orgList != undefined && orgList.length > 0){//选人回显
            orgList.forEach(function(element,index){
              let item = JSON.parse(JSON.stringify(element));
              item.id = element.id.split(":")[1];
              if(userChoiseData.value.multiInstance == "sequential"){
                item.zxbSign = index + 1;
              }
              userChoice.value.push(item);
            });
          }

          personTreeRef.value.findPermUser(userChoiseData);
      });
    }

  function zhuxiebanChange(val){//主协办勾选
      if(val){
        optlabel.value = "主协办";
        for(let j = 0; j < userChoice.value.length; j++){
          if(j === 0){
            userChoice.value[j].zxbSign = t('主办');
          }else{
            userChoice.value[j].zxbSign = t('协办');
          }
        }
      }else{
        optlabel.value = "";
        for(let j = 0; j < userChoice.value.length; j++){
          userChoice.value[j].zxbSign = '';
        }
      }
    }

    function selectedToRight(){//右移
      let checkNode = personTreeRef.value.checkNodes;
      
      if(checkNode.length === 0){
        ElMessage({type: 'error', message: t("请勾选科室或收件人"),offset:65, appendTo: '.userChoise4Custom'});
        return;
      }
      if(userChoiseData.value.multiInstance == "common"){
        if(checkNode.length > 1){
          ElMessage({type: 'error', message: t("当前办理方式为单人办理，只能选择一个办理人！"),offset:65, appendTo: '.userChoise4Custom'});
          return;
        }
      }
      for(let i = 0; i < checkNode.length; i++){
        selectedNode(checkNode[i]);
      }
    }
    
    function selectedNode(checkNode){//单击选择人员
      let user = {};
      user.name = checkNode.name;
      user.id = checkNode.id;
      user.type = checkNode.orgType;
      user.sex = checkNode.sex;
      user.zxbSign = "";
      let ischeck = true;
      //单人节点,报销业务负责人审核,重定位选人不能选择部门,用户组
      if(userChoiseData.value.multiInstance == "common" || "yewufuzerenshenhe" == routeToTask.value){
        if(user.type == "Department" || user.type == "customGroup"){
          return;
        }
        //单人节点,报销业务负责人审核只能选择单人
        if(userChoiseData.value.multiInstance == "common" || "yewufuzerenshenhe" == routeToTask.value){
          userChoice.value = [];
        }
      }
      
      if(userChoice.value.length === 0){
        if(optlabel.value == "主协办"){
          user.zxbSign = t("主办");
        }else if(optlabel.value == "办理顺序"){
          user.zxbSign = "1";
        }
        userChoice.value.push(user);
      }else{
        if(optlabel.value == "主协办"){
          user.zxbSign = t("协办");
        }else if(optlabel.value == "办理顺序"){
          user.zxbSign = (userChoice.value.length + 1).toString();
        }
        for(let j = 0; j < userChoice.value.length; j++){
          if(userChoice.value[j].id === user.id){
            ischeck = false;
          }
        }
        if(ischeck){
          userChoice.value.push(user);
        }
      }
    }

    function setSponsor(row){//设置主办
      for(let j = 0; j < userChoice.value.length; j++){
        userChoice.value[j].zxbSign = t('协办');
        if(userChoice.value[j].id === row.id){
          userChoice.value[j].zxbSign = t('主办');
        }
      }
    }

    function addShuMing(val){//添加署名
      if(val){
        lastfixSmsContext.value = "-- " + userChoiseData.value.userName;
      }else{
        lastfixSmsContext.value = "";
      }
    }

    function resetUser(){//重置选人
      userChoice.value = [];
      personTree.value.checkNodes = [];
    }

   function delPerson(row){//双击删除选人
      let newuserChoice = [];
      for(let item of userChoice.value){
        if(item.id != row.id){
          if(optlabel.value == "办理顺序"){
            item.zxbSign = (newuserChoice.length + 1).toString();
          } else if(optlabel.value == "主协办" && row.zxbSign == "主办"){
            if(newuserChoice.length == 0){
              item.zxbSign = t("主办");
            }
          }
          newuserChoice.push(item);
        }
      }
      userChoice.value = newuserChoice;
    }

    function send(){//发送
      if(userChoice.value.length == 0){
        ElMessage({type: 'error', message: t("请选择办理人"),offset:65, appendTo: '.userChoise4Custom'});
        return;
      }
      let userChoiceId = [];
      let userChoices = [];
      let sponsorGuid = "";
      for(let item of userChoice.value){
        let id = "";
        if(item.type == "Person"){
          id = "3:" + item.id;
        }else if(item.type == "Department"){
          id = "2:" + item.id;
        }else if(item.type == "customGroup"){
          id = "7:" + item.id;
        }
        userChoiceId.push(id);
        userChoices.push({id:id,name:item.name,type:item.type,zxbSign:item.zxbSign,sex:item.sex});
        if(item.zxbSign == "主办"){
          sponsorGuid = id;
        }
      }
      getUserCount(userChoiceId.join(";")).then(res => {
        if(res.success){
          if(res.data > 100){
            ElMessage({type: 'error', message: t("办理人数不能超过100人"),offset:65, appendTo: '.userChoise4Custom'});
            return;
          }
          //this.$parent.$parent.$parent.setUser(userChoice);
          emits('set-UserChoise',userChoices);
        }
      });
    }

   function moveBottom(){//下移
      if(currentRow.value != null){
        let num = parseInt(currentRow.value.zxbSign);
        if(num != userChoice.value.length){
          userChoice.value[num].zxbSign = num;
          currentRow.value.zxbSign = num + 1;
          userChoice.value[num-1] = userChoice.value.splice(num, 1, userChoice.value[num-1])[0];
        }
      }
    }

  function moveTop(){//上移
      if(currentRow.value != null){
        let num = parseInt(currentRow.value.zxbSign);
        if(num != 1){
          userChoice.value[num-2].zxbSign = num;
          currentRow.value.zxbSign = num - 1;
          userChoice.value[num-2] = userChoice.value.splice(num-1, 1, userChoice.value[num-2])[0];
        }
      }
    }

  defineExpose({
    show
  });
</script>

<style >
  .userChoise4Custom .el-tabs__header{
    width: 60%;
    background-color: #fff;
    margin:0px;
  }
 
  .userChoise4Custom .moveRight{
        width: 50px;
        height: 50px;
        background: url('@/assets/youyi.png') center center no-repeat;
        background-size: 60px auto;
        line-height: 45px;
        font-size: v-bind('fontSizeObj.baseFontSize');
        color: #586cb1;
        margin-left: -0.5vw;
    }
  .userChoise4Custom .el-divider--vertical {
      height: 190px;
  }
  .userChoise4Custom .el-checkbox {
    height: 40px;
  }

  .userChoise4Custom .el-textarea__inner{
    box-shadow: 0 0 0 0px var(--el-input-border-color, var(--el-border-color)) inset;
    padding: 0px 11px;
  }

  .table-input .el-input__wrapper{
    box-shadow: 0 0 0 0px var(--el-input-border-color, var(--el-border-color)) inset;
  }

  .userChoise4Custom .table-header .el-table__cell{
    background-color: #ebeef5;
    color:#9ba7d0;
  }
  .userChoise4Custom .el-main{
    background-color: #fff;
    border: 0px solid #ccc;
    padding: 0;
    overflow: hidden;
  }
  .userChoise4Custom .el-link [class*="el-icon-"] + span{
    margin-left: 0px;
  }
  .userChoise4Custom .el-aside{
    background-color: #fff;
    border: 1px solid #ccc;
    padding: 0;
  }

  .userChoise4Custom .el-tabs__nav-scroll{
    padding-left: 5px;
  }

  .userChoise4Custom .el-input__wrapper {
    /* box-shadow: 0 0 0 0px var(--el-input-border-color,var(--el-border-color)) inset; */
    width: 100%;
    height: 30px;
    border-radius: 50px;
  }

  .userChoise4Custom .el-tabs__item {
    padding: 0 10px;
    /* color: #c0c4cc; */
  }

  .el-tabs__item {
    color: #c0c4cc;;
}

  .userChoise4Custom .el-button--small, .el-button--small.is-round{
    padding: 0px;
  }
  
  .userChoise4Custom .el-tabs__nav-wrap::after{
    height: 0px;
    background-color: #ccc;
  }

  .userChoise4Custom .el-button--primary.is-plain {
    --el-button-bg-color: white;
 }

 .userChoise4Custom .el-menu--horizontal > .el-menu-item {
    height: 40px;
    line-height: 40px;
}

 .userChoise4Custom .el-button {
    min-width: 62px;
    font-size: v-bind('fontSizeObj.smallFontSize');
    height: 30px;
    line-height: 30px;
    box-shadow: 2px 2px 2px 1px rgb(0 0 0 / 6%);
    padding: 10px;
}

  .userChoise4Custom .el-table td, .el-table th{
    padding: 6px 0;
  }
  .userChoise4Custom .el-input{
    display: block; 
    width: 100%;
  }
  .userChoise4Custom .el-textarea{
    display: block;
  }
  .userChoise4Custom .el-table::before{
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

  .el-checkbox__label{
    padding-left: 2px;
  }

  .ishide{
    display:none;
  }

  .userChoise4Custom .el-aside{
    line-height: 40px;
    overflow: hidden;
  }

  #tab-SMSReminder{
    display: none;
  }
</style>

<style scoped>
/*message */
:global(.el-message .el-message__content) {
  font-size: v-bind('fontSizeObj.baseFontSize');
}
</style>