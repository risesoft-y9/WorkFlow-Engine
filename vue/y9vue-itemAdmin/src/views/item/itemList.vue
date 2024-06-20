<!--
 * @Descripttion:
 * @version:
 * @Author: zhangchongjie
 * @Date: 2022-05-05 11:38:27
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2024-06-04 16:46:31
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9cloud-v9.6\y9-flowable\vue\y9vue-itemAdmin\src\views\item\itemList.vue
-->
<template>
    <itemTree ref="itemTreeRef" :treeApiObj="treeApiObj" @onTreeClick="onTreeClick" @onDeleteTree="onDeleteTree">
		<template #treeHeaderRight>
            <el-button type="primary" class="global-btn-main" @click="addItem">
                <i class="ri-add-line"></i>
                <span>新增</span>
            </el-button>
		</template>
		<template #rightContainer>
            <div id="loutinav">
                <ul>
                    <li v-for="(item,index) in menuBar" :class="{active:boxType==item.type}" @click="changeBox(item.type)">
                    <a :href="item.type">{{item.name}}</a></li>
                    <li class="last"><a href="#itemBox" :class="{active:boxType=='#itemBox'}" @click="changeBox('#itemBox')"><i class="ri-align-top"></i>顶部</a></li>
                </ul>
            </div>
            <template v-if="Object.keys(currTreeNodeInfo).length > 0">
                <div id="itemBox" class="topBoxDiv">
                    <baseInfo :currTreeNodeInfo="currTreeNodeInfo" :updateItem="updateItem" :itemList="itemList"></baseInfo>
                </div>
                <div id="formBox" class="boxDiv">
                    <formConfig :currTreeNodeInfo="currTreeNodeInfo" :processDefinitionList="processDefinitionList" :selectVersion="selectVersion" :maxVersion="maxVersion" :selVersion="selVersion"></formConfig>
                </div>
                <div id="premBox" class="boxDiv">
                    <permConfig :currTreeNodeInfo="currTreeNodeInfo" :processDefinitionId="processDefinitionId" :selectVersion="selectVersion" :maxVersion="maxVersion" />
                </div>
                <div id="opinionBox" class="boxDiv">
                    <opinionFrameConfig :currTreeNodeInfo="currTreeNodeInfo" :processDefinitionId="processDefinitionId" :selectVersion="selectVersion" :maxVersion="maxVersion" />
                </div>
                <div id="preFormBox" class="boxDiv">
                    <preFormConfig :currTreeNodeInfo="currTreeNodeInfo" />
                </div>
                <div id="organBox" class="boxDiv">
                    <organWordConfig :currTreeNodeInfo="currTreeNodeInfo" :processDefinitionId="processDefinitionId" :selectVersion="selectVersion" :maxVersion="maxVersion" />
                </div>
                <div id="printBox" class="boxDiv">
                    <printConfig :currTreeNodeInfo="currTreeNodeInfo"></printConfig>
                </div>
                <div id="signBox" class="boxDiv">
                    <signConfig :currTreeNodeInfo="currTreeNodeInfo" :processDefinitionId="processDefinitionId" :selectVersion="selectVersion" :maxVersion="maxVersion" />
                </div>
                <div id="routerBox" class="boxDiv">
                    <startNodeConfig :currTreeNodeInfo="currTreeNodeInfo" :processDefinitionId="processDefinitionId" :selectVersion="selectVersion" :maxVersion="maxVersion" />
                </div>
                <div id="buttonBox" class="boxDiv">
                    <buttonConfig :currTreeNodeInfo="currTreeNodeInfo" :processDefinitionId="processDefinitionId" :selectVersion="selectVersion" :maxVersion="maxVersion" />
                </div>
                <div id="viewBox" class="boxDiv">
                    <viewConfig :currTreeNodeInfo="currTreeNodeInfo" />
                </div>
                <div id="mappingBindBox" class="boxDiv">
                    <mappingConfig :currTreeNodeInfo="currTreeNodeInfo" :itemList="itemList"/>
                </div>
                <div id="dataMoveBox" class="boxDiv">
                    <dataTransfer :currTreeNodeInfo="currTreeNodeInfo" :processDefinitionId="processDefinitionId" :selectVersion="selectVersion" :maxVersion="maxVersion"/>
                </div>
           </template>
		</template>
	</itemTree>
    <y9Dialog v-model:config="listDialogConfig">
		<itemForm ref="itemFormRef" isEditState="true" :itemList="itemList"></itemForm>
	</y9Dialog>
