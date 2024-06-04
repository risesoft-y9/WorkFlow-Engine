<template>
    <el-container>
      <el-header height='45px' style="padding:0px">
        <div class="search">
          <span>搜索:</span>
          <el-input type="text" v-model="searchKey" @keyup.enter="searchFunc" class="w-50 m-2" clearable/>
          <el-button type="primary" @click="searchFunc"><i class="ri-search-line"></i></el-button>
          <el-button @click="refresh"><i class="ri-refresh-line"></i></el-button>
        </div>
        <el-divider></el-divider>
      </el-header>
      <el-main class='treeMain'>
        <div class="tree-region"><Tree :setting="setting" :isExpandAll="isExpandAllRef"></Tree></div>
      </el-main>
      <!-- <el-footer height='45px' class='submitForm'>
        <el-divider></el-divider>
        <el-button-group>
          <el-button type="primary" @click="submitForm( )">保存</el-button>
          <el-button @click="close( )">取消</el-button>
        </el-button-group>
      </el-footer> -->
    </el-container>
</template>

<script lang='ts' setup>
import { ref,defineExpose,defineProps,defineEmits,reactive } from 'vue'
import Tree from "@/components/tree/y9Tree.vue"
import type { ElMessage,ElMessageBox ,ElLoading} from 'element-plus'
// 在 axios - API 定义树组件的接口（一级节点接口、二级节点接口）并导入
import { getDeptTree,getDeptChildTree, deptTreeSearch } from "@/api/itemAdmin/entrust"

const emits = defineEmits(['org-click']);
const searchName = ref('');
const isExpandAllRef = ref(false)
let personIds = ref([])
const props = defineProps({
    refreshTable: Function,
    type: String
})

const setting = reactive({
    treeId: 'org',  // 树id
   itemInterface: {
      api: getChildById,
      params:{},
      callback: (data) => {   // 二（三）级节点接口 数据的回调函数
        console.log("callback = ",data);
        return data;    // 如果有回调，则需要返回data - [Array类型]
      }
    },
    itemGroupPrefix: 'itemGroup-',  // 有 children节点的 ID 前缀（ 完整示例：'itemGroup-' + item.id ）
    data: [],   // 一级接口接口返回的源数据
    itemInfo: {
        keys:{  // 数据字段映射
            id: "id",   // id
            parentId: "parentId", // parentId
            name: "name",   // name
            children: "children", // children
            hasChild: "hasChild",   // haschild
            title: "name",     // title 映射接口数据的 name 示例
            subTitle: "name",     // subtitle 映射接口数据的 name 示例
            // 以下字段 如果有，则显示映射的值，否则不显示
            checkbox: "checkbox",   // 复选框 实现每个 item 个性化显示复选框
            add_icon: "add_icon",   // 实现每个 item 个性化显示 增 事件
            delete_icon: "delete_icon", // 实现每个 item 个性化显示 删 事件
            edit_icon: "edit_icon",     // 实现每个 item 个性化显示 改 事件
            select_icon: "select_icon",  // 实现每个 item 个性化显示 查 事件
            title_icon: "title_icon",    // 实现每个 item 个性化显示 icon
            click_title_event:true
        },
        render: {
            checkbox: {          // 复选框
                func: (data) => {
                    // 复选框事件
                    //console.log("点击了复选框", data, data.checked?"勾选状态":"取消勾选状态") 
                    if(data.checked){
                        personIds.value.push(data.id);
                        emits('org-click', personIds);
                    }else{
                        for (let index = 0; index < personIds.value.length; index++) {
                            if( personIds.value[index]==data.id){
                                personIds.value.splice(index,1);
                            }
                        }
                    }
                    
                }
            },    
            add_icon: {         // 增
                text: '',   // 设置 add_icon文本，空则不渲染
                func: (data) => {
                    // 设置 add_icon 点击事件
                    console.log("点击了 add_icon", data) 
                }  
            },  
            delete_icon:{         // 删
                text: '',   // 设置 delete_icon文本，空则不渲染
                func: (data) => {
                    // 设置 delete_icon 点击事件
                    console.log("点击了 delete_icon", data) 
                }  
            }, 
            edit_icon: {         // 改
                text: '',   // 设置 edit_icon文本，空则不渲染
                func: (data) => {
                    // 设置 edit_icon 点击事件
                    console.log("点击了 edit_icon", data) 
                }  
            }, 
            select_icon: {         // 查
                text: '',   // 设置 select_icon文本，空则不渲染
                func: (data) => {
                    // 设置 select_icon 点击事件
                    console.log("点击了 select_icon", data) 
                }  
            },
             mouse_over: {   // 鼠标悬停事件
                func: (e) => {
                    const li = e.originalTarget;
                    //if (li.className === "treeItem") {
                        //li.style.backgroundColor = "#E7ECED";
                   // }
                }
            },
            mouse_leave: {  // 鼠标离开事件
                func: (e) => {
                    //const li = e.originalTarget;
                    //li.style.backgroundColor = "";
                }
            },
            click: {
                // 单击事件
                func: (data) => {
                    // console.log('单击事件', data);
                    
                },
            },
            dbl_click: {
                // 双击事件
                func: (data) => {
                    // console.log('双击事件', data);
                },
            },
            click_title_event: {    
                func: (data) => {
                    //getOrgBaseById(data.dataset);
                    const org = data.dataset;
                    if(org.orgType=='Person'){
                         emits('org-click', org.id, org.name);
                    }
                   
                }
            }
        }
    },
    events: {
        search: {   // 搜索功能
            api: deptTreeSearch,
            params: {
                name:'',
            },
            callback: (data) => {
                /*
                    设置搜索结果的数据 注意保存原有数据
                */
                   treeSearchCallback(data);

                   //setting.data = data;
            }
        }
    },
    style: {
        li: {   // li activeClass 
            activeClassName: useCssModule('classes').active
        },
        animation: {
            in: "fadeInLeftBig",
            out: "fadeOutRight"
        } 
    }
})

