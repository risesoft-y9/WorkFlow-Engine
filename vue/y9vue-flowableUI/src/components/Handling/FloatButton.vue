
<!-- 操作按钮 -->
<template>
    <div class="operation-style" v-if="btnList.length">
        <div class="btn-style">
            <div class="item-style" 
                    v-for="(item, index) in btnList" :key="index">
                <el-popover v-if="item.slot || item.render" @show="handlerShowPopover(index)"
                  @hide="handlerHidePopover"
                  @click="handlerClickPopover"
                    placement="left" trigger="hover" popper-class="popper-content">
                    <template #reference>
                        <div>
                            <div :class="{ 'group-btn': index !== -1, 'popover-show': popoverShow === index ,
                        'group-btn-large': fontSizeObj.buttonSize == 'large' && item.name?.length > 3}">
                                <el-button :size="fontSizeObj.buttonSize"
                                        :style="{ fontSize: fontSizeObj.baseFontSize }"> 
                                    <i :class="item.icon" ></i>
                                    <span class="hover-name">{{ $t(item.name) }}</span>
                                </el-button>
                            </div>
                       </div>
                    </template>
                    <!-- 插槽或函数渲染 -->
                    <template #default >
                        <slot v-if="item.slot" :name="item.slot"  ></slot>
                        <Render v-if="item.render" :render="item.render"></Render>
                    </template>
                </el-popover>
                <div v-else @click="item?.onClick">
                    <div :class="{ 'group-btn': index !== -1, 'popover-show': popoverShow === index,
                'group-btn-large': fontSizeObj.buttonSize == 'large' && item.name?.length > 3 }">
                        <el-button  :size="fontSizeObj.buttonSize"
                        :style="{ fontSize: fontSizeObj.baseFontSize}">
                            <i :class="item.icon"></i>
                            <span class="hover-name" >{{ $t(item.name) }}</span>
                        </el-button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { reactive, toRefs, watch, ref, inject } from 'vue'
import { $deeploneObject, } from '@/utils/object'
import Render from '@/utils/render.vue'
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo'); 
let props = defineProps({
    list: {
        type: Array,
        default: () => []
    }
})

interface listTypeof {
    name?: string | undefined,
    icon?: string,
    slot?: string,
    render?: Function,
    onClick?: Function
}

const btnList = ref<Array<listTypeof>>([])

const data = reactive({
    // 默认的数组值 '返回', '加减签', '收回', '抄送', '打印
    // btnList: [
        // {
        //     name: '返回', // 按钮名称,
        //     render: () => {
        //         return h('div',{}, [
        //             h('span', {
        //                 onclick: () => {
        //                     console.log(333);
                            
        //                 },
        //             }, '增签'),
        //             h('span', {
        //                 onclick: () => {
        //                     console.log(222);
                            
        //                 }
        //             }, '减签是成绩单丝偶发VN手动蝶阀'),
        //         ])
        //     }
            
        // },
        // {
        //     name: '加减签', // 按钮名称,
        //     // slot: 'slotName'
        // },
        // {
        //     name: '收回', // 按钮名称,
            
        // },
        // {
        //     name: '抄送', // 按钮名称,
        //     onClick: () => {
        //          console.log('抄送');
                 
        //     }, // 按钮的点击
        // },
        // {
        //     name: '打印', // 按钮名称,
        //     icon: 'ri-star-line', // 图标
        // }
    // ],
    // 弹框出现
    popoverShow: null
})
let {
    // btnList,
    popoverShow,
} = toRefs(data);


watch( () => props.list, (newVal: any) => {
    if(newVal.length){
        // 传过来数组
        btnList.value = newVal;
    }
},
{
    deep: true,
	immediate: true,
})

// 出现弹框
function handlerShowPopover(index) {
    popoverShow.value = index;
}

// 隐藏弹框
function handlerHidePopover() {
    popoverShow.value = null;
}

// 点击弹框
function handlerClickPopover() {
    console.log(333);
    
}

</script>
<style lang="scss" scoped >

.operation-style {
    background-color: transparent;
    width: 120px;
    height: 100%;
    display: flex;
    justify-content: right;
    position: relative;
    .btn-style {
        text-align: right;
        .item-style {
            box-sizing: border-box;
            margin-bottom: 16px;
            //border: 2px solid gray;
            border-radius: 5px;
            background-color: transparent;
            width: 120px;
            :deep(.el-button) {
                height: 26px;
                color: var(--el-color-primary);
                background-color: var(--el-color-primary-light-9);
                padding: 20px 20px;
                width: 100px;
                border: none;
                border-radius: 5px;
                i {
                    font-size: v-bind('fontSizeObj.largeFontSize');
                }
                .hover-name {
                    margin-left: 10px;
                    // display: none;
                }
            }
        }
        
        .item-style:nth-child(1){
            :deep(.el-button) {
                .hover-name {
                    display: inline-block;
                }
            }
        }
        .group-btn {
            :deep(.el-button) {
                width: 100px;
            }
        }
        .group-btn-large {
            :deep(.el-button) {
                width: 112px;
            }
        }
        .group-btn:hover {
            :deep(.el-button) {
                width: 120px;
                .hover-name {
                    display: inline-block;
                }
            }
        }
        .group-btn-large:hover {
            :deep(.el-button) {
                width: 120px;
                .hover-name {
                    display: inline-block;
                }
            }
        }
        .popover-show {
            :deep(.el-button) {
                width: 120px;
                .hover-name {
                    display: inline-block;
                }
            }
        }
    }
}

</style>

<style>
.el-popover.popper-content {
    border-radius: 5px;
    padding: 0px;
    /* width: 100px !important; */
    min-width: 80px !important;
    /* max-width: 150px !important; */
    width: auto !important;
    text-align: center;
    letter-spacing: 2px;
    box-shadow: 2px 2px 2px 1px rgba(0,0,0,0.06);
    background-color: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
    font-weight: 600;
}

.el-popover.popper-content div {
    display: flex;
    flex-direction: column;
}

.el-popover.popper-content div span {
    width: 100%;
    cursor: pointer;
    display: inline-block;
    height: 30px;
    line-height: 30px;
    padding: 0 30px;
    user-select:none;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}
.el-popover.popper-content div span:hover{
    background-color: var(--el-color-primary-light-8);
}

.popper-content[x-placement^=right] > .popper__arrow::after{
    border-right-color: var(--el-color-primary-light-9)!important;
}

.el-popper.popper-content.is-light .el-popper__arrow::before {
    background-color: var(--el-color-primary-light-9);
    width: 15px;
    height: 15px;
    top: -2px;
    right: -2px;
}

</style>
  