<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2021-07-05 15:02:36
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2021-10-27 10:36:21
 * @FilePath: \workspace-y9boot-9.5-vue\y9vue-cplane\src\views\cplane\list.vue
-->
<template>
  <el-input :prefix-icon="Search" class="cplane-input" v-model="searchName" style="width:350px;" :placeholder="$t('请输入标题或文号')" @keyup.enter.native="reloadTable()" clearable></el-input>
  <el-button class="global-btn-third" style="margin-left: 10px;" @click="refreshTable" :size="fontSizeObj.buttonSize"
    :style="{ fontSize: fontSizeObj.baseFontSize }" ><i class="ri-refresh-line"></i><span>{{ $t('刷新') }}</span></el-button>
  <el-card 
    v-loading="loading"
    :element-loading-text="$t('拼命加载中')"
    element-loading-spinner="el-icon-loading"
    style="margin:20px 0;height: calc( 100% - 150px);"
    class="mycard"
  >
    <div class="cplane-list" style="overflow: auto;height: 100%;position: relative;">
      <template v-for="item in listData">
        <div class="contentdiv">
          <li class="headLi">
            <div style="margin-left: 20px;">{{item.itemName}}</div>
            <div>{{item.number}}</div>
            <div>{{item.startTime}}</div>
          </li>
          <el-link class="ellink" :underline="false" type="primary">
            <font @click="openDoc(item.url)">{{item.title}}</font>
          </el-link>
          <div v-if="item.itembox == 'doing'" style="display: inline-block;color: #2aac0b;">{{ $t('办理中') }}</div>
          <div v-if="item.itembox == 'done'" style="display: inline-block;color: red;">{{ $t('已办结') }}</div>
          <div style=" width: 100%;overflow-x: auto;">
            <el-timeline :id="item.processInstanceId">
              <template v-for="task in item.itemInfo">
                <el-timeline-item 
                  :timestamp="task.endTime == '' ? '--' : task.endTime" 
                  :color="task.endTime == '' ? '#0bbd87' : '#bbb'"
                  >
                  <div>
                    <span style="font-weight: bold;">{{task.taskName}}</span>
                    <p :title="task.assigneeName" style="overflow: hidden;text-overflow: ellipsis;white-space: nowrap;">{{task.assigneeName}}</p>
                  </div>
                </el-timeline-item>
              </template>
            </el-timeline>
          </div>
        </div>
      </template>
    </div>
  </el-card>
  <y9Pagination :config="pageConfig" @current-change="currentChange" @size-change="sizeChange"/>
</template>

<script lang="ts" setup>
  import { ref, defineProps, onMounted, watch,reactive, inject} from 'vue';
  import {processInstanceList} from "@/api/flowableUI/cplane";
  import {Search } from '@element-plus/icons-vue';
  import { useI18n } from 'vue-i18n';
  const { t } = useI18n();
  // 注入 字体对象
  const fontSizeObj: any = inject('sizeObjInfo'); 
  const data = reactive({
    listData:[],
    searchName:'',
    cardStyle:'',
    pageConfig:{
      customStyle:"circular",//自定义样式，可选值有circular圆形,square方形,default默认（无形状）
			hideOnSinglePage:false, //只有一页时是否隐藏
			background:true,//是否显示背景色
			layout:"prev, pager, next,sizes",//布局
			currentPage: 1,//当前页数，支持 v-model 双向绑定
			pageSize: 10,//每页显示条目个数，支持 v-model 双向绑定
			total: 10,//总条目数
			pageSizeOpts: [10, 20, 30, 40, 50],//每页显示个数选择器的选项设置
    },
    loading:false,
  })
    
  let {
    searchName,
    listData,
    loading,
    pageConfig
  } = toRefs(data);

  onMounted(()=>{
    document.title = t("我的协作");
    reloadTable();
  });

  async function reloadTable(){
    loading.value = true;
    let res = await processInstanceList(pageConfig.value.currentPage,pageConfig.value.pageSize,searchName.value);
    loading.value = false;
    listData.value = res.rows;
    pageConfig.value.total = res.total;
    setTimeout(()=>{//修改宽度
      updateWidth();
    },500)
  }

  function refreshTable(){
    searchName.value = "";
    pageConfig.value.currentPage = 1;
    pageConfig.value.pageSize = 10;
    reloadTable();
  }

  function currentChange(currPage){
    pageConfig.value.currentPage = currPage;
    reloadTable();
  }

  function sizeChange(pageSize) {
    pageConfig.value.pageSize = pageSize;
    reloadTable();
  }

  function updateWidth(){//修改宽度
    for(let item of listData.value){
      let obj = document.getElementById(item.processInstanceId);
      if(obj != null){
        obj.style.width = item.itemInfo.length * 180 + 50 + "px";
      }
    }
  }

  function openDoc(url){
    window.open(url);
  }
</script>

<style>
  .mycard .el-card__body{
    position: relative !important;
    height: 100% !important;
  } 
  .cplane-list .el-main-table{
    padding: 0px;
  }
  .cplane-input .el-input__wrapper {
      border-radius: 30px;
  }
  .cplane-list .el-timeline {
    padding-left: 20px;
    width: 600px;
    font-family: "微软雅黑";
  }
  .cplane-list .el-timeline-item {
    position: relative;
    padding-bottom: 20px;
    display: inline-block;
    width: 180px;
  }
  .cplane-list .ellink{
    height: 50px;
    margin-left: 20px;
    width: 40%;
    font-size: v-bind('fontSizeObj.mediumFontSize');
  }
  .cplane-list .el-link__inner{
    justify-content:left;
  }
  .cplane-list .ellink span{
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    width: 100%;
  }
  .cplane-list .headLi{
    height: 40px;
    background-color: #eee;
    list-style: none;
    border-top: 1px solid #ccc;
  }
  .cplane-list .headLi div{
    display: inline-block;
    width: 20%;
    line-height: 40px;
  }
  .cplane-list .contentdiv{
    border-bottom: 1px solid #ccc;
    border-right: 1px solid #ccc;
    border-left: 3px solid #5c70b3;
    margin-bottom: 20px;
  }
  .cplane-list .timelinediv{
    width: 100%;
    overflow-x: auto;
  }
  .cplane-list .el-timeline-item__tail {
    position: absolute;
    left: 4px;
    height: 2px;
    border-top: 2px solid #E4E7ED;
    width: 100%;
    top: 4px;
  }
  .cplane-list h4{
    margin: 0;
  }
  .cplane-list p{
    margin: 5px 0px;
  }
  .cplane-list .el-timeline-item__wrapper {
    position: relative;
    top: 20px;
    padding-left:0px;
    height: 80px;
  }
</style>