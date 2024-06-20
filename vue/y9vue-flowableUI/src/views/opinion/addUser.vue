<template>
   <div style="height:350px;border: 0px solid #ccc;overflow-y: auto;padding: 8px;">
    <y9Filter :itemList="filtersList" :filtersValueCallBack="filtersValueCallBack"></y9Filter>    
    <orgTree ref="orgTreeRef" :treeApiObj="treeApiObj" :showHeader="showHeader" v-model:selectedData="treeSelectedData"/>
   </div>
</template>

<script lang="ts" setup>
import {getBureauTree,getBureauTreeById,bureauTreeSearch} from "@/api/flowableUI/opinion";

const data = reactive({
  orgTreeRef:"",//tree实例
		treeApiObj:{//tree接口对象
			topLevel: '',
			childLevel:{//子级（二级及二级以上）tree接口
				api:'',
				params:{}
			},
			search:{
				api:'',
				params:{
					key:'',
				}
			},
		},
		treeSelectedData: [],//tree选择的数据
		showHeader:true,
    currFilters:{},//当前过滤值
		filtersValueCallBack:(filters)=>{//过滤回调值
			  currFilters.value = filters;
        treeApiObj.value.search.api = bureauTreeSearch;
        treeApiObj.value.search.params.key = currFilters.value.name;
		},
    filtersList: [
        {
          type:"search",
          span:24,
          key:"name"
        }
    ]
  });   

  let {
    currFilters,
    filtersValueCallBack,
    filtersList,
		orgTreeRef,
		treeApiObj,
		showHeader,
		treeSelectedData,
	} = toRefs(data);

  showTree();
   function showTree(){
      treeSelectedData.value = [];
      treeApiObj.value.topLevel = getBureauTree;
      treeApiObj.value.childLevel.api = getBureauTreeById;
      //treeApiObj.value.childLevel.params.treeType = 'Role';
      
      showHeader.value = false;

      setTimeout(() => {
          orgTreeRef.value.onRefreshTree();
      }, 500);
    }

defineExpose({
  treeSelectedData
});
</script>
 
<style>
  .adduser .el-dialog__body{
    padding: 0 10px 0px 10px;
  }
</style>