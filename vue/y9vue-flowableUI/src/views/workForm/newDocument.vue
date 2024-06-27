<template>
  <el-container v-loading="loading"
                :element-loading-text="$t(loadingtext)"
                :style="style"
                class="newDocument-container"
                element-loading-background="rgba(0, 0, 0, 0.8)"
                element-loading-spinner="el-icon-loading">
    <div :class="{ 'document-shrink': !processFlag}" class="document">
      <div class="buttonDiv1">
        <FloatButton :list="operationBtnList"/>
      </div>
      <div class="tab-nav">
        <Tabs v-model:activeName="activeName" :list="dataList" class="nav" @tab-click="tabClick"></Tabs>
      </div>

      <!-- 收缩 -->
      <div class="shrink" @click="handlerClickShrink">
        <div v-if="processFlag">
          <i class="ri-arrow-right-s-line"></i>
          <i class="ri-arrow-right-s-line" style="margin-left: -23px"></i>
        </div>
        <div v-else>
          <i class="ri-arrow-left-s-line"></i>
          <i class="ri-arrow-left-s-line" style="margin-left: -23px"></i>
        </div>
      </div>

      <div style="margin-top: 104px">
        <!-- {{ activeName }} -->
        <!-- 表单 -->
        <myFormRef v-show="activeName.indexOf('y9form') > -1" ref="myForm" :basicData="basicData" :processInstanceId="processInstanceId" @refreshCount="updateLeftListCount"/>
        <!-- 附件 -->
        <fileList v-if="activeName == 'attach'" ref="fileListRef" :basicData="basicData" :processSerialNumber="processSerialNumber"/>
        <!-- 关联流程 -->
        <associatedFileList v-if="activeName == 'associatedFile'" ref="associatedFileListRef" :basicData="basicData" :processSerialNumber="processSerialNumber"/>
        <!-- 沟通交流 -->
        <speakInfo v-if="activeName == 'speakInfo'" ref="speakInfoRef" :clickCount="clickCount" :processInstanceId="processInstanceId"/>
        <!-- 历程 -->
        <processListCom v-if="activeName == 'process'" ref="processListRef" :processInstanceId="processInstanceId"/>
        <!-- 流程图 -->
        <flowChart v-if="activeName == 'flowChart'" ref="flowchartRef" :processDefinitionId="basicData.processDefinitionId" :processInstanceId="processInstanceId"/>
      </div>
    </div>

    <div v-if="processFlag" :style="processStyle" class="process-content">
      <el-card style="height:100%;box-shadow:0px 0px 0px 0px #333;">
        <template #header>
          <div class="card-header">
            <span :style="{fontSize: fontSizeObj.mediumFontSize}">{{ $t('流转状态') }}</span>
          </div>
        </template>
        <div style="height: 94%;overflow-y: auto;overflow-x: hidden;display: flex;background-color: white; justify-content: center;align-items: self-start;">
          <!-- 没数据 显示暂无数据 -->
          <ProcessStatus v-if="processTimeLineList.length>0" :list="processTimeLineList"/>
          <el-empty v-else :description="$t('暂无数据')"/>
        </div>

        <!-- <div :class="opinion_content" style="height: 94%;overflow-y: auto;">
            <opinion ref="opinionRef" @childFunction="initOpinion" />
        </div> -->
      </el-card>
    </div>

    <y9Dialog v-model:config="dialogConfig" class="y9DialogClass">
      <customProcessCom v-if="dialogConfig.type=='customProcess'" ref="customProcessRef" :basicData="basicData" :dialogConfig="dialogConfig"/>
      <userChoise v-if="dialogConfig.type=='userChoise'" ref="userChoiseRef" :basicData="basicData" :fromType="$t(fromType)" :reposition="reposition" :routeToTask="routeToTask"/>
      <csUserChoise v-if="dialogConfig.type=='csUserChoise'" ref="csUserChoiseRef" :basicData="basicData" :dialogConfig="dialogConfig" @csRefreshCount="updateLeftListCount" @update-BasicData="setCsProcessData"/>
      <rollbackOrTakeback v-if="dialogConfig.type=='rollbackOrTakeback'" ref="rollbackOrTakebackRef" :basicData="basicData" :optType="optType"/>
      <specialComplete v-if="dialogConfig.type=='specialComplete'" ref="specialCompleteRef" :basicData="basicData"/>
      <multiInstance v-if="dialogConfig.type=='multiInstance'" ref="multiInstanceRef" :basicData="basicData" @refreshButton="refreshButton"/>
    </y9Dialog>

    <el-dialog v-model="dialogVisible" :title="$t('办结提示')" class="tishi" width="20%">
      <template v-if="y9UserInfo.tenantId == settings.risesoftTenantId">
        <span :style="{fontSize: fontSizeObj.mediumFontSize}">
          <i :style="{fontSize: fontSizeObj.extraLargeFont,verticalAlign: 'middle',marginRight: '-2px',color:'#FF7F50'}" class="ri-error-warning-line"></i>
          {{ $t('数据是否在数据中心公开显示?') }}'
        </span><br/>
        <el-radio-group v-model="infoOvert" style="margin-left: 3px;">
          <el-radio label="1">{{ $t('公开') }}<font color="red">({{ $t('所有人可搜索到该办件') }})</font></el-radio>
          <el-radio label="0">{{ $t('不公开') }}<font color="red">({{ $t('仅参与人可搜索到该办件') }})</font></el-radio>
        </el-radio-group>
      </template>
      <template v-else>
        <span :style="{fontSize: fontSizeObj.mediumFontSize}">
          <i :style="{fontSize: fontSizeObj.extraLargeFont,verticalAlign: 'middle',marginRight: '-2px',color:'#FF7F50'}" class="ri-error-warning-line"></i>
          {{ $t('确定办结该件') }}?
        </span>
      </template>
      <template #footer>
        <span class="dialog-footer">
          <el-button :size="fontSizeObj.buttonSize" :style="{ fontSize: fontSizeObj.baseFontSize }" class="global-btn-main"
                     @click="setInfoOvert()">{{ $t('办结') }}</el-button>
          <el-button :size="fontSizeObj.buttonSize" :style="{ fontSize: fontSizeObj.baseFontSize }" class="global-btn-third"
                     @click="dialogVisible = false">{{ $t('取消') }}</el-button>
        </span>
      </template>
    </el-dialog>
  </el-container>
