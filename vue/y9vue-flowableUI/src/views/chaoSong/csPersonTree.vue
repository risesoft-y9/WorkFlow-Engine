<template>
  <el-container class="personTree" style="height:525px;">
    <el-header style="height:36px;padding:0;">
      <el-input style="width: 100%;" :prefix-icon="Search" v-model="searchName" :placeholder="$t('请输入名称按回车键搜索')" @keyup.enter.native="search()" clearable></el-input>
    </el-header>
    <el-main  width="45%" style="height:auto;overflow: hidden; border-top: 0px solid #ccc;border-left: 0;border-right: 0;border-bottom: 0;">
      <el-menu default-active="2" class="el-menu-demo" mode="horizontal" ref="csMenu"  @select="handleSelect">
        <el-menu-item index="2">{{ $t('部门') }}</el-menu-item>
        <el-menu-item index="7">{{ $t('用户组') }}</el-menu-item>
      </el-menu>
      <div class="mytreediv" style="width: 100%;height: 91%;overflow-y: auto;">
        <y9Tree
          ref="y9TreeRef"
          showCheckbox
          :checkStrictly="checkStrictly"
          :highlightCurrent="highlightCurrent"
          :data="alreadyLoadTreeData"
          :lazy="lazy"
          :load="onTreeLazyLoad"
		  :nodeDblclick="true"
          @node-click="onNodeClick"
          @node-dblclick="nodeDblclick"
          @node-expand="onNodeExpand"
          @check-change="onCheckChange">
            <template #title="{item}">
              <i :class="item.title_icon"></i>
              <span>{{item[nodeLabel]}}</span>
            </template>
        </y9Tree>
      </div>
    </el-main>
  </el-container>
