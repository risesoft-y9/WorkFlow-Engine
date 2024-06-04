
<template>
    <router-link to="/" class="indexlayout-top-message">
      <icon-svg type="message"  :style="{ fontSize: '16px' }"></icon-svg>
      <el-badge
        class="indexlayout-top-message-badge"
        :value="message"
        type="danger"
      />
    </router-link>
</template>
<script lang="ts">
import { computed, ComputedRef, defineComponent, onMounted } from "vue";
import IconSvg from "./IconSvg";

interface RightTopMessageSetupData {
    message: ComputedRef<number>;
}

export default defineComponent({
    name: 'RightTopMessage',
    components: {
      IconSvg
    },
    setup(): RightTopMessageSetupData {

        // const store = useStore<{user: UserStateType}>();
        
        const message = computed<number>(()=> 2);


        onMounted(()=>{
            // store.dispatch("user/fetchMessage");
        })


        return {
            message
        }
    }
})
</script>
<style lang="scss" scoped>
// @import '../../assets/css/global.scss';
.indexlayout-top-message {
    height: $headerHeight;
    line-height: $headerHeight;
    /* display: inline-block; */
    display: inline;
    color: #c0c4cc;
    .indexlayout-top-message-badge {
        margin-left: -5px;
        margin-top: -20px;
        :deep(.el-badge__content) {
            border: 0;
        }
    }
}
</style>