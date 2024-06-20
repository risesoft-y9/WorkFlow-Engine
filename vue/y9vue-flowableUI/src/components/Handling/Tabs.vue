
<!-- tab栏 -->
<template>
    <div class="tabs-style">
       <div style="display: flex">
            <div class="item-style" @click="handlerClick(item, index)"
                v-for="(item, index) in tabsList" :key="index">
                <div class="name"  :class="{ 'is-active': item.name === activeIndex}">
                    <div style="writing-mode: vertical-lr;">{{ fontSizeObj.buttonSize == 'large' && item.label.length > 7 ? `${$t(item.label?.slice(0, 7))}...` : $t(item.label) }}</div>
                    <span v-if="item.number" style="height: 20px;">{{$t(item.number)}}</span>
                </div>
                <div class="triangles" :class="{ 'triangle-active': item.name === activeIndex }"></div>
            </div>
       </div>
    </div>
</template>
<script lang="ts" setup>
import { reactive, toRefs, watch, inject } from 'vue'
// 注入 字体对象
const fontSizeObj: any = inject('sizeObjInfo');
interface listTypes {
    name?: string,
    number?: number | string,
    label: string
}
interface propsTypes {
    tabsList: Array<listTypes>,
    activeIndex: string | undefined
}
let props = defineProps({
    list: {
        type: Array,
        default: () => []
    },
    activeName: {
        type: String,
    }
})

const emits = defineEmits(['tab-click'])

const data = reactive<propsTypes>({
    // 页签 数组
    tabsList: [],
    // 当前活跃值
    activeIndex: props.activeName,
})
let {
    tabsList,
    activeIndex,
} = toRefs(data);


// console.log(props.activeName, '00');


watch( () => props.list, (newVal: any) => {
   if(newVal.length){
        newVal?.map(item => {
            if(item.label.includes('(')){
                let data = item.label.split('(');
                item.label = data[0];
                item.number = `(${data[1]}`;  
            }
        })
        tabsList.value = newVal;
   }
},
{
    deep: true,
	immediate: true,
})

watch(() => props.activeName, (newVal: any) => {
    console.log(newVal, '99');
    
    activeIndex.value = newVal;
    let objInfo = {name: newVal}
     // emits
     emits('tab-click', objInfo);
})


// 切换 页签 
function handlerClick(item, index) {
    // 给 activeIndex 赋值
    activeIndex.value = item.name;
    console.log("indddddd="+index);
    
    // emits
    emits('tab-click', item);
}


</script>
<style lang="scss" scoped>
.theme-default .triangles {
  background-color: initial;
}
.tabs-style {
    display: flex;
    flex-direction: column;
    background-color: transparent;
    .item-style {
        display: flex;
        flex-direction: column;
        width: 40px;
        background-color: transparent;
        // line-height: 40px;
        margin-right: 16px;
        text-align: center;
        color: rgba(255,255,255, 0.8);
        // 13
        font-size: v-bind('fontSizeObj.baseFontSize');
        letter-spacing: 1px;
        cursor: pointer;
        .name {
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            width: 40px;
            height: 85px;
            padding: 2px 0px;
            background-color: gray;
            box-shadow: 2px 2px 3px 2px rgba(0, 0, 0, 0.25);
        }
        .triangles {
            width: 0;
            height: 0;
            border-top: 20px solid gray;
            border-bottom: 20px solid transparent;
            border-left: 20px solid transparent;
            border-right: 20px solid transparent;
            // 利用滤镜filter,添加投影
            filter: drop-shadow(3px 3px 2px rgba(0, 0, 0, .35));
        }
        .is-active {
            background-color: var(--el-color-primary);
            
        }
        
        .triangle-active {
            width: 0;
            height: 0;
            border-top: 20px solid var(--el-color-primary);
            border-bottom: 20px solid transparent;
            border-left: 20px solid transparent;
            border-right: 20px solid transparent;
        }
    }
    // hover效果
    .item-style:hover {
        .name {
            background-color: var(--el-color-primary) !important;
        }
        .triangles {
            border-top: 20px solid var(--el-color-primary);
        }
    }
}
</style>
  