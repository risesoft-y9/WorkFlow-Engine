<!-- 两个弹框 -->
<template>
    <fixedTreeModule ref="fixedTreeRef" :treeApiObj="treeApiObj" @onTreeClick="onTreeClick">
		<template #rightContainer>
            <SendReceive ref="sendReceive" :deptName="deptName" :deptId="deptId" :isReceiveSendDept="isReceiveSendDept"/>
		</template>
	</fixedTreeModule>
	
</template>

<script lang="ts" setup>
    import {
        getOrg,
        getOrgChildTree,
        orgTreeSearch,
        checkReceiveSend,
        } from '@/api/itemAdmin/sendReceive';
    import SendReceive from "@/views/sendReceive/list.vue";
    //数据
	const data = reactive({
		fixedTreeRef:"",//tree实例
		isReceiveSendDept:false,
        deptId:'',
        deptName:'',
		treeApiObj:{//tree接口对象
			topLevel: getOrg,
			childLevel: {
				api:getOrgChildTree,
				params:{treeType:'tree_type_dept'}
			},
			search:{
				api:orgTreeSearch,
				params:{
					key:'',
					treeType:"tree_type_dept"
				}
			}

		},
		currTreeNodeInfo:{},//当前tree节点的信息
	})
	
	const {
		fixedTreeRef,
        isReceiveSendDept,
        deptId,
        deptName,
		treeApiObj,
		currTreeNodeInfo,
	} = toRefs(data);

	//点击tree的回调
	function onTreeClick(currTreeNode){
		currTreeNodeInfo.value = currTreeNode;
        if(currTreeNodeInfo.value.orgType == 'Department'){
             checkReceiveSend(currTreeNodeInfo.value.id).then(res => {
              isReceiveSendDept.value = res.success;
            });
            deptId.value = currTreeNodeInfo.value.id;
        }
        deptName.value = currTreeNodeInfo.value.name;
	}
</script>
<style scoped lang="scss">
    :deep(.el-descriptions__label){
		width: 14%;
		text-align: center !important;   
	
	}
	
	:deep(.el-descriptions__content){
		width: 19.3%;
		// width: 36%;
		.el-select,.el-date-editor{
			width: 100%;
		}
	}
	
	:deep(.el-descriptions__title){
		font-weight: normal;
		text-align: center;
	}
</style>