</template>

<script lang="ts" setup>
import ProcessStatus from '@/components/Handling/ProcessStatus.vue';
import {computed, inject, onMounted, reactive} from 'vue';
import {ElMessage, ElMessageBox} from 'element-plus';
import {ntkoBrowser} from '@/assets/js/ntkobackground.min.js';
import myFormRef from "@/views/workForm/newForm.vue";
import fileList from "@/views/file/fileList.vue";
import associatedFileList from "@/views/associatedFile/associatedFileList.vue";
import processListCom from "@/views/process/processList.vue";
import speakInfo from "@/views/speakInfo/speakInfo.vue";
import userChoise from "@/views/workForm/userChoise.vue";
import csUserChoise from "@/views/chaoSong/csUserChoise.vue";
import rollbackOrTakeback from "@/views/workForm/rollbackOrTakeback.vue";
import specialComplete from "@/views/workForm/specialComplete.vue";
import multiInstance from "@/views/multiInstance/list.vue";
import customProcessCom from "@/views/workForm/customProcess.vue";
import flowChart from "@/views/flowchart/index.vue";
import {addData, getTodoData} from "@/api/flowableUI/index";
import {delOfficeFollow, saveOfficeFollow} from "@/api/flowableUI/follow";
import {openDraft} from "@/api/flowableUI/draft";
import {buttonApi, forwarding} from "@/api/flowableUI/buttonOpt";
import settings from '@/settings';
import {useRoute, useRouter} from 'vue-router'
import {useFlowableStore} from "@/store/modules/flowableStore";
import {processList} from "@/api/flowableUI/process";
import y9_storage from '@/utils/storage';
import {useSettingStore} from "@/store/modules/settingStore";
import {chaoSongSave} from "@/api/flowableUI/chaoSong";
import {useI18n} from 'vue-i18n';

const {t} = useI18n();
const settingStore = useSettingStore()
let style = 'height:calc(100vh - 105px);margin-top: 45px;';
let processStyle = 'height: calc(100vh - 106px) !important;';
if (settingStore.pcLayout == 'Y9Horizontal') {
  style = 'height:calc(100vh - 160px);margin-top: 45px;';
  processStyle = 'height: calc(100vh - 162px) !important;';
}
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo') || {};
const router = useRouter()
// 获取当前路由
const currentrRute = useRoute()
const flowableStore = useFlowableStore()
let baseData1 = {
  tenantId: '',
  userId: '',
  processSerialNumber: '',
  processDefinitionId: '',
  processInstanceId: '',
  taskId: '',
  taskDefKey: '',
  formId: '',
  itembox: '',
  initDataUrl: '',
  itemId: '',
  startTaskDefKey: '',
}

const emits = defineEmits(['refreshCount']);
const data = reactive({
  clickCount: '',
  processDataList: [],
  fromType: '',//区分弹窗选人来源哪个操作
  optType: '',
  routeToTask: '',
  reposition: '',
  myForm: '',
  y9UserInfo: {},
  dialogVisible: false,
  loading: false,
  loadingtext: '拼命加载中',
  customItem: false,//是否定制流程
  activeName: 'y9form',
  itemId: '',
  itembox: '',//文件类型，add新建，todo待办，doing在办，done办结，draft草稿
  taskId: "",
  addInitData: {},//新建数据
  menuMap: {},//按钮菜单数据
  sendMap: {},//发送菜单数据
  formList: [{"formName": "未绑定表单"}],//绑定表单数据
  basicData: baseData1,
  processSerialNumber: '',//流程编号
  processInstanceId: '',//流程实例id
  printUrl: '',//打印url
  printFormType: '',//打印配置
  formId: '',//表单id
  showOtherFlag: '',//是否显示关联流程，正文，沟通交流页签
  listType: '',//列表类型，判断从哪个列表跳转过来，用于返回列表
  follow: false,//是否关注该件
  fileLabel: '附件',//是否有附件
  docNum: 0,//是否有正文
  speakInfoLabel: '沟通交流',//是否沟通交流新消息
  associatedFileLabel: '关联流程',//是否有关联流程
  fileListShow: false,//点击对应的页签才加载对应组件
  processListShow: false,
  speakInfoShow: false,
  associatedFileListShow: false,
  repositionMap: [],//重定位菜单
  doneManage: false,//地灾租户办结角色
  infoOvert: '0',//办结是否在数据中心显示
  multiInstanceType: "",//定制流程使用，当前节点类型
  nextNode: false,//定制流程使用，是否发送下一节点
  //弹窗配置
  dialogConfig: {
    show: false,
    title: "",
    onOkLoading: true,
    closeOnClickModal: false,//是否可以通过点击 modal 关闭 Dialog
    onOk: (newConfig) => {
      return new Promise(async (resolve, reject) => {

      })
    },
    visibleChange: (visible) => {

    }
  },
  userChoiseRef: '',
  // 数组
  dataList: [],
  operationBtnList: [],
  processTimeLineList: [],
  // 收缩 流程状态
  processFlag: null,
  isRefreshButton: false,//串行存在未开始人员，如果有发送按钮，提交按钮，办结按钮等，要提醒重新打开办件处理
});

let {
  processDataList, routeToTask, reposition, myForm, y9UserInfo, dialogVisible, loading, loadingtext, customItem, activeName, itemId,
  itembox, taskId, addInitData, menuMap, sendMap, formList, basicData, processSerialNumber, processInstanceId,
  printUrl, printFormType, formId, showOtherFlag, listType, follow, fileLabel, docNum, speakInfoLabel,
  associatedFileLabel, fileListShow, processListShow, speakInfoShow, associatedFileListShow,
  dataList, operationBtnList, processTimeLineList, processFlag, clickCount, isRefreshButton,
  repositionMap, doneManage, infoOvert, multiInstanceType, nextNode, dialogConfig, userChoiseRef, optType, fromType
} = toRefs(data);