</template>
<script lang="ts" setup>
  import { Search } from '@element-plus/icons-vue'
	 import { $dataType, $deepAssignObject, $deeploneObject } from '@/utils/object'; //工具类
	 import { inject, ref, useCssModule, watch,reactive} from 'vue';
   import {findCsUser,findCsUserSearch} from "@/api/flowableUI/personTree";
	 // 注入 字体对象
   const data = reactive({
      searchName:'',
      principalType:2,
      selectField:[
        {
          fieldName: 'orgType',
          value: ['Department','Position','customGroup'],
		    }
      ],//设置需要选择的字段
  });

  let {
    searchName,
    principalType,
    selectField,
	} = toRefs(data);

	 const props = defineProps({
		 treeApiObj: { //tree接口对象,参数名称请严格按照以下注释进行传参。
			   type: Object,
		 },
		 showHeader: { //是否显示头部
			 type: Boolean,
			 default: true,
		 },
		 nodeLabel: { //显示的节点属性
			 type:String,
			 default:'name'
		 },
		 
		 checkStrictly:{ //是否严格的遵循父子不互相关联的做法，默认为 false,父子互相关联
			 type: Boolean,
			 default:true
		 },
		 
		 highlightCurrent: { //是否高亮当前选中节点
			 type: Boolean,
			 default: true,
		 },
     basicData: { //参数
			   type: Object,
		 },
	 });
	 
	 
	 //已经加载的tree数据
	 const alreadyLoadTreeData = ref([]);
 
	 const emits = defineEmits(['onTreeClick','onTreeDbClick', 'update:onCheckBox', 'change','onCheckChange','onNodeExpand']);
 
	 let nodeClickTimer = null;//单击事件的计时器
	 //点击节点
	 const onNodeClick = (node) => {
		 clearTimeout(nodeClickTimer);
		 nodeClickTimer = setTimeout(() => {
			emits('onTreeClick',node);
		},200)
	 }
	 
	 const nodeDblclick = (node) => {
		clearTimeout(nodeClickTimer);
		 emits('onTreeDbClick',node);
	 }
	 
	 
	 
	 //节点被展开时触发的事件
	 const onNodeExpand = (node) => {
		 emits('onNodeExpand',node);
	 }
	 
	 //当复选框被点击的时候触发
	 const onCheckChange = (node,isChecked,childIsHaveChecked) => {
		 emits('onCheckChange',node,isChecked,childIsHaveChecked);
		 
	 }
	
	 
	 //tree实例
	 const y9TreeRef = ref();
	 
	 //格式化懒加载的数据
	 function formatLazyTreeData(data,isTopLevel?) {
		 for(let i = 0; i < data.length; i++){
				 const item = data[i];
				 if(selectField.value.length>0){
					 //根据需要选择框的字段进行格式化
					 const disabled:boolean = selectField.value.every((item2) => {//every()方法会遍历数组的每一项,如果有一项不满足条件,则返回false,剩余的项将不会再执行检测。如果遍历完数组后,每一项都符合条,则返回true。
						 if ($dataType(item2.value) == 'array') {
							 return item2.value.includes(item[item2.fieldName]);
						 } else {
							 return item[item2.fieldName] == item2.value;
						 }
					 });
					 if(!item.disabled){
						 item.disabled = !disabled;//设置不可以选中的字段禁止选择
					 }
				 }
				 
				 switch (item.orgType) {
					 case 'Organization'://组织
						 item.title_icon = 'ri-stackshare-line';
						 break;
						 
					 case 'Department'://部门
						 item.title_icon = 'ri-slack-line';
						 break;
			 
					 case 'Group'://组
						 item.title_icon = 'ri-shield-star-line';
              item.isLeaf = true;//叶子节点（即没有展开按钮）
						 break;

            case 'customGroup'://用户组
						  item.title_icon = 'ri-shield-star-line';
						break;
			 
					 case 'Position'://岗位
						 item.title_icon = 'ri-shield-user-line';
              item.isLeaf = true;//叶子节点（即没有展开按钮）
						 break;
			 
					 case 'Person'://人员
						 item.isLeaf = true;//叶子节点（即没有展开按钮）
						 item.title_icon = 'ri-women-line';
						 if (item.sex == 1) {
							 item.title_icon = 'ri-men-line';
						 }
						 if (item.disabled) {
							 item.name = item.name + (item.disabledRemark?item.disabledRemark:"[禁用]");
						 }
						 break;
					 default:
						 item.title_icon = '';
				 }
		 }
	 }
	 
	 //懒加载
	 const onTreeLazyLoad = async (node, resolve: () => void) => {
	 
		 if (node.$level === 0) {
			 
			 //1.获取数据
			 let data = [];
			 const res = await findCsUser(principalType.value,props.basicData.processInstanceId);//请求一级节点接口
			 data = res.data || res;
			 
			 //2.格式化数据
			 await formatLazyTreeData(data,true)
			 
			 return resolve(data)//返回一级节点数据
		 } else {
			 //1.获取数据
			 let data = [];
			 if(node.children && node.children.length > 0){//如果有传入的数据就取传入的数据
				 data = node.children;
			 }else{//如果没有则取接口数据
				 //整合参数
				 let params = {}
				 const childLevelParams = props.treeApiObj?.childLevel?.params
				 if(childLevelParams){
					 params = childLevelParams;
				 }
				 params.parentId = node.id;
				 //请求接口
				 const res = await findCsUser(principalType.value,props.basicData.processInstanceId,params.parentId);
				 data = res.data || res;
			 }
			 
			 //2.格式化数据
			 await formatLazyTreeData(data,false)
			 
			 return resolve(data);//返回二级三级...节点数据
		 }
	 }
	 
	 //请求搜索api
	 async function search(){
		  if(searchName.value){//有值就请求搜索api
				 //整合参数
				 let params = {
					name:searchName.value,
          principalType: principalType.value,
          processInstanceId: props.basicData.processInstanceId,
				}
				 
				 //请求搜索接口
				 const res = await findCsUserSearch(params);
         
				 const data = res.data;
				 //格式化tree数据
				 await formatLazyTreeData(data)

				await data?.map(item => {
					let child = data.filter(resultItem => item.parentId === resultItem.id);
					if(child.length == 0){
						item.parentId = "";
					}
				 })
				 //根据搜索结果转换成tree结构显示出来
				 alreadyLoadTreeData.value = transformTreeBySearchResult(data);
				 nextTick(() => {
					 y9TreeRef.value.setExpandAll()
				 })
			 }else{//没有就获取获取已经懒加载过的数据，并且设置默认选中第一个节点、默认展开第一个节点，模拟点击第一个节点
				 
				 alreadyLoadTreeData.value = y9TreeRef.value.getLazyTreeData();//获取已经懒加载过的数据
				 
				 nextTick(() => {
					 if(alreadyLoadTreeData.value.length > 0){
						 y9TreeRef.value.setCurrentKey(alreadyLoadTreeData.value[0].id);//设置第一个节点为高亮节点
						 
						 y9TreeRef.value.setExpandKeys([alreadyLoadTreeData.value[0].id])//设置第一个节点展开
						 
						 onNodeClick(alreadyLoadTreeData.value[0]);//模拟点击第一个节点
					 }
				 })
			 }
	 }
	 
	 
	 //根据搜索结果转换成tree结构
	 function transformTreeBySearchResult(result){
			 const treeData = [];
			 for(let i = 0; i<result.length; i++){
				 const item = result[i];
				 if(!item.parentId){//一级节点
					 let node = item;
					 const child = result.filter(resultItem => resultItem.parentId === item.id);
					 if(child.length > 0){//如果有子节点则递归子节点，进行组合
						 item.children = child;
						 const fn2 = (data) => {
							 for(let j = 0; j<data.length; j++){
								 const itemJ = data[j];
								 const childs = result.filter(resultItem => resultItem.parentId === itemJ.id);
								 if(childs.length > 0){
									 itemJ.children = childs
									 if(itemJ.children.length > 0){
										 fn2(itemJ.children)
									 }
								 }
							 }
						 }
						 fn2(item.children);//递归子节点
					 }
					 treeData.push(item)
				 }
			 }
			 return treeData
	 }
	 
	 //是否懒加载
	 const lazy:boolean = ref(true);
	 
	 //刷新tree
	 const onRefreshTree = () => {
		 alreadyLoadTreeData.value = [];
		 lazy.value = false;
		 setTimeout(() => {
			 lazy.value = true;
		 },0)
	 }
	 
	 /**根据id查找tree节点信息
	  * @param {Object} data 树的数据
	  * @param {Object} targetId 目标节点id
	  */
	 function findNode(data,targetId){
		 let currNodeInfo = null;;
		 const fun = (data,targetId) => {
			 for(let i = 0; i<data.length; i++){
				 const item = data[i];
				 if(item.id === targetId){
					 currNodeInfo = item;
					 break;
				 }
				 if(item.children && item.children.length > 0){
					 fun(item.children,targetId);
				 }
			 }
		 }
		 fun(data,targetId);
		 return currNodeInfo;
	 }
	 
	 //设置树数据
	 async function setTreeData(data){
		 await formatLazyTreeData(data);
		 alreadyLoadTreeData.value = data;
	 }
	 
	 //获取树数据
	 function getTreeData(){
		  return alreadyLoadTreeData.value 
	 }


   function handleSelect(key) {
    principalType.value = parseInt(key);
    onRefreshTree();
   }

 
	 defineExpose({
		 y9TreeRef,
		 onRefreshTree, //刷新tree
		 getTreeData,
	 });
 </script>
 
 <style lang="scss" scoped>
	 @import '@/theme/global.scss';
	 @import '@/theme/global-vars.scss';
	 @import '@/theme/global.scss';
	 
 
	 //过滤样式
	 .select-tree-filter-div {
	   display: flex;
	   align-items: center;
	   flex-wrap: wrap;
		margin-bottom: 16px;
	   .el-button {
		   margin-right: 16px;
			margin-bottom: 10px;
	   }
	   :deep(.el-input){
		   width: 250px;
		   max-width:100%;
			 margin-bottom: 10px;
		   .el-input__wrapper{
			   border-radius: 30px;
			   box-shadow: 0 2px 4px 0 rgb(0 0 0 / 5%);
			   border:1px solid var(--el-color-primary-light-7);
		   }
		  
		 
	   }
	 }
	 
	 :deep(.node-title){
		 display: inline-flex;
		 align-items: center;
		 i{
			 margin-right: 5px;
			 font-weight: normal;
			 
		 }
	 }
	 :deep(.active-node){
		 .y9-tree-item-content{
			 .y9-tree-item-content-div{
				 .action-icons{
					 i{
						 color: var(--el-color-white) !important;
					 }
					 
				 }
			 }
		 }
		 
	 }
 </style>