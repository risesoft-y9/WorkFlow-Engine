
<template>
	<y9Card :showHeader="showHeader">
		<template #header>
			<y9Filter
				:itemList="itemList"
				:filtersValueCallBack="filtersValueCallBack"
				itemMarginBottom="0px"
			>
				<template #refresh>
					<el-button @click="onRefreshTree" class="global-btn-second" :size="fontSizeObj.buttonSize"
						:style="{ fontSize: fontSizeObj.baseFontSize }">
						<i class="ri-refresh-line"></i>
						<span>{{ $t('刷新') }}</span>
					</el-button>
				</template>

				<template #rightBtn>
					<slot name="treeHeaderRight"></slot>
				</template>
			</y9Filter>
		</template>

		<y9Tree :setting="orgTreeStore.getOrgTreeSetting" :isExpandAll="isExpandAllTree"></y9Tree>
	</y9Card>

</template>

<script lang="ts" setup>
import {inject} from 'vue';
import { useSettingStore } from "@/store/modules/settingStore"
import { useOrgTreeStore } from "@/store/modules/orgTreeStore"
import { $dataType, $deepAssignObject, $deeploneObject } from '@/utils/object'//工具类 
import { onMounted, useCssModule } from 'vue';
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo'); 
const props = defineProps({
	treeApiObj: {//tree接口对象,参数名称请严格按照以下注释进行传参。
		type: Object,
	},
	setting: {//tree的配置
		type: Object,
	},
	showHeader:Boolean,
	selectedData: {//选择的数据，可以通过双向绑定的方式获取选择的数据
		type: [Array,Object],
	},
})


const emits = defineEmits(['onTreeClick']);

const data = reactive({

	itemList: [//过滤列表
		{
			type: "slot",
			slotName: "refresh",
			span: 4,
		},
		{
			type: "search",
			span: 12,
			key: "name"

		},
	],

	filterValue: {},//当前过滤值

	currTreeNodeInfo: {}, //当前tree节点的信息

	isExpandAllTree: false,//是否展开tree
	newSelectedData:[],

})

let {
	itemList,
	filterValue,
	y9TreeRef,
	currTreeNodeInfo,
	isExpandAllTree,
	newSelectedData
} = toRefs(data);


// 初始化orgTreeStore
let orgTreeStore = useOrgTreeStore();



orgTreeStore.$patch((state) => {
	//初始化配置
	state.orgTreeSetting = $deeploneObject(state.initOrgTreeSetting);
	state.orgTreeSetting.style.li.activeClassName = useCssModule('classes').active
	if (props.treeApiObj) {
		if (props.treeApiObj.childLevel) {
			state.orgTreeSetting.itemInterface.api = props.treeApiObj.childLevel.api;//子级tree接口
			state.orgTreeSetting.itemInterface.params = props.treeApiObj.childLevel.params;//子级tree接口参数
		}
		if (props.treeApiObj.search) {//搜索
			state.orgTreeSetting.events.search.api = props.treeApiObj.search.api;//搜索api
			state.orgTreeSetting.events.search.params = props.treeApiObj.search.params;//搜索参数
			state.orgTreeSetting.events.search.callback = onSearchResultCallback;//搜索结果回调
		}
	}

	state.orgTreeSetting.itemInfo.render.checkbox.func = onCheckBox; //点击复选框
	state.orgTreeSetting.itemInfo.render.click.func = onTreeClick; //点击树
});

//初始化tree
async function initTree() {
	try {
		props.treeApiObj && await orgTreeStore.getTree(props.treeApiObj.topLevel); //获取一级tree数据
		if (orgTreeStore.getOrgTreeSetting.data.length > 0) {

			onTreeClick({ dataset: JSON.stringify(orgTreeStore.getOrgTreeSetting.data[0]) });//默认选中第一项

			//默认给第一项添加选中样式
			const hashClassName = orgTreeStore.$state.orgTreeSetting.style.li.activeClassName

			const treeId = orgTreeStore.$state.orgTreeSetting.treeId;

			document.getElementById(treeId).childNodes[1].childNodes[0].className += " " + hashClassName;

			// 默认展开第一个子节点
			Array.from(document.getElementById(treeId).childNodes[1].childNodes[0].children).map(item => {
				if (item.dataset && item.dataset.itemEvent === "clickDropDownEvent") {
					return item.click()
				}
			})

		}

	} catch (err) {

		console.log(err)

	}

}

//initTree(); //初始化tree

watch(()=>props.selectedData,(newVal) => {
			if(newVal){
				
				newSelectedData.value = newVal;
			
			}
		},
		{
		  deep: true,
		  immediate: true,
		}
)

watch(
	() => props.setting,
	(newVal) => {
		if (newVal) {

			orgTreeStore.$state.orgTreeSetting = $deepAssignObject(orgTreeStore.$state.orgTreeSetting, newVal);//深度合并

		}
	},
	{
		deep: true,
		immediate: true,
	}
)


//刷新Tree
async function onRefreshTree() {
	//清空选项样式
	const treeId = orgTreeStore.$state.orgTreeSetting.treeId;

	const div = document.getElementById(treeId);

	for (let i = 0; i < div.childNodes.length; i++) {

		const item = div.childNodes[i];

		for (let j = 0; j < item.childNodes.length; j++) {

			const item2 = item.childNodes[j];

			const node = item.childNodes[0];

			if (node.nodeName == 'LI' && node.className.includes('treeItem')) {

				node.className = 'treeItem';

			}
		}
	}

	isExpandAllTree.value = false;

	initTree();
}