onMounted(async () => {
  y9UserInfo.value = y9_storage.getObjectItem('ssoUserInfo');
  const value = JSON.parse(sessionStorage.getItem('newProcess'));
  processFlag.value = value === null ? true : value;
  itemId.value = currentrRute.query.itemId;
  itembox.value = currentrRute.query.itembox;
  taskId.value = currentrRute.query.taskId;
  processInstanceId.value = currentrRute.query.processInstanceId;
  processSerialNumber.value = currentrRute.query.processSerialNumber;
  listType.value = currentrRute.query.listType;
  basicData.value.itembox = itembox.value;
  doneManage.value = false;
  nextNode.value = false;
  if (itembox.value === "add") {
    customItem.value = flowableStore.getCustomItem;
    processFlag.value = false;
    await getAddData();
  } else if (itembox.value === "draft") {
    processFlag.value = false;
    await getOpenDraftData();
  } else if (itembox.value === "todo" || itembox.value === "doing" || itembox.value === "done" || itembox.value === "monitorDoing" || itembox.value === "monitorDone") {
    customItem = false;
    await getOpenTodoData();
  }

  setTimeout(() => {
    getTabs();
  }, 500);

  if (processInstanceId.value != '' && processInstanceId.value != undefined) {
    getProcessList();
  }
});

function updateLeftListCount() {
  emits("refreshCount");
}

//串行存在未开始人员，如果有发送按钮，提交按钮，办结按钮等，要提醒重新打开办件处理
function refreshButton() {
  isRefreshButton.value = true;
}

async function getAddData() {//获取新建数据
  loading.value = true;
  let res = await addData(itemId.value);
  loading.value = false;
  if (res.success) {
    addInitData.value = res.data;
    menuMap.value = addInitData.value.menuMap.reverse();//将menuMap.reverse()倒序
    sendMap.value = addInitData.value.sendMap;
    formList.value = addInitData.value.formList;
    showOtherFlag.value = addInitData.value.showOtherFlag;
    processSerialNumber.value = addInitData.value.processSerialNumber;
    formId.value = addInitData.value.formList[0].formId;
    setBasicData(addInitData.value);

    //事项前置表单处理
    if(currentrRute.query.processSerialNumber != '' && currentrRute.query.processSerialNumber != undefined){//填写前置表单，返回主表主键id
      processSerialNumber.value = currentrRute.query.processSerialNumber;
      basicData.value.processSerialNumber = currentrRute.query.processSerialNumber;
      basicData.value.formType = currentrRute.query.formType;//前置表单标识
    }
    // myForm.value.show(formId.value);
  }
}

// 页签和按钮的获取
function getTabs() {
  dataList.value = [];
  for(let item of formList.value){
    dataList.value.push({label: item.formName, name: 'y9form' + item.formId});
  }
  activeName.value = "y9form" + formList.value[0].formId;
  if (showOtherFlag.value.includes('showFileTab')) {
    dataList.value.push({label: fileLabel.value, name: 'attach'});
  }
  if (showOtherFlag.value.includes('showHistoryTab')) {
    dataList.value.push({label: associatedFileLabel.value, name: 'associatedFile'});
  }
  if (itembox.value != 'add' && itembox.value != 'draft') {
    dataList.value.push({label: speakInfoLabel.value, name: 'speakInfo'});
    dataList.value.push({label: '历程', name: 'process'});
    dataList.value.push({label: '流程图', name: 'flowChart'});
  }

  operationBtnList.value = [];
  for (let item of menuMap.value) {
    if (listType.value == 'draftRecycle' && item.menuKey != '03') {//回收站只显示返回按钮
      continue;
    }
    if (item.menuKey.indexOf('follow') > -1 && follow.value) { //关注按钮：取消关注
      operationBtnList.value.push({
        name: '取消关注', icon: 'ri-star-fill',
        onClick: () => {
          delFollow()
        }
      });
    } else if (!customItem.value && item.menuKey == '02') {//发送按钮
      operationBtnList.value.push({
        name: item.menuName, icon: 'ri-send-plane-fill',
        render: () => {
          let sbnArr: any = [];
          sendMap.value.forEach(item => {
            sbnArr.push(h('span', {
              onclick: () => {
                sendEvent(item.sendKey);
              },
            }, t(item.sendName)));
          });
          return h('div', {}, sbnArr)
        }
      });
    } else if (item.menuKey.indexOf('follow') > -1 && !follow.value) {//关注按钮：点击关注
      operationBtnList.value.push({
        name: '关注', icon: 'ri-star-line',
        onClick: () => {
          saveFollow()
        }
      });
    } else if (item.menuKey == '16') {//重定位按钮
      operationBtnList.value.push({
        name: item.menuName, icon: 'ri-gps-line',
        render: () => {
          let rArr: any = [];
          repositionMap.value.forEach(ritem => {
            rArr.push(h('span', {
              onclick: () => {
                repositionEvent(ritem.repositionKey);
              },
            }, t(ritem.repositionName)));
          });
          return h('div', {}, rArr)
        }
      });
    } else if (item.menuKey == '12') {//办结按钮
      operationBtnList.value.push({
        name: item.menuName, icon: 'ri-radio-button-line',
        onClick: () => {
          buttonEvent(item.menuKey)
        }
      });
    } else if (item.menuKey == '20') {//恢复待办按钮
      if (listType.value.indexOf('monitor') == -1 || settings.huifudaiban) {
        operationBtnList.value.push({
          name: item.menuName, icon: 'ri-device-recover-line',
          onClick: () => {
            buttonEvent(item.menuKey)
          }
        });
      }
    } else if (item.menuKey == '18') {//抄送按钮
      operationBtnList.value.push({
        name: item.menuName, icon: 'ri-file-copy-2-line',
        onClick: () => {
          buttonEvent(item.menuKey)
        }
      });
    } else if (item.menuKey == '17') {//打印按钮
      if (itembox.value != "add") {//新建不显示打印按钮
        operationBtnList.value.push({
          name: item.menuName, icon: 'ri-printer-line',
          onClick: () => {
            buttonEvent(item.menuKey)
          }
        });
      }
    } else if (item.menuKey != '02' && item.menuKey != '17' && item.menuKey != '18' && item.menuKey != '12' && item.menuKey != '16' && item.menuKey != '20'
        && item.menuKey.indexOf('follow') == -1) {//其他按钮
      let iconName = 'ri-mouse-line';
      if (item.menuKey == '01') {//保存
        iconName = 'ri-save-line';
      } else if (item.menuKey == '03') {//返回
        iconName = 'ri-arrow-go-back-fill';
      } else if (item.menuKey == '04') {//退回
        iconName = 'ri-reply-line';
      } else if (item.menuKey == '05') {//委托
        iconName = 'ri-hand-heart-line';
      } else if (item.menuKey == '06') {//协商
        iconName = 'ri-wechat-2-line';
      } else if (item.menuKey == '07') {//完成
        iconName = 'ri-check-line';
      } else if (item.menuKey == '08') {//送下一人
        iconName = 'ri-user-shared-2-line';
      } else if (item.menuKey == '09') {//办理完成
        iconName = 'ri-check-line';
      } else if (item.menuKey == '10') {//签收
        iconName = 'ri-mail-check-line';
      } else if (item.menuKey == '11') {//撤销签收
        iconName = 'ri-mail-close-line';
      } else if (item.menuKey == '13') {//收回
        iconName = 'ri-folder-received-line';
      } else if (item.menuKey == '14') {//拒签
        iconName = 'ri-mail-forbid-line';
      } else if (item.menuKey == '15') {//特殊办结
        iconName = 'ri-radio-button-line';
      } else if (item.menuKey == '19') {//加减签
        iconName = 'ri-user-add-line';
      } else if (item.menuKey == 'common_fasongren') {//返回发送人
        iconName = 'ri-arrow-left-line';
      } else if (item.menuKey == '21') {//提交
        iconName = 'ri-check-line';
      }

      operationBtnList.value.push({
        name: item.menuName, icon: iconName,
        onClick: () => {
          buttonEvent(item.menuKey)
        }
      });
    } else if (customItem.value) {//流程定制按钮
      operationBtnList.value.push({
        name: item.menuName, icon: 'ri-node-tree',
        onClick: () => {
          customProcess()
        }
      });
    }
  }
}