</template>
<script lang="ts" setup>
import { ref, defineProps, onMounted, watch,reactive, onBeforeMount} from 'vue';
import type {ElMessageBox, ElMessage,ElLoading } from 'element-plus';
import baseInfo from './baseInfo.vue';
import itemForm from './itemForm.vue';
import formConfig from './config/formConfig/formConfig.vue';
import permConfig from './config/permConfig/permConfig.vue';
import opinionFrameConfig from './config/opinionFrameConfig/opinionFrameConfig.vue';
import organWordConfig from './config/organWordConfig/organWordConfig.vue';
import printConfig from './config/printConfig/printConfig.vue';
import signConfig from './config/signConfig/signConfig.vue'; 
import startNodeConfig from './config/startNodeConfig/startNodeConfig.vue'; 
import buttonConfig from './config/buttonConfig/buttonConfig.vue';
import viewConfig from './config/viewConfig/viewConfig.vue';
import dataTransfer from './config/dataTransfer/dataTransfer.vue';
import mappingConfig from './config/mappingConfig/mappingConfig.vue';
import {saveItem,getItemList,deleteItem} from '@/api/itemAdmin/item/item';
import {getProcessDefinitionList} from "@/api/itemAdmin/item/itemAdminConfig";
import preFormConfig from './config/preFormConfig/index.vue';
//数据
const data = reactive({
    itemTreeRef:"",//tree实例
    treeApiObj:{//tree接口对象
        topLevel: getItemList,
    },
    currTreeNodeInfo:{},//当前tree节点的信息
    boxType:'#itemBox',
    menuBar:[
        {type:'#itemBox',name:'事项信息'},
        {type:'#formBox',name:'表单配置'},
        {type:'#premBox',name:'权限配置'},
        {type:'#opinionBox',name:'意见框配置'},
        {type:'#preFormBox',name:'前置表单配置'},
        {type:'#organBox',name:'编号配置'},
        {type:'#printBox',name:'打印配置'},
        {type:'#signBox',name:'签收配置'},
        {type:'#routerBox',name:'路由配置'},
        {type:'#buttonBox',name:'按钮配置'},
        {type:'#viewBox',name:'视图配置'},
        {type:'#mappingBindBox',name:'表单字段映射'},
        {type:'#dataMoveBox',name:'实例迁移'},
    ],
    //弹窗配置
    listDialogConfig: {
        show: false,
        title: "",
        onOkLoading: true,
        onOk: (newConfig) => {
            return new Promise(async (resolve, reject) => {
                let result = {success:false,msg:''};
                let valid = await itemFormRef.value.validForm();
                if(!valid){
                    reject();
                    return;
                }
                let formData = itemFormRef.value.itemForm;
                result = await saveItem(JSON.stringify(formData).toString());
                ElNotification({
                    title: result.success ? '成功' : '失败',
                    message: result.msg,
                    type: result.success ? 'success' : 'error',
                    duration: 2000,
                    offset: 80
                });
                if(result.success){
                    itemTreeRef.value.onRefreshTree();
                }
                resolve();
            })
        },
        visibleChange:(visible) => {
            if(!visible){
                listDialogConfig.value.onOkLoading = false;
            }
        }
    },
    y9ListRef:"",//气泡框的列表实例
    itemFormRef:"",
    processDefinitionId:'',
    selectVersion:1,
    maxVersion:1,
    itemList:[],
    processDefinitionList:[],
})