async function getChildById(params) {
    const child = await getDeptChildTree(params.parentId);
    child.data.forEach(element => {
        //element.edit_icon = true;
        //element.delete_icon = true;
        
        if(element.orgType == 'Person'){
            element.title_icon = 'ri-women-line';
            if(props.type =='sendReceive'){
                element.checkbox = true;
            }
            
            if(element.sex == 0){
                element.title_icon = 'ri-men-line';
            }
        }else if(element.orgType == 'Position'){

        }else if(element.orgType == 'Department'){
            element.hasChild = true;
            //element.checkbox = true;
            element.title_icon = 'ri-slack-line';
        }
    })
    return child;
}

defineExpose({
    getTree
})
// 树组件 - 一级节点接口数据
// axios API 使用一级节点渲染树组件
async function getTree() {
    personIds.value = [];
    const tree = await getDeptTree();
    tree.data.map((item) => {
        item.hasChild = true;
        item.title_icon = 'ri-stackshare-line';
        // item.add_icon = true;
        //item.edit_icon = true;
        //item.delete_icon = true;
        //item.select_icon = true;
        
    })
   
    setting.data = tree.data
     
    // isExpandAllfunc();
    setTimeout(() => {
       // isExpandAllfunc();
       // 默认展开第一个子节点
       Array.from(document.getElementById("org").childNodes[1].childNodes[0].children).map(item => {
            if (item.dataset && item.dataset.itemEvent === "clickDropDownEvent" ) {
            return item.click()
            }
        })
    }, 500);
}

getTree()

async function treeSearchCallback(data){
    const deepTraversal = async (itemList) => {
        itemList.forEach(element => {
            if(element.orgType == 'Position'){
                element.title_icon = 'ri-shield-user-line';
                //element.checkbox = true;
            }else if(element.orgType == 'Person'){
                element.title_icon = 'ri-women-line';
                if(element.sex == 1){
                    element.title_icon = 'ri-men-line';
                }
                //element.checkbox = true;
            }else if(element.orgType == 'Department'){
                element.hasChild = true;
                element.title_icon = 'ri-slack-line';
            }else if(element.orgType == 'Organization'){
                element.hasChild = true;
                element.title_icon = 'ri-stackshare-line';
            }
            if (element.children) {
                deepTraversal(element.children);
            }
        });
    }
    deepTraversal(data);
    setting.data = data;
}

async function refresh(){
    getTree()
    searchName.value = '';
}

async function close(){
}

async function submitForm(){
    if(personIds.value.length==0){
        ElMessage({message: "请选择要设置管理员的人员！",type: 'error',offset:65,})
        return false;
    }
}

const searchKey = ref("")
function searchFunc() {
    setting.events.search.params.name = searchKey.value;
}

function isExpandAllfunc() {
    if (!isExpandAllRef.value) {
        isExpandAllRef.value = true
    }else{
        isExpandAllRef.value = false
    }
}
function collapseAllFunc() {
    if(isExpandAllRef.value){
        isExpandAllRef.value = false
    }
}
</script>
<style scoped lang="scss">
.tree-region{
    background-color: var(--el-bg-color);  // 
    padding: 20px;
}

$lineHeight_25: "25px";
$lineHeight_32: "32px";
$color_text: #ffffff;
$color_btn_bg: #586cb1;

@mixin layout($display: flex, $justifyContent: left, $align-items: center) {
  display: $display;
  justify-content: $justifyContent;
  align-items: $align-items;
}
.y9_card {
  height: calc(100vh - 102px);
  border-radius: 2px;
}
.el-button + .el-button {
  margin-left: 0px;
}
.el-divider--horizontal {
  margin: 5px 0;
}

.search {
  @include layout;
  line-height: 36px;

  span {
    min-width: 42px;
  }
  input {
    max-width: 40%;
    font-size: 14px;
    line-height: 1.7;
    border: 1px solid #e6e6e6;
    border-radius: 3px;
  }
  button {
    //color: $color_text;
    //background-color: $color_btn_bg;
    //border: 0;
    border-radius: 3px;
    padding: 8px 9px;

    &:first-of-type {
      margin: 0 4px;
    }
  }
}

.treeMain {
  height: 600px;
  padding: 0px;
}
.newOrModify {
  .el-dialog__body {
    padding: 0px;
  }
}

.submitForm {
  padding: 0px;
  text-align: center;
}
</style>