async function getProcessList() {
  let res = await processList(processInstanceId.value);
  if (res.success) {
    processDataList.value = [];
    const rows = res.data;
    let index = 0;
    for (let item of rows) {
      index++;
      let obj = {};
      obj.content = item.name;
      if (item.assignee.indexOf('（') != -1) {
        let nameArr = [];
        let reg = new RegExp('）', 'g')
        let userName = item.assignee.split('（')[1].replace(reg, "");
        nameArr.push(userName);
        nameArr.push(item.assignee.split('（')[0]);
        obj.name = nameArr;
      } else {
        obj.name = item.assignee;
      }
      obj.type = 'solid';
      obj.placement = 'left';
      if (item.endTime === "") {
        obj.timestamp = item.startTime;
        obj.type = 'current';
      } else {
        let timneArr = [];
        timneArr.push(item.startTime);
        timneArr.push(item.endTime);
        obj.timestamp = timneArr;
      }
      if ((index & 1) === 0) {
        obj.placement = 'right';
      }
      processTimeLineList.value.push(obj);
    }
  }
}

function setBasicData(data) {
  basicData.value.flowableUIBaseURL = data.flowableUIBaseURL;
  basicData.value.processSerialNumber = data.processSerialNumber;
  basicData.value.tenantId = data.tenantId;
  basicData.value.userId = data.userId;
  basicData.value.processDefinitionId = data.processDefinitionId;
  basicData.value.processDefinitionKey = data.processDefinitionKey;
  basicData.value.taskDefKey = data.taskDefKey;
  basicData.value.formId = data.formList[0].formId;
  basicData.value.printFormId = data.printFormId;
  basicData.value.initDataUrl = data.initDataUrl == undefined ? "" : data.initDataUrl;
  basicData.value.itemId = data.itemId;
  basicData.value.processInstanceId = data.processInstanceId == undefined ? "" : data.processInstanceId;
  basicData.value.taskId = data.taskId == undefined ? "" : data.taskId;
  basicData.value.sponsorHandle = data.sponsorHandle == undefined ? "" : data.sponsorHandle;
  basicData.value.activitiUser = data.activitiUser;
  basicData.value.itembox = data.itembox == undefined ? "" : data.itembox;
  basicData.value.startor = data.startor == undefined ? "" : data.startor;
  basicData.value.startTaskDefKey = data.startTaskDefKey == undefined ? "" : data.startTaskDefKey;
}

async function getOpenDraftData() {//获取草稿数据
  loading.value = true;
  let res = await openDraft(processSerialNumber.value, itemId.value);
  loading.value = false;
  if (res.success) {
    let draftData = res.data;
    customItem.value = draftData.customItem;
    flowableStore.$patch({
      customItem: customItem.value
    })
    menuMap.value = draftData.menuMap.reverse();//将menuMap.reverse()倒序
    sendMap.value = draftData.sendMap;
    formList.value = draftData.formList;
    formId.value = draftData.formList[0].formId;
    showOtherFlag.value = draftData.showOtherFlag;
    fileLabel.value = '';
    fileLabel.value = draftData.fileNum == 0 ? "附件" : "附件(" + draftData.fileNum + ")";
    docNum.value = draftData.docNum;
    associatedFileLabel.value = draftData.associatedFileNum == 0 ? "关联流程" : "关联流程(" + draftData.associatedFileNum + ")";
    setBasicData(draftData);
    printFormType.value = draftData.printFormType;
    // myForm.value.show(formId.value);
  }
}

//新建抄送后，更新启动流程返回的数据
function setCsProcessData(csData) {
  basicData.value.processInstanceId = csData.processInstanceId;
  basicData.value.taskId = csData.taskId;
  basicData.value.itembox = csData.itembox;
  processInstanceId.value = csData.processInstanceId;
  taskId.value = csData.taskId;
  itembox.value = csData.itembox;
  //dialogConfig.value.show = false;
}