const {
    boxType,
    menuBar,
    itemTreeRef,
    treeApiObj,
    currTreeNodeInfo,
    listDialogConfig,
    itemFormRef,
    processDefinitionId,
    selectVersion,
    maxVersion,
    itemList,
    processDefinitionList
} = toRefs(data);

onMounted(()=>{
    itemList.value = itemTreeRef.value.getTreeData();
});

//点击tree的回调
async function onTreeClick(currTreeNode){
    let res = await getProcessDefinitionList(currTreeNode.workflowGuid);
    if(res.success){
        processDefinitionList.value = res.data;
        if(processDefinitionList.value.length > 0){
            maxVersion.value = processDefinitionList.value[0].version;
            selectVersion.value = processDefinitionList.value[0].version;
            processDefinitionId.value = processDefinitionList.value[0].id;
        }
    }
    currTreeNode.processDefinitionId = processDefinitionId.value;
    currTreeNodeInfo.value = currTreeNode;
}

function changeBox(type){
    boxType.value = type;
}

function updateItem(){
    itemTreeRef.value.onRefreshTree();
    itemList.value = itemTreeRef.value.getTreeData();
}

function addItem() {
    Object.assign(listDialogConfig.value,{
        show:true,
        title:'新增事项',
        showFooter:true
    })
}

function onDeleteTree(data){
    ElMessageBox.confirm(
        `是否删除【${data.name}】?`,
        '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info',
    }).then(async () => {
        let result = {success:false,msg:''};
        result = await deleteItem(data.id)
        ElNotification({
            title: result.success ? '成功' : '失败',
            message: result.msg,
            type: result.success ? 'success' : 'error',
            duration: 2000,
            offset: 80
        });
        if(result.success){
            itemTreeRef.value.onRefreshTree();
        }
    }).catch((e) => {
        ElMessage({
            type: 'info',
            message: '已取消删除',
            offset: 65
        });
    });
}

//选择流程版本
function selVersion(processDefinitionId1,selectVersion1) {
    processDefinitionId.value = processDefinitionId1;
    selectVersion.value = selectVersion1;
    currTreeNodeInfo.value.processDefinitionId = processDefinitionId1;
}
</script>

<style lang="scss">

.y9-dialog .y9-dialog-content{
    padding: 26px 26px !important;
}

#loutinav {
        width: auto;
        position: fixed;
        top: 125px;
        right: 17.5px;
        z-index: 0;
    }
    
    #loutinav ul li {
        width: 100px;
        height: 35px;
        border-bottom: 1px dotted #DDDDDD;
        list-style: none;
        font-size: 14px;
        text-align: center;
        position: relative;
        cursor: pointer;
        padding: 10px 0;
        background: #ffffff;
        color: #fff;
        &.active {
            background: var(--el-color-primary);;
            color: #fff;
            display: block;
        }
    }
    #loutinav ul{
        padding: 0;
    }
    #loutinav ul li a {
        width: 100px;
        height: 35px;
        padding: 10px 0;
        position: absolute;
        top: 0;
        left: 0;
        color: #303133;
        text-decoration: none;
    }
    
    #loutinav ul li.last {
        background: rgb(0 0 0 / 6%);
        color: #fff;
        border-bottom: 1px solid #ddd;
    }

    #loutinav ul li.active a {
        background: var(--el-color-primary);;
        color: #fff;
        display: block;
    }

    #loutinav ul li:hover a {
        background: var(--el-color-primary);;
        color: #fff;
        display: block;
    }

    .right-container[data-v-36f781e4] .y9-card {
        margin-bottom: 35px;
    }

     .boxDiv {
        margin-top: 35px;
        width: calc(100% - 100px);
     } 
     
     .topBoxDiv {
        width: calc(100% - 100px);
     } 

     .boxDiv .el-table{
        overflow-y:auto;
     }
     .topBoxDiv .el-table{
        overflow-y:auto;
     }

</style>
