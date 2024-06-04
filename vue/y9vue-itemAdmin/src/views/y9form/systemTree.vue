<!--
 * @Descripttion: 
 * @version: 
 * @Author: zhangchongjie
 * @Date: 2022-07-06 15:21:52
 * @LastEditors: zhangchongjie
 * @LastEditTime: 2023-06-16 10:16:24
 * @FilePath: \workspace-y9boot-9.5-liantong-vued:\workspace-y9boot-9.6-vue\y9vue-itemAdmin\src\views\y9form\systemTree.vue
-->
<template>
	<div id="fixedDiv1" class="fixed">
		<slot name="leftFixed">
			<y9Card :showHeader="false">
				<y9Tree
					ref="y9TreeRef"
					:data="alreadyLoadTreeData"
					:lazy="lazy"
					:load="onTreeLazyLoad"
					@node-click="onNodeClick">
					<template #title="{item}">
						<i :class="item.title_icon"></i>
						<span>{{item[nodeLabel]}}</span>
					</template>
				</y9Tree>
			</y9Card>
		</slot>
	</div>
	<div class="right-container">
		<slot name="rightContainer"></slot>
	</div>
</template>

<script lang="ts" setup>
	 import { $dataType, $deepAssignObject, $deeploneObject } from '@/utils/object'; //工具类
	 import { inject, ref, useCssModule, watch } from 'vue';
	 import {getAppList} from '@/api/itemAdmin/y9form';
	 // 注入 字体对象
	 const props = defineProps({
		 treeApiObj: { //tree接口对象,参数名称请严格按照以下注释进行传参。
			type: Object,
		 },
 
		 selectField: {//设置需要选择的字段
			 type: Array,
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
			 default:false
		 },
		 
		 highlightCurrent: { //是否高亮当前选中节点
			 type: Boolean,
			 default: true,
		 },

		 showNodeDelete: { //是否显示删除icon
			type: Boolean,
			default: true,
		},
	 });
	 
	//tree实例
	const y9TreeRef = ref();

	 //已经加载的tree数据
	 const alreadyLoadTreeData = ref([]);
 
	 const emits = defineEmits(['onTreeClick', 'update:onCheckBox', 'change','onCheckChange']);
 
	 //点击节点
	 const onNodeClick = (node) => {
		emits('onTreeClick',node);
		y9TreeRef.value?.setCurrentKey(node.id);//设置为高亮节点
	 }
	
	 //格式化懒加载的数据
	 function formatLazyTreeData(data,isTopLevel?) {
		//  for(let i = 0; i < data.length; i++){
		// 	const item = data[i];
		//  }
	 }
	 
	 //懒加载
	 const onTreeLazyLoad = async (node, resolve: () => void) => {
	 
		 if (node.$level === 0) {
			 
			 //1.获取数据
			 let data = [];
			 const res = await getAppList();//请求一级节点接口
			 data = res.data || res;
			 
			 let children = [];
			 let parent = [];
			let parentNode = {};
			data.map((item,index) => {
				if(index == 0){
					item.title_icon = 'ri-folder-2-line';
					parentNode = item;
				}else{
					item.title_icon = 'ri-apps-line';
					item.isLeaf = true;//叶子节点（即没有展开按钮）
					children.push(item);
				}
			})
			parentNode.children = children;
			parent.push(parentNode);
			 //2.格式化数据
			 await formatLazyTreeData(parent,true)
			 
			 //初始化数据后，默认选中第一个节点、设置第一个节点展开，模拟点击第一个节点
			const callBack = () => {
				if(parent.length > 0){
					y9TreeRef.value.setCurrentKey(parent[0].id);//设置第一个节点为高亮节点
					y9TreeRef.value.setExpandKeys([parent[0].id])//设置第一个节点展开
					// onNodeClick(parent[0]);//模拟点击第一个节点
				}
					
			}
			 
			 return resolve(parent,callBack)//返回一级节点数据
				 
	 
		 } else {
			 
			 //1.获取数据
			 
			 //2.格式化数据
			 await formatLazyTreeData(data,false)
			 
			 return resolve(data);//返回二级三级...节点数据
		 }
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

	 defineExpose({
		 y9TreeRef,
		 onRefreshTree, //刷新tree
		 getTreeData,
	 });
 </script>

<style lang="scss" scoped>
@import "@/theme/global.scss";
@import "@/theme/global-vars.scss";
@import "@/theme/global.scss";
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
//y9Card插槽头部样式
.y9Card-slot-header {
	display: flex;
	justify-content: space-between;
	flex-wrap: wrap;
	.header-left {
		display: flex;
		:deep(.el-input) {
			.el-input__inner,
			.ri-search-line {
				line-height: 30px !important;
				height: 30px !important;
			}

			.el-input__clear {
				margin-left: 4px;
			}
		}
	}
}

.right-container {
	height: 100%;
	margin-left: calc(18vw + 35px);
	:deep(.y9-card) {
		margin-bottom: 35px;
		height: calc( 50% - 17.5px );
	}

	:deep(.y9-card:last-child) {
		margin-bottom: 0px;
	}
}

/* 固定左侧树 */
.fixed {
	position: fixed;
	width: 18vw;
	animation: moveTop-2 0.2s;
	animation-fill-mode: forwards;
	:deep(.y9-card) {
		height: calc(
			100vh - #{$headerHeight} - #{$headerBreadcrumbHeight} - 35px
		);
		overflow: hidden;
	}
}

.fixed-herder-horizontal {
	margin-top: 60px;
	:deep(.y9-card) {
		height: calc(
			100vh - #{$headerHeight} - #{$headerBreadcrumbHeight} - 95px
		);
		overflow: hidden;
	}
}

.fixed-after-scroll {
	position: fixed;
	width: 26.6vw;
	animation: moveTop 0.2s;
	animation-fill-mode: forwards;
	:deep(.y9-card) {
		height: calc(100vh - 35px - 35px);
		overflow: auto;
	}
}
@keyframes moveTop {
	0% {
		top: calc(#{$headerHeight} + #{$headerBreadcrumbHeight});
	}
	100% {
		top: 35px;
	}
}
@keyframes moveTop-2 {
	0% {
		top: 35px;
	}
	100% {
		top: calc(#{$headerHeight} + #{$headerBreadcrumbHeight});
	}
}
</style>



<style lang="scss" module="classes">
/* 点击选中的activeClass */
.active {
	background-color: var(--el-color-primary-light-3) !important;
	color: var(--el-color-white) !important;

	/* 改变icon的颜色，没有效果 */
	& > .info > :last-child > div > i {
		color: var(--el-color-white) !important;
	}
}
</style>