async function getOpenTodoData() {//获取办件数据
  loading.value = true;
  let res = await getTodoData(processInstanceId.value, taskId.value, itemId.value, itembox.value);
  loading.value = false;
  if (res.success) {
    let todoData = res.data;
    menuMap.value = todoData.menuMap.reverse();//将menuMap.reverse()倒序
    sendMap.value = todoData.sendMap;
    formList.value = todoData.formList;
    formId.value = todoData.formList[0].formId;
    showOtherFlag.value = todoData.showOtherFlag;
    processInstanceId.value = todoData.processInstanceId;
    follow.value = todoData.follow;
    fileLabel.value = '';
    fileLabel.value = todoData.fileNum == 0 ? "附件" : "附件(" + todoData.fileNum + ")";
    docNum.value = todoData.docNum;
    associatedFileLabel.value = todoData.associatedFileNum == 0 ? "关联流程" : "关联流程(" + todoData.associatedFileNum + ")";
    speakInfoLabel.value = todoData.speakInfoNum == 0 ? "沟通交流" : "沟通交流(" + todoData.speakInfoNum + ")";
    printFormType.value = todoData.printFormType;
    repositionMap.value = todoData.repositionMap;
    //修改标题数据
    flowableStore.$patch({
      documentTitle: todoData.title
    })
    multiInstanceType.value = todoData.multiInstance;
    nextNode.value = todoData.nextNode;
    doneManage.value = todoData.doneManage;
    setBasicData(todoData);
    // myForm.value.show(formId.value);
  }
}

function tabClick(item) {//页签切换
  activeName.value = item.name;
  if (item.name == "attach") {
    fileListShow.value = true;
  } else if (item.name == "process") {
    processListShow.value = true;
  } else if (item.name == "flowChart") {
    // processListShow.value = true;
  } else if (item.name == "speakInfo") {
    clickCount.value++;
    speakInfoShow.value = true;
  } else if (item.name == "associatedFile") {
    associatedFileListShow.value = true;
  }else if(item.name.indexOf("y9form") > -1){
    myForm.value.show(item.name.slice(6));
  }
}

function backToList() {//返回列表
  let link = currentrRute.matched[0].path;
  let query = {
    itemId: itemId.value,
  };
  flowableStore.$patch({//设置打开当前页
    currentPage: flowableStore.currentPage + '_back'
  });
  router.push({path: link + '/' + listType.value, query: query});
}

function customProcess() {//流程定制
  myForm.value.saveY9Form(false).then(res => {
    loading.value = true;
    return myForm.value.saveY9ProcessParam();
  }).then(value => {
    return myForm.value.saveY9Draft();
  }).then(() => {
    loading.value = false;
    Object.assign(dialogConfig.value, {
      show: true,
      width: '50%',
      title: computed(() => t('定制流程')),
      type: 'customProcess',
      showFooter: false
    });
  });
}

function sendClick() {//发送点击

}

function rollbackToStartor() {//退回发起人，直接退回，不用选人
  ElMessageBox.confirm(t("确定返回发起人?"), t("提示"), {confirmButtonText: t("确定"), cancelButtonText: t("取消"), type: "info", appendTo: '.newDocument-container'}).then(() => {
    loading.value = true;
    loadingtext.value = "正在处理中";
    buttonApi.rollbackToStartor(basicData.value.taskId).then(res1 => {
      buttonAfter(res1);
    });
  }).catch(() => {
    ElMessage({type: "info", message: t("已取消返回发起人"), offset: 65, appendTo: '.newDocument-container'});
  });
}

function sendToSender() {//返回发送人，直接发送，不用选人
  let msg = myForm.value.signOpinion();
  if (msg != "") {
    ElMessage({type: "error", message: msg, dangerouslyUseHTMLString: true, offset: 65, appendTo: '.newDocument-container'});
    return;
  }
  //myForm.value.saveChangeOpinion();//保存编辑未保存的意见内容
  ElMessageBox.confirm(t("确定返回发送人?"), t("提示"), {confirmButtonText: t("确定"), cancelButtonText: t("取消"), type: "info", appendTo: '.newDocument-container'}).then(() => {
    loading.value = true;
    loadingtext.value = "正在处理中";
    buttonApi.sendToSender(basicData.value.taskId).then(res1 => {
      buttonAfter(res1);
    });
  }).catch(() => {
    ElMessage({type: "info", message: t("已取消返回发送人"), offset: 65, appendTo: '.newDocument-container'});
  });
}

function sendToStartor() {//发送发送人，直接发送，不用选人
  ElMessageBox.confirm(t("确定返回发送人?"), t("提示"), {confirmButtonText: t("确定"), cancelButtonText: t("取消"), type: "info", appendTo: '.newDocument-container'}).then(() => {
    loading.value = true;
    loadingtext.value = "正在处理中";
    buttonApi.sendToStartor(basicData.value.taskId).then(res1 => {
      buttonAfter(res1);
    });
  }).catch(() => {
    ElMessage({type: "info", message: t("已取消返回发送人"), offset: 65, appendTo: '.newDocument-container'});
  });
}

function submitTo21() {//提交
  let msg = myForm.value.signOpinion();
  if (msg != "") {
    ElMessage({type: "error", message: msg, dangerouslyUseHTMLString: true, offset: 65, appendTo: '.newDocument-container'});
    return;
  }
  myForm.value.saveChangeOpinion();//保存编辑未保存的意见内容
  myForm.value.saveY9Form(false).then(res => {
    return myForm.value.saveY9ProcessParam();
  }).then(value => {
    return myForm.value.saveY9Draft();
  }).then(() => {
    ElMessageBox.confirm(t("确定提交至下一人?"), t("提示"), {confirmButtonText: t("确定"), cancelButtonText: t("取消"), type: "info", appendTo: '.newDocument-container'}).then(() => {
      loading.value = true;
      loadingtext.value = "正在处理中";
      buttonApi.submitTo(basicData.value.itemId, basicData.value.taskId, basicData.value.processSerialNumber).then(res1 => {
        buttonAfter(res1);
      });
    }).catch(() => {
      ElMessage({type: "info", message: t("已取消提交"), offset: 65, appendTo: '.newDocument-container'});
    });
  });
}

function repositionEvent(sendKey) {//重定位选择
  routeToTask.value = sendKey;
  reposition.value = "reposition";
  fromType.value = "重定位";
  Object.assign(dialogConfig.value, {
    show: true,
    width: '50%',
    title: computed(() => t('重定位人员选择')),
    type: 'userChoise',
    showFooter: false
  });
}

function directSend(sendKey) {//直接发到分配人员手上
  ElMessageBox.confirm(t("确定发送至结果反馈?"), t("提示"), {confirmButtonText: t("确定"), cancelButtonText: t("取消"), type: "info", appendTo: '.newDocument-container'}).then(() => {
    loading.value = true;
    loadingtext.value = "正在处理中";
    buttonApi.directSend(basicData.value.processInstanceId, basicData.value.taskId, sendKey).then(res1 => {
      buttonAfter(res1);
    });
  }).catch(() => {
    ElMessage({type: "info", message: t("已取消发送"), offset: 65, appendTo: '.newDocument-container'});
  });
}