//点击复选框
	function onCheckBox(data){
		if(data.checked){
			newSelectedData.value.push(data.dataset)
		}else {
			newSelectedData.value.forEach((item,index) => {
				if(item.id == data.dataset.id){
					newSelectedData.value.splice(index,1)
				}
			})
		}
		const ids = newSelectedData.value.map(item => {
			return item.id
		})
		emits('update:onCheckBox',newSelectedData)
	}

//过滤值回调
function filtersValueCallBack(filters) {
	filterValue.value = filters;
}

//监听过滤值改变，请求搜索接口
watch(
	() => filterValue.value,
	(newVal, oldVal) => {

		if (newVal.name && newVal.name != "" && props.treeApiObj) {

			orgTreeStore.$state.orgTreeSetting.events.search.params.key = newVal.name;

		} else if (newVal.name != oldVal.name) {

			orgTreeStore.$state.orgTreeSetting.events.search.params.key = "";

			onRefreshTree();

		}

	},
	{
		deep: true,
	}
)

watch(() => props.treeApiObj.topLevel,(newVal, oldVal) => {
		orgTreeStore.$state.orgTreeSetting.data = [];
		initTree();
	},
	{
		deep: true,
	}
)

watch(() => props.treeApiObj.childLevel.api,(newVal, oldVal) => {
		orgTreeStore.$state.orgTreeSetting.itemInterface.api = props.treeApiObj.childLevel.api;//子级tree接口
	},
	{
		deep: true,
	}
)

watch(() => props.treeApiObj.search.api,(newVal, oldVal) => {
		orgTreeStore.$state.orgTreeSetting.events.search.api = props.treeApiObj.search.api;//搜索tree接口
	},
	{
		deep: true,
	}
)

//搜索结果回调
async function onSearchResultCallback(data) {
	orgTreeStore.$patch((state) => {
		const deepTraversal = async (itemList) => {
			itemList.forEach(element => {
				element.newName = element.name;
				// console.log(element, "element");
				switch (element.orgType) {
					case 'Person':
						element.title_icon = 'ri-women-line';
						if (element.sex == 1) {
							element.title_icon = 'ri-men-line';
						}
						if (!element.original) {
							if (element.sex == 1) {
								element.title_icon = 'ri-men-fill';
							} else {
								element.title_icon = 'ri-women-fill';
							}
						}
						element.checkbox = true;
						break;
					case 'Department':
						element.title_icon = 'ri-slack-line';
						let treeType = props.treeApiObj.childLevel.params.treeType;
						if(treeType == 'Department'){
							element.checkbox = true;
						}
						break;
					case 'Position':
						element.title_icon = 'ri-shield-user-line';
						element.checkbox = true;
						break;
					case 'Organization':
						element.title_icon = 'ri-stackshare-line';
						let treeType1 = props.treeApiObj.childLevel.params.treeType;
						if(treeType1 == 'Department'){
							//element.checkbox = true;
						}
						break;
				}

				// 角色
				switch(element.type){
					
					case 'role'://角色 人员
						element.title_icon = 'ri-women-line';
						break;
					
					case 'folder': // 文件夹
						element.title_icon = 'ri-folder-2-line';
						break;
								
				}

				if (element.children) {

					deepTraversal(element.children);

				}

			});

		}

		deepTraversal(data);

		state.orgTreeSetting.data = data;

	});

}

//点击Tree
function onTreeClick(data) {
	if ($dataType(data.dataset) == 'string') {

		currTreeNodeInfo.value = JSON.parse(data.dataset);

	} else if ($dataType(data.dataset) == 'object') {

		currTreeNodeInfo.value = data.dataset;

	}

	emits('onTreeClick', currTreeNodeInfo);
}

defineExpose({
	onRefreshTree,//刷新tree
})



//固定模块样式
// 在 fiexHeader 变化时 监听滚动事件，改变className
const settingStore = useSettingStore()
const isFiexHeader = computed(() => settingStore.getFixedHeader)
function listener() {
	const elementId = "fixedDiv"
	const scroll_Y = window.scrollY
	if (scroll_Y > 100 && document.getElementById(elementId)) {
		document.getElementById(elementId).className = 'fixed-after-scroll'
	}
	if (scroll_Y < 100 && document.getElementById(elementId)) {
		document.getElementById(elementId).className = 'fixed'
	}
}

watch(isFiexHeader, (newV, oldV) => {
	// console.log(newV, oldV);

	if (!newV) {
		window.addEventListener('scroll', listener, false)
	}
	else {
		// 移除监听
		window.removeEventListener('scroll', listener, false)
	}

})

onMounted(() => {
	const layout = computed(() => settingStore.getLayout)
	if (layout.value === "Y9Horizontal") {
		document.getElementById("fixedDiv").className = 'fixed fixed-herder-horizontal'
	}
	if (!isFiexHeader.value) {

		window.addEventListener('scroll', listener, false)
	}
	watch(layout, (newV, oldV) => {
		if (newV === "Y9Horizontal") {
			document.getElementById("fixedDiv").className = 'fixed fixed-herder-horizontal'
		} else {
			document.getElementById("fixedDiv").className = 'fixed'
		}
	})
})
</script>

<style lang="scss" scoped>
@import "@/theme/global.scss";
@import "@/theme/global-vars.scss";
@import "@/theme/global.scss";

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
	margin-left: calc(26.6vw + 35px);
	:deep(.y9-card) {
		margin-bottom: 35px;
	}

	:deep(.y9-card:last-child) {
		margin-bottom: 0px;
	}
}

/* 固定左侧树 */
.fixed {
	position: fixed;
	width: 26.6vw;
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
	background-color: var(--el-color-primary-light-4) !important;
	// color: var(--el-color-white) !important;

	/* 改变icon的颜色，没有效果 */
	& > .info > :last-child > div > i {
		color: var(--el-color-white) !important;
	}
}
</style>