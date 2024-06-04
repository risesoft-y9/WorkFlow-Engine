<template>
  <y9Table ref="filterRef" :filterConfig="filterConfig" :config="tableConfig">
    <template #buttonslot>
      <el-button class="global-btn-main" @click="reloadTable" :size="fontSizeObj.buttonSize"
      :style="{ fontSize: fontSizeObj.baseFontSize }">
        <i class="ri-search-line"></i>
        <span>{{$t('搜索') }}</span>
      </el-button>
      <el-button class="global-btn-third" @click="refreshTable" :size="fontSizeObj.buttonSize"
      :style="{ fontSize: fontSizeObj.baseFontSize }">
        <i class="ri-refresh-line"></i>
        <span>{{ $t('刷新') }}</span>
      </el-button>
      <el-button class="global-btn-third" @click="exportData" :size="fontSizeObj.buttonSize"
      :style="{ fontSize: fontSizeObj.baseFontSize }">
        <i class="ri-file-excel-2-line"></i>
        <span>{{ $t('导出') }}</span>
      </el-button>
      <el-button class="global-btn-third" v-print="'#printTest'" :size="fontSizeObj.buttonSize"
      :style="{ fontSize: fontSizeObj.baseFontSize }">
        <i class="ri-printer-line"></i>
        <span>{{ $t('打印') }}</span>
      </el-button>
    </template>
  </y9Table>

  <div style="display: none;">
    <div id="printTest" style="width: 750px;">
      <el-table border style="width: 100%;margin: auto;" :data="listData">
        <el-table-column :label="$t('序号')" type="index" align="center" width="60"></el-table-column>
        <el-table-column prop="USERNAME" :label="$t('姓名')" align="center" width="200"></el-table-column>
        <el-table-column prop="DEPTNAME" :label="$t('部门名称')" align="center" width="200"></el-table-column>
        <el-table-column prop="LEAVETYPE" :label="$t('事项类型')" align="center" width="100"></el-table-column>
        <el-table-column prop="leaveDuration" :label="$t('合计')" align="center" width="100"></el-table-column>
        <el-table-column prop="danwei" :label="$t('单位')" align="center" width="90"></el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { ref, defineProps, onMounted, watch,reactive, inject} from 'vue';
  import {countList} from "@/api/flowableUI/leaveCount";
  import y9_storage from "@/utils/storage";
  import settings from '@/settings';
  import { useSettingStore } from "@/store/modules/settingStore";
  const settingStore = useSettingStore();
  import { useI18n } from 'vue-i18n';