async function sendEvent(sendKey) {//发送按钮事件
  if (isRefreshButton.value) {
    ElMessage({type: "info", message: t("您已加减签，请重新打开办件处理"), offset: 65, appendTo: '.newDocument-container'});
    return;
  }
  let msg = myForm.value.signOpinion();
  if (msg != "") {
    ElMessage({type: "error", message: msg, dangerouslyUseHTMLString: true, offset: 65, appendTo: '.newDocument-container'});
    return;
  }
  myForm.value.saveChangeOpinion();//保存编辑未保存的意见内容

  let msg1 = await myForm.value.setNumber();//编号
  if (msg1 == '') {
    ElMessage({type: "error", message: t("编号异常"), dangerouslyUseHTMLString: true, offset: 65, appendTo: '.newDocument-container'});
    return;
  }

  myForm.value.saveY9Form(false).then(res => {
    return myForm.value.saveY9ProcessParam();
  }).then(value => {
    return myForm.value.saveY9Draft();
  }).then(() => {
    if (sendKey == "send_faqiren") {
      rollbackToStartor();
      return;
    }
    if (sendKey == "send_fasongren") {
      sendToStartor();
      return;
    } else if (sendKey == "jieguofankui") {//系统工单结果反馈，直接发到分配人员手上
      directSend(sendKey);
      return;
    }
    if (basicData.value.sponsorHandle == "true") {//并行主办人办理，提示协办办理情况
      buttonApi.getParallelNames(basicData.value.taskId).then(res => {
        let parallelNames = res.data.parallelDoing;
        let count = res.data.count;
        let str = "";
        if (count > 5) {
          str = t("等共") + count + t("人");
        }
        if (parallelNames != "") {
          ElMessageBox.confirm(`<font color='red'>${t('温馨提示：并行协办')}【${parallelNames}】${str}${t('未办理完成，发送将使协办人任务强制办结')}。</font>`, t("提示"),
              {confirmButtonText: t("确定"), cancelButtonText: t("取消"), type: "info", dangerouslyUseHTMLString: true, appendTo: '.newDocument-container'}).then(() => {
            routeToTask.value = sendKey;
            fromType.value = "发送";
            Object.assign(dialogConfig.value, {
              show: true,
              width: '50%',
              title: computed(() => t('人员选择')),
              type: 'userChoise',
              showFooter: false
            });
          }).catch(() => {
            ElMessage({type: "info", message: t("已取消发送"), offset: 65, appendTo: '.newDocument-container'});
          });
        } else {
          signTaskConfig(sendKey);
        }
      });
    } else {
      signTaskConfig(sendKey);
    }
  });
}

function signTaskConfig(sendKey) {//判断是直接发送还是弹框选人
  buttonApi.signTaskConfig(basicData.value.itemId, basicData.value.processDefinitionId, sendKey, basicData.value.processSerialNumber).then(res0 => {
    if (res0.success) {
      if (res0.data.signTask) {//抢占式签收任务直接发送
        directForwarding(res0.data.userChoice, sendKey);
      } else {
        if (res0.data.onePerson) {//单实例且只有一个人员，则直接发送
          directForwarding(res0.data.userChoice, sendKey);
        } else {//其他情况弹出选人界面
          routeToTask.value = sendKey;
          fromType.value = "发送";
          Object.assign(dialogConfig.value, {
            show: true,
            width: '50%',
            title: computed(() => t('人员选择')),
            type: 'userChoise',
            showFooter: false
          });
        }
      }
    }
  });
}

function directForwarding(userChoice, sendKey) {//直接发送
  loading.value = true;
  forwarding(basicData.value.itemId, basicData.value.processInstanceId, basicData.value.taskId, basicData.value.processDefinitionKey,
      basicData.value.processSerialNumber, basicData.value.sponsorHandle, userChoice, "", sendKey, "false", "false", "").then(res => {
    loading.value = false;
    if (res.success) {
      ElMessage({type: "success", message: res.msg, offset: 65, appendTo: '.newDocument-container'});
      let query = {
        itemId: basicData.value.itemId,
        refreshCount: true
      };
      router.push({path: '/todo', query: query});
    } else {
      ElMessage({type: "error", message: res.msg, offset: 65, appendTo: '.newDocument-container'});
    }
  });
}

function setInfoOvert() {//办结设置是否在数据中心显示
  dialogVisible.value = false;
  loading.value = true;
  loadingtext.value = "正在处理中";
  if (nextNode.value) {//定制流程办结
    buttonApi.customProcessHandle(basicData.value.itemId, basicData.value.processSerialNumber, basicData.value.processDefinitionKey,
        multiInstanceType.value, nextNode.value, basicData.value.processInstanceId, basicData.value.taskId, infoOvert.value).then(res => {
      buttonAfter(res);
    });
  } else {//常规办结
    buttonApi.complete(basicData.value.taskId, infoOvert.value).then(async res => {
      if (res.success) {
        myForm.value.generateForm.sendRequest("xiaoJia");//办结销假，shoutong使用。
        if (settings.chaoSongItemId.indexOf(basicData.value.itemId) > -1 && settings.legalPersonId != '') {//特定事项办结自动抄送法人，shoutong使用。
          chaoSongSave(basicData.value.processInstanceId, basicData.value.itemId, basicData.value.processSerialNumber, basicData.value.processDefinitionKey, "6:" + settings.legalPersonId, '', '', '', '');
        }
      }
      setTimeout(() => {
        buttonAfter(res);
      }, 500);
    });
  }
}

function buttonEvent(key) {//按钮事件
  if (key == "01") {//保存
    myForm.value.saveForm(true);
    myForm.value.saveChangeOpinion();//保存编辑未保存的意见内容
  } else if (key == "03") {//返回
    backToList();
  } else if (key == "04") {//退回
    myForm.value.saveChangeOpinion();//保存编辑未保存的意见内容
    optType.value = 'rollback';
    Object.assign(dialogConfig.value, {
      show: true,
      width: '40%',
      title: computed(() => t('退回')),
      type: 'rollbackOrTakeback',
      showFooter: false
    });
  } else if (key == "08") {//送下一人
    if (isRefreshButton.value) {
      ElMessage({type: "info", message: t("您已加减签，请重新打开办件处理"), offset: 65, appendTo: '.newDocument-container'});
      return;
    }
    let msg = myForm.value.signOpinion();
    if (msg != "") {
      ElMessage({type: "error", message: msg, dangerouslyUseHTMLString: true, offset: 65, appendTo: '.newDocument-container'});
      return;
    }
    myForm.value.saveChangeOpinion();//保存编辑未保存的意见内容
    myForm.value.saveY9Form(false).then(res => {
      ElMessageBox.confirm(t("是否送下一人"), t("提示"), {confirmButtonText: t("确定"), cancelButtonText: t("取消"), type: "info", appendTo: '.newDocument-container'}).then(() => {
        loading.value = true;
        loadingtext.value = "正在处理中";
        buttonApi.handleSerial(basicData.value.taskId).then(res1 => {
          buttonAfter(res1);
        });
      }).catch(() => {
        ElMessage({type: "info", message: t("已取消送下一人"), offset: 65, appendTo: '.newDocument-container'});
      });
    });
  } else if (key == "09") {//办理完成
    if (isRefreshButton.value) {
      ElMessage({type: "info", message: t("您已加减签，请重新打开办件处理"), offset: 65, appendTo: '.newDocument-container'});
      return;
    }
    let msg = myForm.value.signOpinion();
    if (msg != "") {
      ElMessage({type: "error", message: msg, dangerouslyUseHTMLString: true, offset: 65, appendTo: '.newDocument-container'});
      return;
    }
    myForm.value.saveChangeOpinion();//保存编辑未保存的意见内容
    myForm.value.saveY9Form(false).then(res => {
      ElMessageBox.confirm(t("是否办理完成"), t("提示"), {confirmButtonText: t("确定"), cancelButtonText: t("取消"), type: "info", appendTo: '.newDocument-container'}).then(() => {
        loading.value = true;
        loadingtext.value = "正在处理中";
        if (nextNode.value || multiInstanceType.value == "sequential") {//定制流程办理完成
          buttonApi.customProcessHandle(basicData.value.itemId, basicData.value.processSerialNumber, basicData.value.processDefinitionKey,
              multiInstanceType.value, nextNode.value, basicData.value.processInstanceId, basicData.value.taskId, infoOvert.value).then(res => {
            buttonAfter(res);
          });
        } else {//常规办理完成
          buttonApi.handleParallel(basicData.value.taskId).then(res => {
            buttonAfter(res);
          });
        }
      }).catch(() => {
        ElMessage({type: "info", message: t("已取消办理完成"), offset: 65, appendTo: '.newDocument-container'});
      });
    });
  } else if (key == "12") {//办结infoOvert
    if (isRefreshButton.value) {
      ElMessage({type: "info", message: t("您已加减签，请重新打开办件处理"), offset: 65, appendTo: '.newDocument-container'});
      return;
    }
    let msg = myForm.value.signOpinion();
    if (msg != "") {
      ElMessage({type: "error", message: msg, dangerouslyUseHTMLString: true, offset: 65, appendTo: '.newDocument-container'});
      return;
    }
    myForm.value.saveChangeOpinion();//保存编辑未保存的意见内容
    myForm.value.saveY9Form(false).then(res => {
      dialogVisible.value = true;
    });
  } else if (key == "10") {//签收
    loading.value = true;
    loadingtext.value = "正在处理中";
    buttonApi.claim(basicData.value.taskId).then(res => {
      buttonAfter(res);
    });
  } else if (key == "11") {//撤销签收
    loading.value = true;
    loadingtext.value = "正在处理中";
    buttonApi.unclaim(basicData.value.taskId).then(res => {
      buttonAfter(res);
    });
  } else if (key == "14") {//拒签
    ElMessageBox.confirm(t("是否拒签该件?"), t("提示"), {confirmButtonText: t("确定"), cancelButtonText: t("取消"), type: "warning", appendTo: '.newDocument-container'}).then(() => {
      loading.value = true;
      loadingtext.value = "正在处理中";
      buttonApi.refuseClaim(basicData.value.taskId).then(res => {
        buttonAfter(res);
      });
    }).catch(() => {
      ElMessage({type: "info", message: t("已取消拒签"), offset: 65, appendTo: '.newDocument-container'});
    });
  } else if (key == "13") {//收回
    optType.value = 'takeback';
    Object.assign(dialogConfig.value, {
      show: true,
      width: '40%',
      title: computed(() => t('收回')),
      type: 'rollbackOrTakeback',
      showFooter: false
    });
  } else if (key == "15") {//特殊办结
    Object.assign(dialogConfig.value, {
      show: true,
      width: '40%',
      title: computed(() => t('特殊办结')),
      type: 'specialComplete',
      showFooter: false
    });
  } else if (key == "17") {//打印
    if (printFormType.value == "") {
      ElMessage({type: "error", message: t("未配置打印配置"), offset: 65, appendTo: '.newDocument-container'});
      return;
    }
    if (printFormType.value == "2") {//打印表单
      printUrl.value = import.meta.env.VUE_APP_HOST_INDEX + "print?formId=" + basicData.value.printFormId + "&processSerialNumber=" + basicData.value.processSerialNumber
          + "&processDefinitionId=" + basicData.value.processDefinitionId + "&taskDefKey=" + basicData.value.taskDefKey + "&itemId=" + basicData.value.itemId
          + "&activitiUser=" + basicData.value.activitiUser + "&processInstanceId=" + basicData.value.processInstanceId;
      window.open(printUrl.value);
    }
  } else if (key == "18") {//抄送
    myForm.value.saveY9Form(false).then(res => {
      return myForm.value.saveY9ProcessParam();
    }).then(value => {
      return myForm.value.saveY9Draft();
    }).then(() => {
      Object.assign(dialogConfig.value, {
        show: true,
        width: '50%',
        title: computed(() => t('人员选择')),
        type: 'csUserChoise',
        showFooter: false
      });
    });
  } else if (key == "19") {//加减签
    Object.assign(dialogConfig.value, {
      show: true,
      width: '50%',
      title: computed(() => t('加减签')),
      type: 'multiInstance',
      showFooter: false
    });
  } else if (key == "common_fasongren") {//返回发送人
    sendToSender();
  } else if (key == "20") {//恢复待办
    ElMessageBox.confirm(t("是否恢复待办?"), t("提示"), {
      confirmButtonText: t("确定"),
      cancelButtonText: t("取消"),
      dangerouslyUseHTMLString: true,
      type: "info",
      appendTo: '.newDocument-container'
    }).then(() => {
      loading.value = true;
      loadingtext.value = "正在处理中";
      buttonApi.multipleResumeToDo(basicData.value.processInstanceId).then(res => {
        loading.value = false;
        if (res.success) {
          emits("refreshCount");
          ElMessage({type: "success", message: res.msg, offset: 65, appendTo: '.newDocument-container'});
          let link = currentrRute.matched[0].path;
          let query = {
            itemId: basicData.value.itemId,
          };
          router.push({path: link + '/done', query: query});
        } else {
          ElMessage({type: "error", message: res.msg, offset: 65, appendTo: '.newDocument-container'});
        }
      }).catch(() => {
        loading.value = false;
        ElMessage({type: "info", message: t("发生异常"), offset: 65, appendTo: '.newDocument-container'});
      });
    }).catch(() => {
      ElMessage({type: "info", message: t("已取消操作"), offset: 65, appendTo: '.newDocument-container'});
    });
  } else if (key == "21") {//提交
    if (isRefreshButton.value) {
      ElMessage({type: "info", message: t("您已加减签，请重新打开办件处理"), offset: 65, appendTo: '.newDocument-container'});
      return;
    }
    submitTo21();
  }
}