import { computed } from 'vue';
  const { t } = useI18n();
  // 注入 字体对象
  const fontSizeObj: any = inject('sizeObjInfo')||{}; 
  const data = reactive({
    filterRef:'',
    currFilters:{},//当前选择的过滤数据
    listData:[],
    tableConfig: {//表格配置
      border: false,
      headerBackground: true,
      columns: [
        { title: computed(() =>t("序号")), type:'index', width: '60', },
        { title: computed(() =>t("姓名")), key: "USERNAME", width: '300',},
        { title: computed(() =>t("部门名称")), key: "DEPTNAME", width: 'auto'},
        { title: computed(() =>t("事项类型")), key: "LEAVETYPE", width: '150'},
        { title: computed(() =>t("合计")), key: "leaveDuration", width: '150', },
        { title: computed(() =>t("单位")), key: "danwei", width: '150', },
      ],
      tableData: [],
      height:window.innerHeight - 280,
      pageConfig:false,
    },
    filterConfig:{//过滤配置
			itemList:[
          {
            type:"search",
            key:"userName",
            props:{
              placeholder: computed(() =>t('姓名'))
            },
            span:settingStore.device === 'mobile'?6:3,
          },
          {
            type:"search",
            key:"deptName",
            props:{
              placeholder: computed(() =>t('部门名称'))
            },
            span:settingStore.device === 'mobile'?6:3,
          },
          {
            type:'select',
            key:'leaveType',
            label: computed(() =>t('类型')),
            props:{
              placeholder:"请选择类型",
              options:[
                {label:'全部',value:''},
                {label:'年假',value:'年假'},
                {label:'事假',value:'事假'},
                {label:'病假',value:'病假'},
                {label:'产假',value:'产假'},
                {label:'产检假',value:'产检假'},
                {label:'陪产假',value:'陪产假'},
                {label:'育儿假',value:'育儿假'},
                {label:'婚假',value:'婚假'},
                {label:'丧假',value:'丧假'},
                {label:'哺乳假',value:'哺乳假'},
                {label:'调休',value:'调休'},
                {label:'入职申请',value:'入职申请'},
                {label:'转正申请',value:'转正申请'},
                {label:'离职申请',value:'离职申请'},
                {label:'公出',value:'公出'},
                {label:'其他',value:'其他'}
              ],
              events:{
                change:leaveTypechange,
              }
            },
            span:settingStore.device === 'mobile'?6:3,
          },
          {
            type:'date',
            key:'startTime',
            label:computed(() =>t('月份')),
            props:{
              clearable:true,
              dateType:"month",
              format:"YYYY-MM",
              formatValueType:false,
              valueFormat:"YYYY-MM"
            },
            span:settingStore.device === 'mobile'?6:4,
          },
          {
            type:'date',
            key:'endTime',
            label:computed(() =>t('至')),
            props:{
              clearable:true,
              dateType:"month",
              format:"YYYY-MM",
              formatValueType:false,
              valueFormat:"YYYY-MM"
            },
            span:settingStore.device === 'mobile'?6:4,
          },
          {
            type:"slot",
            span: settingStore.device === 'mobile' ? 12 : 1,
            slotName: "buttonslot",
          },
			],
			filtersValueCallBack:(filters) => {//过滤值回调
				currFilters.value = filters
			},
		},
  })
    
  let {
    filterRef,
    currFilters,
    filterConfig,
    tableConfig,
    listData
  } = toRefs(data);
  
  onMounted(()=>{
    let yy = new Date().getFullYear();
    let mm = new Date().getMonth() + 1;
    let MM = '';
    if(mm < 10){
      MM = "0" + mm.toString();
    }else{
      MM = mm.toString();
    }
    filterConfig.value.itemList.forEach(items => {
      if (items.type == 'select' && items.key == 'leaveType') {
        items.value = items.props.options[0].value;//默认选择第一项
        return items.props.options;
      }else if(items.key == 'startTime'){
        items.value = yy + "-" + MM;
      }else if(items.key == 'endTime'){
        items.value = yy + "-" + MM;
      }
    });
    currFilters.value.startTime =  yy + "-" + MM;
    currFilters.value.endTime =  yy + "-" + MM;
    reloadTable();
  });

  function leaveTypechange(val){
    reloadTable();
  }

  function refreshTable(){
    let yy = new Date().getFullYear();
    let mm = new Date().getMonth() + 1;
    let MM = '';
    if(mm < 10){
      MM = "0" + mm.toString();
    }else{
      MM = mm.toString();
    }
    currFilters.value.leaveType  = '';
    currFilters.value.userName  = '';
    currFilters.value.deptName  = '';
    currFilters.value.startTime  = yy + "-" + MM;
    currFilters.value.endTime  = yy + "-" + MM;
    filterRef.value.elTableFilterRef.onReset();
    setTimeout(() => {
      reloadTable();
    }, 500);
  }

  async function reloadTable(){
    tableConfig.value.loading = true;
    let res = await countList(currFilters.value.leaveType,currFilters.value.userName,currFilters.value.deptName,currFilters.value.startTime,currFilters.value.endTime);
    tableConfig.value.tableData = res.data;
    listData.value = res.data;
    tableConfig.value.loading = false;
  }

  function exportData(){
    let leaveType = currFilters.value.leaveType == undefined ? '' : currFilters.value.leaveType;
    let deptName = currFilters.value.deptName == undefined ? '' : currFilters.value.deptName;
    let userName = currFilters.value.userName == undefined ? '' : currFilters.value.userName;
    let startTime = currFilters.value.startTime == undefined ? '' : currFilters.value.startTime;
    let endTime = currFilters.value.endTime == undefined ? '' : currFilters.value.endTime;
    window.open(import.meta.env.VUE_APP_CONTEXT + "vue/leaveCount/exportExcel?leaveType="+ leaveType
    +"&userName="+userName+"&deptName="+deptName+"&startTime="+startTime+"&endTime="+endTime
    +"&access_token="+y9_storage.getObjectItem(settings.siteTokenKey, 'access_token'));
  }
</script>

<style>
</style>