function buttonAfter(res) {
  loading.value = false;
  if (res.success) {
    ElMessage({type: "success", message: res.msg, offset: 65, appendTo: '.newDocument-container'});
    let link = currentrRute.matched[0].path;
    let query = {
      itemId: basicData.value.itemId,
      refreshCount: true
    };
    router.push({path: link + '/todo', query: query});
  } else {
    ElMessage({type: "error", message: res.msg, offset: 65, appendTo: '.newDocument-container'});
  }
}

function delFollow() {//取消关注
  delOfficeFollow(basicData.value.processInstanceId).then(res => {
    if (res.success) {
      // emits("refreshCount");
      ElMessage({type: 'success', message: res.msg, offset: 65, appendTo: '.newDocument-container'});
      follow.value = false;
      setTimeout(() => {
        getTabs();
      }, 100);
    } else {
      ElMessage({type: 'error', message: res.msg, offset: 65, appendTo: '.newDocument-container'});
    }
  });
}

function saveFollow() {//设置关注
  saveOfficeFollow(basicData.value.processInstanceId).then(res => {
    if (res.success) {
      // emits("refreshCount");
      ElMessage({type: 'success', message: res.msg, offset: 65, appendTo: '.newDocument-container'});
      follow.value = true;
      setTimeout(() => {
        getTabs();
      }, 100);
    } else {
      ElMessage({type: 'error', message: res.msg, offset: 65, appendTo: '.newDocument-container'});
    }
  });
}

// 点击收缩按钮 右边出现或不出现
function handlerClickShrink() {
  // console.log('点击');
  processFlag.value = !processFlag.value;
  sessionStorage.setItem('newProcess', JSON.stringify(processFlag.value));
}

</script>
<style lang="scss">
.y9DialogClass .el-button i {
  margin-right: 4px !important;
}

.tishi .el-button {
  padding: 0px 18px !important;
  height: 32px;
  line-height: 32px;
}
</style>
<style lang="scss" scoped>
body {
  height: 100%;
  width: 100%;
  position: absolute;
}

.document {
  background-color: #e7e8ec;
  height: 100%;
  position: relative;
  width: 79%;
  font: v-bind('fontSizeObj.baseFontSize') Helvetica Neue, Helvetica, PingFang SC, Tahoma, Arial, sans-serif;
}

.document-shrink {
  width: 100% !important;
}

.document .buttonDiv1 {
  position: absolute;
  right: 0%;
  top: 15%;
  z-index: 2;
  height: 125px;
}

.el-tabs__nav-wrap::after {
  height: 0px;
}

.document .el-tabs__active-bar {
  bottom: 5px;
  margin: 0px 15px !important;
}

*, ::after, ::before {
  box-sizing: content-box;
}

.document .el-tabs__header {
  width: 50%;
  background-color: #fff;
  border-top: 2px solid #E4E7ED;
  border-bottom: 2px solid #E4E7ED;
  margin: 0px;
  height: 48px;
  line-height: 48px;
  z-index: 0;
}

.document .el-tabs__content {
  height: 93%;
  width: 100%;
}

.document .el-tabs__item {
  height: 50px;
  line-height: 50px;
  padding: 0px 15px;
}

.document .el-tabs__nav-scroll {
  padding-left: 15px;
}

.document .el-container {
  // height: calc(100vh - 210px) !important;
  overflow: auto;
}

.tab-nav {
  width: 100%;
  height: 125px;
  top: -5%;
  position: absolute;
}

.shrink {
  position: absolute;
  right: 0.5%;
  top: 1.5%;
  color: var(--el-color-primary);
  cursor: pointer;
  font-size: v-bind('fontSizeObj.morerLargeFont');
}

.process-content {
  width: 19%;
  // height: calc(100vh - 106px);
  margin-left: 45px;
  border-left: 0px solid #aaa;
  padding: 0;

  :deep(.el-card__body) {
    height: 99%;
    padding: 0px 10px 10px 10px;
    background-color: #e7e8ec;
  }

  :deep(.el-card__header) {
    padding: calc(var(--el-card-padding) - 2px) var(--el-card-padding);
    border-bottom: 0px solid var(--el-card-border-color);
    box-sizing: border-box;
    text-align: center;
    font-weight: bold;
    background-color: #e7e8ec;
  }
}

:deep(.el-empty__description p) {
  font-size: v-bind('fontSizeObj.baseFontSize');
}

.newDocument-container {
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

@media screen and (min-width: 1365px) and (max-width: 1860px) {
  .process-content {
    width: 28%;
  }
}

@media screen and (max-width: 1365px) {
  .process-content {
    width: 34%;
  }
}


</style>